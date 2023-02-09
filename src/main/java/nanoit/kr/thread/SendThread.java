package nanoit.kr.thread;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import nanoit.kr.InternalDataType;
import nanoit.kr.TemporaryQueue;
import nanoit.kr.domain.internaldata.InternalDataSender;
import nanoit.kr.domain.message.Payload;
import nanoit.kr.domain.message.PayloadType;
import nanoit.kr.domain.message.Send;
import nanoit.kr.extension.Jackson;
import nanoit.kr.service.SendMessageService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
public class SendThread implements Runnable {
    private final Consumer<String> cleaner;
    private final BufferedWriter bufferedWriter;
    private final TemporaryQueue queue;
    private AtomicBoolean authenticationStatus;
    private AtomicBoolean sendThreadStatus;

    public SendThread(Consumer<String> cleaner, TemporaryQueue queue, BufferedWriter bufferedWriter, AtomicBoolean authenticationStatus, AtomicBoolean sendThreadStatus) {
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
                    Object object = queue.subscribe(InternalDataType.SENDER);
                    if (object instanceof InternalDataSender) {
                        InternalDataSender internalDataSender = (InternalDataSender) object;
                        if (!send(internalDataSender.getPayload())) {
                            log.info("[SEND] DATA SEND TO G/W FAILED");
                        }
                    }
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

