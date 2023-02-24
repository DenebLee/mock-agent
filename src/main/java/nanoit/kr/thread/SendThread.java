package nanoit.kr.thread;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.db.DatabaseHandler;
import nanoit.kr.domain.entity.SendAckEntity;
import nanoit.kr.queue.InternalDataType;
import nanoit.kr.queue.InternalQueueImpl;
import nanoit.kr.repository.MessageRepository;
import nanoit.kr.service.MessageService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
public class SendThread implements Runnable {
    private final Consumer<String> cleaner;
    private final BufferedWriter bufferedWriter;
    private final InternalQueueImpl queue;
    private AtomicBoolean authenticationStatus;
    private AtomicBoolean sendThreadStatus;
    private final String uuid;
    private final String duplicateKey;
    private final MessageRepository repository;
    private final MessageService service;
    private final DatabaseHandler databaseHandler;

    public SendThread(MessageRepository repository, String uuid, String duplicateKey, Consumer<String> cleaner, InternalQueueImpl queue, BufferedWriter bufferedWriter, AtomicBoolean authenticationStatus, AtomicBoolean sendThreadStatus) {
        this.repository = repository;
        this.uuid = uuid;
        this.duplicateKey = duplicateKey;
        this.cleaner = cleaner;
        this.queue = queue;
        this.bufferedWriter = bufferedWriter;
        this.authenticationStatus = authenticationStatus;
        this.sendThreadStatus = sendThreadStatus;
        this.databaseHandler = new DatabaseHandler();
        this.service = databaseHandler.getMessageService1();
    }

    @Override
    public void run() {
        try {
            log.info("[SEND-THREAD@{}] THREAD START", uuid);
            long lastPollTime = 0;
            long interval = 1000 / 80; // 1000은 1초를 밀리초(ms)로 나타낸 값이며, 80은 1초당 최대 전송 가능한 메시지 개수
            while (sendThreadStatus.get()) {
                if (authenticationStatus.get()) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastPollTime >= interval) { // 12.5안에 queue poll 하고 전송
                        lastPollTime = currentTime;
                        String sendPayload = queue.sendSubscribe(duplicateKey);
                        if (!send(sendPayload)) {
                            log.error("[SEND-THREAD@{}] Failed to send Message", uuid);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("[SEND-THREAD@{}] terminating ----", uuid, e);
            cleaner.accept(this.getClass().getName());
        }
    }

    private boolean send(String str) throws IOException {
        str = str + "\n";
        bufferedWriter.write(str);
        bufferedWriter.flush();
        log.info("[SEND-THREAD@{}] DATA SEND TO G/W SUCCESS => DATA : {}", uuid, str);
        return true;
    }
}

