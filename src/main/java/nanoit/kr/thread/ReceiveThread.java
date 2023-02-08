package nanoit.kr.thread;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.TemporaryQueue;
import nanoit.kr.domain.message.*;
import nanoit.kr.extension.Jackson;
import nanoit.kr.service.ReceiveMessageService;

import java.io.BufferedReader;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
public class ReceiveThread implements Runnable {
    private final Consumer<String> cleaner;
    private final ReceiveMessageService receiveMessageService;
    private final Socket socket;
    private final TemporaryQueue queue;
    private final BufferedReader bufferedReader;
    private final AtomicBoolean authenticationStatus;
    private final AtomicBoolean readThreadStatus;

    public ReceiveThread(Consumer<String> cleaner, ReceiveMessageService receiveMessageService, Socket socket, TemporaryQueue queue, BufferedReader bufferedReader, AtomicBoolean authenticationStatus, AtomicBoolean readThreadStatus) {
        this.cleaner = cleaner;
        this.socket = socket;
        this.receiveMessageService = receiveMessageService;
        this.queue = queue;
        this.bufferedReader = bufferedReader;
        this.authenticationStatus = authenticationStatus;
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
//                    log.info("[RECEIVE] RECEIVE DATA : [{}]", receiveData);
                    Payload payload = Jackson.getInstance().getObjectMapper().readValue(receiveData, Payload.class);
                    switch (payload.getType()) {
                        case SEND_ACK:
                            SendAck sendAck = Jackson.getInstance().getObjectMapper().convertValue(payload.getData(), SendAck.class);
//                            log.info("[RECEIVE] SEND_ACK receive result : [{}]", sendAck.getResult());
                            sendAck
                                    .setCreatedAt(new Timestamp(System.currentTimeMillis()))
                                    .setLastModifiedAt(new Timestamp(System.currentTimeMillis()));
                            if (!receiveMessageService.insertReceiveMessage(sendAck)) {
                                log.error("[RECEIVE] RECEIVE DATA INSERT TO DB FAILED ");
                            }
                            break;
                        case AUTHENTICATION_ACK:
                            AuthenticationAck authenticationAck = Jackson.getInstance().getObjectMapper().convertValue(payload.getData(), AuthenticationAck.class);
                            if (authenticationAck.getResult().contains("Success")) {
                                authenticationStatus.compareAndSet(false, true);
                            } else {
                                socket.close();
                                throw new Exception();
                            }
                            break;
                        case REPORT:
                            Report report = Jackson.getInstance().getObjectMapper().convertValue(payload.getData(), Report.class);
//                            log.info("[RECEIVE] REPORT receive agentId : [{}] result : [{}]", report.getAgent_id(), report.getResult());
                            break;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("[RECEIVE] terminating ", e);
            cleaner.accept(this.getClass().getName());
        }
    }
}
