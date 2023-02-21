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
    private final InternalQueueImpl queue;
    private final BufferedReader bufferedReader;
    private final AtomicBoolean readThreadStatus;

    public ReceiveThread(Consumer<String> cleaner, InternalQueueImpl queue, BufferedReader bufferedReader, AtomicBoolean readThreadStatus) {
        this.cleaner = cleaner;
        this.queue = queue;
        this.bufferedReader = bufferedReader;
        this.readThreadStatus = readThreadStatus;
    }

    @Override
    public void run() {
        try {
            log.info("[RECEIVE] RECEIVE - THREAD START");
            while (readThreadStatus.get()) {
                String receiveData = bufferedReader.readLine();
                if (receiveData != null) {
                    log.info("[RECEIVE] RECEIVE DATA : [{}]", receiveData);
                    if (queue.publish(InternalDataType.RECEIVE_MAPPER, receiveData)) {
                        log.debug("[RECEIVE] DATA SEND TO MAPPER data : [{}]", receiveData);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("[RECEIVE] terminating ", e);
            cleaner.accept(this.getClass().getName());
        }
    }
}
