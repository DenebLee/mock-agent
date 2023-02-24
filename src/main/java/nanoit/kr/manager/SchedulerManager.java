package nanoit.kr.manager;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.scheduler.DataBaseScheduler;
import nanoit.kr.unclassified.GlobalConstant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SchedulerManager {
    private Map<String, DataBaseScheduler> schedulerManagerMap;
    private final ScheduledExecutorService scheduledExecutorService;

    public SchedulerManager() {
        this.schedulerManagerMap = new ConcurrentHashMap<>();
        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
    }

    public void start() {
        log.info("[SCHEDULER_MGR] Scheduler manager Start");
        scheduledExecutorService.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
    }

    private Runnable task = () -> {
        try {
            for (Map.Entry<String, DataBaseScheduler> entry : schedulerManagerMap.entrySet()) {
                if (entry.getValue().isShedulerState() || entry.getValue().isSchedulerShutdown()) {
                    entry.getValue().shutdownRestartScheduler();
                }
                // socket 통신 중 비정상적으로 끊겼을 경우
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public boolean registeScheduler(String key, DataBaseScheduler dataBaseScheduler) {
        if (key == null || dataBaseScheduler == null) {
            log.warn("[SCHEDULER-MGR] Request values are null");
            return false;
        }
        if (schedulerManagerMap.containsKey(key) || schedulerManagerMap.containsValue(dataBaseScheduler)) {
            log.warn("[SCHEDULER-MGR] Request value already contain SchedulerMap");
            return false;
        }
        log.info("[SCHEDULER-MGR] Scheduler regist Complete key : [{}]", key);
        schedulerManagerMap.put(key, dataBaseScheduler);
        return true;
    }

    private boolean unregisteScheduler(String key) {
        if (key == null) {
            return false;
        }
        if (!schedulerManagerMap.containsKey(key)) {
            log.warn("[SCHEDULER-MGR] Request key is not contain SchedulerMap");
            return false;
        }
        log.info("[SCHEDULER-MGR] Scheduler Unregist Complete key : [{}]",key);
        schedulerManagerMap.remove(key);
        return true;
    }
}
