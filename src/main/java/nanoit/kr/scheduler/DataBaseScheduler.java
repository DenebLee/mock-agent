package nanoit.kr.scheduler;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.domain.message.Send;
import nanoit.kr.exception.SelectFailedException;
import nanoit.kr.queue.InternalDataType;
import nanoit.kr.queue.InternalQueueImpl;
import nanoit.kr.service.MessageService;
import nanoit.kr.service.before.MessageServiceBefore;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DataBaseScheduler {
    private final ScheduledExecutorService scheduledExecutorService;
    private final MessageService messageService;
    private final InternalQueueImpl queue;

    public DataBaseScheduler(InternalQueueImpl queue) {
        this.messageService = MessageService.;
        this.queue = queue;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    public void start() {
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                List<Send> selectList = messageService.selectAll();
                for (Send send : selectList) {
                    if (send == null) {
                        throw new SelectFailedException("[SCHEDULER] Data Select from DB Error");
                    }
                    if (!sendToMapperQueue(selectList)) {
                        throw new RuntimeException();
                    }
                }
            } catch (Exception e) {
                log.error("[SCHEDULER] Exception occurred in the scheduler: {}", e.getMessage(), e);
                shutdownRestartScheduler();
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    public boolean isSchedulerShutdown() {
        return scheduledExecutorService.isShutdown();
    }

    public void shutdownRestartScheduler() {
        scheduledExecutorService.shutdown();
        try {
            // awaitThermination
            // ExecutorService가 종료될 때까지 대기
            //  해당 메소드를 호출하여 모든 작업 완료되고 ExecutorService 가 종료될 때까지 기다릴 수있다

            if (!scheduledExecutorService.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduledExecutorService.shutdownNow();
                if (!scheduledExecutorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    log.error("[SCHEDULER] Failed to shutdown scheduler");
                }
            }
            log.debug("[SCHEDULER] Scheduler is shutdown");
        } catch (InterruptedException e) {
            scheduledExecutorService.shutdownNow();
            Thread.currentThread().interrupt();
            log.error("[SCHEDULER] Shutdown of scheduler interrupted: {}", e.getMessage(), e);
        } finally {
            start();
        }
    }

    public boolean sendToMapperQueue(List<Send> sendList) {
        if (sendList == null) {
            return false;
        }
        if (queue.publish(InternalDataType.SENDER, sendList)) {
            log.debug("[SCHEDULER] SELECT DATA FROM TABLE SUCCESS !!! number Imported : [{}]", sendList.size());
            return true;
        }
        return false;
    }
}
