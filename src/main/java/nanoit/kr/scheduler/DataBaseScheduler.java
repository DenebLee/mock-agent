package nanoit.kr.scheduler;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.TemporaryQueue;
import nanoit.kr.domain.message.Send;
import nanoit.kr.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DataBaseScheduler {
    private final ScheduledExecutorService scheduledExecutorService;
    private final MessageService messageService;
    private final TemporaryQueue queue;
    private List<Send> selectList;

    public DataBaseScheduler(MessageService messageService, TemporaryQueue queue) {
        this.messageService = messageService;
        this.queue = queue;
        this.selectList = new ArrayList<>();
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }


    public Runnable task = new Runnable() {
        @Override
        public void run() {
            try {
                selectList = messageService.selectAll();
                for (Send send : selectList) {
                    if (queue.publish(send)) {
                        log.debug("[SCHEDULER] SELECT DATA FROM TABLE SUCCESS !!! number Imported : [{}]", selectList.size());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                shutdownScheduler();
                log.error("[SCHEDULER] SHUTDOWN -> {}", e.getMessage());
            }
        }
    };

    public void start() {
        scheduledExecutorService.scheduleWithFixedDelay(task, 1, 3, TimeUnit.MICROSECONDS);
    }

    public boolean isSchedulerShutdown() {
        return scheduledExecutorService.isShutdown();
    }

    public void shutdownScheduler() {
        this.scheduledExecutorService.shutdownNow();
    }


}
