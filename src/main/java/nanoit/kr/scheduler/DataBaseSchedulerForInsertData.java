package nanoit.kr.scheduler;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.TemporaryQueue;
import nanoit.kr.domain.entity.SendEntity;
import nanoit.kr.domain.message.Send;
import nanoit.kr.service.ReceiveMessageService;
import nanoit.kr.service.SendMessageService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DataBaseSchedulerForInsertData {
    private final ScheduledExecutorService scheduledExecutorService;
    private final SendMessageService sendMessageService;

    public DataBaseSchedulerForInsertData(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(task, 5, 10, TimeUnit.SECONDS);
    }

    public Runnable task = new Runnable() {
        @Override
        public void run() {
            try {
                Scanner scanner = new Scanner(System.in);
                System.out.println();
                System.out.println("=================================================================================================================================================================================================");
                System.out.println("                                                                            전송 할 메시지 입력 ");
                System.out.println("=================================================================================================================================================================================================");
                int limit = scanner.nextInt();
                int count = 0;
                SendEntity sendEntity = new SendEntity();
                List<SendEntity> list = new ArrayList<>();
                do {
                    count++;
                    sendEntity
                            .setId(0)
                            .setPhoneNum("010-4444-5555")
                            .setCallback("053-444-5555")
                            .setName("이정섭")
                            .setContent("안녕하세요" + count)
                            .setCreatedAt(new Timestamp(System.currentTimeMillis()))
                            .setLastModifiedAt(new Timestamp(System.currentTimeMillis()));
                    list.add(sendEntity);
                } while (count < limit);
                if (sendMessageService.insertAll(list)) {
                    log.info("[SCHEDULER-INSERT-DATA] INSERT SUCCESS  total count : [{}]", list.size());
                }
            } catch (Exception e) {
                e.printStackTrace();
                scheduledExecutorService.shutdown();
                log.error("[SCHEDULER-INSERT-DATA] SHUTDOWN -> {}", e.getMessage());
            }
        }
    };
}

