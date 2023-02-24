package nanoit.kr.scheduler;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nanoit.kr.db.DatabaseHandler;
import nanoit.kr.domain.message.Send;
import nanoit.kr.exception.SelectFailedException;
import nanoit.kr.queue.InternalQueueImpl;
import nanoit.kr.repository.MessageRepository;
import nanoit.kr.service.MessageService;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DataBaseScheduler {
    private final ScheduledExecutorService scheduledExecutorService;
    private final MessageService messageService;
    private final DatabaseHandler databaseHandler;
    private final MessageRepository repository;
    @Getter
    private final InternalQueueImpl queue;
    @Getter
    private boolean shedulerState = false;
    private final String key;


    public DataBaseScheduler(String key, MessageRepository repository, InternalQueueImpl queue) {
        this.key = key;
        this.queue = queue;
        this.databaseHandler = new DatabaseHandler();
        this.repository = repository;
        this.messageService = databaseHandler.getMessageService1();
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);

        start();
    }

    // 하나의 스케줄러는 여러개의 계정에 데이터를 보낼 수 있다 .

    public void start() {
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                List<Send> selectList = messageService.selectAll(repository);
                for (Send send : selectList) {
                    if (send == null) {
                        throw new SelectFailedException("[SCHEDULER] Data Select from DB Error");
                    }
                    if (!sendToMapperQueue(send)) {
                        throw new RuntimeException();
                    }
                }
            } catch (Exception e) {
                log.error("[SCHEDULER] Exception occurred in the scheduler: {}", e.getMessage(), e);
                this.shedulerState = true;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
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

    public boolean sendToMapperQueue(Send send) {
        if (send == null) {
            return false;
        }

        if (queue.sendPublish(key, send)) {
            log.debug("[SCHEDULER@{}] SELECT DATA FROM TABLE SUCCESS !!! Data Imported : [{}]", sessionResource.getSocket(), send);
            return true;
        }
        return false;
    }
}
