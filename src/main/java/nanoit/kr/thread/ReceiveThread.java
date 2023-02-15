package nanoit.kr.thread;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.InternalQueue;
import nanoit.kr.domain.internaldata.InternalDataMapper;

import java.io.BufferedReader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
public class ReceiveThread implements Runnable {
    private final Consumer<String> cleaner;
    private final InternalQueue queue;
    private final BufferedReader bufferedReader;
    private final AtomicBoolean readThreadStatus;

    public ReceiveThread(Consumer<String> cleaner, InternalQueue queue, BufferedReader bufferedReader, AtomicBoolean readThreadStatus) {
        this.cleaner = cleaner;
        this.queue = queue;
        this.bufferedReader = bufferedReader;
        this.readThreadStatus = readThreadStatus;
    }

    @Override
    public void run() {
        try {
            log.info("[RECEIVE] RECEIVE - THREAD START");
            // 인증 메시지를 보낸다음 authentication 성공 여부에 따라 루프문 실행
            while (readThreadStatus.get()) {
                String receiveData = bufferedReader.readLine();
                if (receiveData != null) {
                    log.info("[RECEIVE] RECEIVE DATA : [{}]", receiveData);
                    if (queue.publish(new InternalDataMapper(receiveData))) {
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
