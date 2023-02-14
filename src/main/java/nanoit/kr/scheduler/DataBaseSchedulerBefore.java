package nanoit.kr.scheduler;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.TemporaryQueue;
import nanoit.kr.domain.before.SendEntityBefore;
import nanoit.kr.domain.internaldata.InternalDataMapper;
import nanoit.kr.domain.message.MessageStatus;
import nanoit.kr.domain.message.Payload;
import nanoit.kr.domain.message.PayloadType;
import nanoit.kr.service.SendMessageService;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DataBaseSchedulerBefore {
    private final ScheduledExecutorService scheduledExecutorService;
    private final SendMessageService sendMessageService;
    private final TemporaryQueue queue;
    private long conditionId;

    public DataBaseSchedulerBefore(SendMessageService sendMessageService, TemporaryQueue queue) {
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
                    List<SendEntityBefore> selectData = sendMessageService.selectSendMessagesById(conditionId);

                    if (!selectData.isEmpty()) {
                        log.debug("[SCHEDULER] SELECT DATA FROM SEND TABLE select data count : {}", selectData.size());
                        conditionId = selectData.get(selectData.size() - 1).getId();
                        log.debug("[SCHEDULER] conditionId VALUE CHECK : {}", conditionId);

                        for (SendEntityBefore sendEntityBefore : selectData) {
                            if (queue.publish(new InternalDataMapper(new Payload(PayloadType.SEND, String.valueOf(count), sendEntityBefore.toDto())))) {

                                // 에러발생
                                // 중복값 안되게

                                log.debug("[SCHEDULER] DATA INSERT IN TO QUEUE SUCCESS data : {}", sendEntityBefore.toDto());
                                sendMessageService.updateSendMessageStatus(sendEntityBefore.getId(), MessageStatus.SENT);
                                // 상태 수정
                            }
                        }
                        selectData.clear();

                    } else {
                        log.warn("[SCHEDULER] DATA TO IMPORT DOES NOT EXIST IN THE TABLE");
                    }
                }
            } catch (NullPointerException e) {
                log.debug("[SCHEDULER] There is no data in the table to select -> {}", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                scheduledExecutorService.shutdown();
                log.error("[SCHEDULER] SHUTDOWN -> {}", e.getMessage());
            }
        }
    };
}
