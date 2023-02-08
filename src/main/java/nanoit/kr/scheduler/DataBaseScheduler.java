package nanoit.kr.scheduler;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.TemporaryQueue;
import nanoit.kr.domain.entity.SendEntity;
import nanoit.kr.domain.message.Send;
import nanoit.kr.service.SendMessageService;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DataBaseScheduler {
    private final ScheduledExecutorService scheduledExecutorService;
    private final SendMessageService sendMessageService;
    private final TemporaryQueue queue;
    private long conditionId;

    public DataBaseScheduler(SendMessageService sendMessageService, TemporaryQueue queue) {
        this.sendMessageService = sendMessageService;
        this.queue = queue;
        this.conditionId = 0;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(task, 1, 3, TimeUnit.SECONDS);
    }

    public Runnable task = new Runnable() {
        @Override
        public void run() {
            try {
                long count = sendMessageService.count();
                if (count == 0) {
                    log.info("[SCHEDULER] There are no Data for SEND");
                } else if (count > 0) {
                    List<SendEntity> selectData = sendMessageService.selectSendMessagesById(conditionId);
                    if (!selectData.isEmpty()) {
//                        log.info("[SCHEDULER] SELECT DATA FROM SEND TABLE select data count : {}", selectData.size());
                        conditionId = selectData.get(selectData.size() - 1).getId();
//                        log.info("[SCHEDULER] conditionId VALUE CHECK : {}", conditionId);
                        for (SendEntity sendEntity : selectData) {
                            Send send = sendEntity.toDto();
                            if (queue.publish(send)) {
//                                log.info("[SCHEDULER] DATA INSERT IN TO QUEUE SUCCESS data : {}", send);
                            }
                        }
                        selectData.clear();
                    } else {
                        log.warn("[SCHEDULER] DATA TO IMPORT DOES NOT EXIST IN THE TABLE");
                    }
                }
            } catch (NullPointerException e) {
                log.info("[SCHEDULER] There is no data in the table to select -> {}", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                scheduledExecutorService.shutdown();
                log.error("[SCHEDULER] SHUTDOWN -> {}", e.getMessage());
            }
        }
    };
}
