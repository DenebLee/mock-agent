package nanoit.kr.thread;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.queue.InternalDataType;
import nanoit.kr.queue.InternalQueueImpl;

import java.io.BufferedReader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
public class ReceiveThread implements Runnable {
    private final Consumer<String> cleaner;
    private final BufferedReader bufferedReader;
    private final AtomicBoolean readThreadStatus;
    private final InternalQueueImpl queue;
    private final String uuid;

    public ReceiveThread(String uuid, Consumer<String> cleaner, InternalQueueImpl internalQueue, BufferedReader bufferedReader, AtomicBoolean readThreadStatus) {
        this.uuid = uuid;
        this.queue = internalQueue;
        this.cleaner = cleaner;
        this.bufferedReader = bufferedReader;
        this.readThreadStatus = readThreadStatus;
    }

    @Override
    public void run() {
        try {
            log.info("[REV-THREAD@{}] RECEIVE - THREAD START", uuid);
            while (readThreadStatus.get()) {
                String receiveData = bufferedReader.readLine();
                if (receiveData != null) {
                    log.info("[REV-THREAD@{}] RECEIVE DATA : [{}]", uuid, receiveData);
                    if (!queue.receivePublish(InternalDataType.RECEIVE_MAPPER, receiveData)) {
                        log.error("[REV-THREAD@{}] Failed to insert response message into queue", uuid);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("[REV-THREAD@{}] terminating ----", uuid, e);
            cleaner.accept(this.getClass().getName());
        }
    }
}
