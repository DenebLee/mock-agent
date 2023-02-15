package nanoit.kr.module;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ModuleProcessManagerImpl {

    private final Map<String, ModuleProcess> objectMap = new ConcurrentHashMap<>();
    private final Map<String, Thread> threadMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private static final long DEAD_LINE = 3 * 60 * 1000L; // 1000 * 60 * 3 = 3분


    public ModuleProcessManagerImpl() {
        scheduledExecutorService.scheduleAtFixedRate(this::monitor, 1000, 1000, TimeUnit.MILLISECONDS);
        monitor();
    }


    private void monitor() {
        for (String key : objectMap.keySet()) {
            ModuleProcess process = objectMap.get(key);
            if (!threadMap.containsKey(key) && process.status == ModuleProcess.Status.INIT) {
                Thread thread = new Thread(process, key);
                thread.start();
                process.status = ModuleProcess.Status.RUN;
                threadMap.put(key, thread);
            }
        }

        // hreadMap을 반복하는 동안 반복자(iterator)를 사용하도록 변경
        // 이렇게 하면 threadMap을 동시에 수정하는 상황이 발생하지 않음

        Iterator<Map.Entry<String, Thread>> iterator = threadMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Thread> entry = iterator.next();
            String key = entry.getKey();
            Thread thread = entry.getValue();
            ModuleProcess process = objectMap.get(key);

            if (process == null) {
                if (interruptThread(key)) {
                    iterator.remove();
                }
            } else {
                Thread.State state = thread.getState();
                if (state == Thread.State.TERMINATED) {
                    iterator.remove();
                    Thread newThread = new Thread(process, key);
                    newThread.start();
                    threadMap.put(key, newThread);
                } else if (state == Thread.State.BLOCKED) {
                    long deadLine = calculateDeadLine(key);
                    if (isSetCurrentTime(key) && isOverDeadLine(key, deadLine)) {
                        thread.interrupt();
                        if (thread.getState() != Thread.State.TERMINATED) {
                            process.shoutDown();
                        }
                    }
                }
            }
        }
    }

    public int getObjectMapSize() {
        log.info("[@THREAD:MANAGER:SCHEDULER@] OBJECT-MAP SIZE => {}", objectMap.size());
        return objectMap.size();
    }

    public int getThreadMapSize() {
        log.info("[@THREAD:MANAGER:SCHEDULER@] THREAD-MAP SIZE => {}", threadMap.size());
        return threadMap.size();
    }

    public void register(ModuleProcess... modules) {
        if (modules == null) {
            return;
        }
        for (ModuleProcess moduleProcess : modules) {
            if (moduleProcess.getUuid() == null || objectMap.containsKey(moduleProcess.getUuid())) {
                return;
            }
        }
        for (ModuleProcess moduleProcess : modules) {
            objectMap.put(moduleProcess.getUuid(), moduleProcess);
            Thread thread = new Thread(moduleProcess);
            threadMap.put(moduleProcess.getUuid(), thread);
            thread.start();
        }
    }

    public boolean unregister(String uuid) {
        if (uuid == null || !objectMap.containsKey(uuid)) {
            return false;
        }
        objectMap.remove(uuid);
        if (!interruptThread(uuid)) {
            return false;
        }
        interruptThread(uuid);
        threadMap.remove(uuid);
        return true;
    }

    public long runningThreadCount() {
        return threadMap.entrySet().stream()
                .filter(stringThreadEntry -> stringThreadEntry.getValue().getState() != Thread.State.TERMINATED).count();
    }

    public boolean interruptThread(String uuid) {
        if (uuid == null) {
            return false;
        }
        return threadMap.get(uuid).getState() == Thread.State.TERMINATED;
    }

    public void shutDown() {
        scheduledExecutorService.shutdown();
    }


    private long getCurrentTime(String uuid) {
        return objectMap.get(uuid).lastRunningTime;
    }

    public boolean isOverDeadLine(String key, long deadLine) {
        return (System.currentTimeMillis() - getCurrentTime(key)) > deadLine;
    }

    public long calculateDeadLine(String key) {
        return DEAD_LINE + (getCurrentTime(key) * 1000L);
    }

    private boolean isSetCurrentTime(String key) {
        return objectMap.get(key).lastRunningTime > 0;
    }

    public boolean isSuccessToStart() {
        return runningThreadCount() == 2 && objectMap.size() == 2 && threadMap.size() == 2;
    }
}
