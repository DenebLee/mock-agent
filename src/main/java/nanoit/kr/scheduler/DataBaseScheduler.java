package nanoit.kr.scheduler;

import nanoit.kr.domain.message.Send;
import nanoit.kr.service.SendMessageService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataBaseScheduler {
    private final ScheduledExecutorService scheduledExecutorService;
    private final SendMessageService sendMessageService;

    public DataBaseScheduler(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(this::selectData, 2000, 5000, TimeUnit.MICROSECONDS);
    }

    private void selectData() {
        try {
            while (true) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
