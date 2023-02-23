package nanoit.kr.thread;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.domain.entity.SendAckEntity;
import nanoit.kr.queue.InternalDataType;
import nanoit.kr.queue.InternalQueueImpl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
public class SendThread implements Runnable {
    private final Consumer<String> cleaner;
    private final BufferedWriter bufferedWriter;
    private final LinkedBlockingQueue queue;
    private AtomicBoolean authenticationStatus;
    private AtomicBoolean sendThreadStatus;

    public SendThread(Consumer<String> cleaner, LinkedBlockingQueue queue, BufferedWriter bufferedWriter, AtomicBoolean authenticationStatus, AtomicBoolean sendThreadStatus) {
        this.cleaner = cleaner;
        this.queue = queue;
        this.bufferedWriter = bufferedWriter;
        this.authenticationStatus = authenticationStatus;
        this.sendThreadStatus = sendThreadStatus;
    }

    @Override
    public void run() {
        try {
            log.info("[SEND] THREAD START");
            while (sendThreadStatus.get()) {
                if (authenticationStatus.get()) {

                }
            }
        } catch (Exception e) {
            log.warn("[SEND] terminating", e);
            cleaner.accept(this.getClass().getName());
        }
    }

    private boolean send(String str) throws IOException {
        str = str + "\n";
        bufferedWriter.write(str);
        bufferedWriter.flush();
        log.info("[SEND] DATA SEND TO G/W SUCCESS => DATA : {}", str);
        return true;
    }
}

