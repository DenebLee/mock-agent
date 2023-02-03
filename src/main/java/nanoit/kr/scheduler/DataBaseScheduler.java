package nanoit.kr.scheduler;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.TemporaryQueue;
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

    public DataBaseScheduler(SendMessageService sendMessageService, TemporaryQueue queue) {
        this.sendMessageService = sendMessageService;
        this.queue = queue;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(this::selectData, 2000, 5000, TimeUnit.MICROSECONDS);
    }

    private void selectData() {
        try {
            List<Send> selectData = sendMessageService.selectSendMessages();

            if (!selectData.isEmpty()) {
                log.info("[SCHEDULER] SELECT DATA FROM SEND TABLE select data count = [{}]", selectData.size());
                for (Send send : selectData) {
                    queue.publish(send);
                }
                selectData.clear();
            } else {
                log.warn("[SCHEDULER] DATA TO IMPORT DOES NOT EXIST IN THE TABLE");
            }
            // 만약 select 했을 때 값이 없을때 대응 로직 짜야됨
            // 스케쥴러에서 가져올때 select 한 메시지들 총합 갯수 log 로 띄우기

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
