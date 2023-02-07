package nanoit.kr.thread;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import nanoit.kr.TemporaryDataType;
import nanoit.kr.TemporaryQueue;
import nanoit.kr.domain.message.Authentication;
import nanoit.kr.domain.message.Payload;
import nanoit.kr.domain.message.PayloadType;
import nanoit.kr.domain.message.Send;
import nanoit.kr.extension.Jackson;
import nanoit.kr.service.SendMessageService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
public class SendThread implements Runnable {
    private final Consumer<String> cleaner;
    private final SendMessageService sendMessageService;
    private final Socket socket;
    private final BufferedWriter bufferedWriter;
    private final TemporaryQueue queue;
    private final Properties properties;
    private AtomicBoolean authenticationStatus;
    private AtomicBoolean sendThreadStatus;

    public SendThread(Consumer<String> cleaner, SendMessageService sendMessageService, Socket socket, TemporaryQueue queue, Properties properties, BufferedWriter bufferedWriter, AtomicBoolean authenticationStatus, AtomicBoolean sendThreadStatus) throws IOException {
        this.cleaner = cleaner;
        this.socket = socket;
        this.sendMessageService = sendMessageService;
        this.queue = queue;
        this.properties = properties;
        this.bufferedWriter = bufferedWriter;
        this.authenticationStatus = authenticationStatus;
        this.sendThreadStatus = sendThreadStatus;
    }


    // 인증 무조건 성공하는 조건으로 일단 제작
    @Override
    public void run() {
        try {
            log.info("[SEND] THREAD START");

            while (sendThreadStatus.get()) {
                if (authenticationStatus.get()) {
                    Object object = queue.subscribe(TemporaryDataType.SEND);
                    if (object instanceof Send) {
                        Send send = (Send) object;
                        String sendPayload = toJSON(send);
                        if (!send(sendPayload)) {
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

    private String toJSON(Send send) throws JsonProcessingException {
        return Jackson.getInstance().getObjectMapper().writeValueAsString(new Payload(PayloadType.SEND, "123", send));
    }

    private boolean send(String str) throws IOException {
        str = str + "\n";
        bufferedWriter.write(str);
        bufferedWriter.flush();
        log.info("[SEND] DATA SEND TO G/W SUCCESS => DATA : {}", str);
        return true;
    }
}

