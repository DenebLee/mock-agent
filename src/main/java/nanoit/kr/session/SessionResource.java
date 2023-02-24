package nanoit.kr.session;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nanoit.kr.domain.PropertyDto;
import nanoit.kr.domain.message.Authentication;
import nanoit.kr.domain.message.Payload;
import nanoit.kr.domain.message.PayloadType;
import nanoit.kr.extension.Jackson;
import nanoit.kr.queue.InternalQueueImpl;
import nanoit.kr.repository.MessageRepository;
import nanoit.kr.thread.ReceiveThread;
import nanoit.kr.thread.SendThread;

import java.io.*;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class SessionResource {
    private final AtomicBoolean readThreadStatus = new AtomicBoolean(true);
    private final AtomicBoolean writeThreadStatus = new AtomicBoolean(true);
    private final AtomicBoolean authenticationStatus = new AtomicBoolean(false);
    @Getter
    private final Socket socket;
    private final PropertyDto dto;
    // Internal resources
    private final Thread writeThread;
    private final Thread receiveThread;
    private final InputStreamReader inputStreamReader;
    private final OutputStreamWriter outputStreamWriter;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final SendThread send;
    private final ReceiveThread receive;
    private final InternalQueueImpl queue;
    private final String uuid;
    private final String duplicateKey;


    public SessionResource(String uuid, Socket socket, PropertyDto dto, InternalQueueImpl queue, MessageRepository repository) throws IOException {
        this.uuid = uuid;
        this.dto = dto;
        this.duplicateKey = uuid + dto.getDbName();

        this.queue = queue;
        this.socket = socket;
        //initialize Internal Resources
        this.inputStreamReader = new InputStreamReader(socket.getInputStream());
        this.outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        this.reader = new BufferedReader(inputStreamReader);
        this.writer = new BufferedWriter(outputStreamWriter);

        this.send = new SendThread(repository,uuid, duplicateKey, this::writeThreadCleaner, queue, writer, authenticationStatus, writeThreadStatus);
        this.receive = new ReceiveThread(uuid, this::receiveThreadCleaner, queue, reader, readThreadStatus);

        this.receiveThread = new Thread(receive);
        this.receiveThread.setName("RECEIVE-THREAD");
        this.writeThread = new Thread(send);
        this.writeThread.setName("WRITE-THREAD");
    }

    public void start() {
        try {

            if (!sendAuthentication(dto)) {
                log.error("[SESSION-RESOURCE@{}] DATA SEND TO G/W FAILED", socket);
            }
            this.receiveThread.start();
            this.writeThread.start();

        } catch (IOException e) {
            log.error("[SESSION-RESOURCE@{}] AN IO EXCEPTION OCCURRED: {}", socket, e.getMessage(), e);
        } catch (Exception e) {
            log.error("[SESSION-RESOURCE@{}] AN EXCEPTION OCCURRED: {}", socket, e.getMessage(), e);
        }
    }

    public void writeThreadCleaner(String calledClassName) {
        try {
            log.info("[SESSION-RESOURCE@{}] name = {} called cleaner", socket, calledClassName);
            writeThreadStatus.compareAndSet(true, false);
            writeThread.interrupt();
            this.socket.shutdownInput();
            readThreadStatus.compareAndSet(true, false);
            connectClose();
        } catch (IOException e) {
            log.error("[SESSION-RESOURCE@{}] name = {} SOCKET INPUT STREAM CLOSE FAILED", socket, calledClassName, e);
        }
    }

    public void receiveThreadCleaner(String calledClassName) {
        try {
            log.info("[SESSION-RESOURCE@{}] name = {} called cleaner", socket, calledClassName);
            readThreadStatus.compareAndSet(true, false);
            receiveThread.interrupt();
            this.socket.shutdownOutput();
            writeThreadStatus.compareAndSet(true, false);
            connectClose();
        } catch (IOException e) {
            log.error("[SESSION-RESOURCE@{}] name = {} SOCKET OUT STREAM CLOSE FAILED", socket, calledClassName, e);
        }
    }

    public boolean isTerminated() {
        return writeThread.getState().equals(Thread.State.TERMINATED) && receiveThread.getState().equals(Thread.State.TERMINATED);
    }

    public void connectClose() throws IOException {
        this.socket.close();
    }

    public boolean isSocketClose() {
        return this.socket.isClosed();
    }

    public boolean kill() throws IOException {
        readThreadStatus.compareAndSet(true, false);
        writeThreadStatus.compareAndSet(true, false);
        this.socket.shutdownOutput();
        if (!isSocketClose()) {
            log.warn("[SESSION-RESOURCE@{}] Socket is not close", socket);
            return false;
        }
        if (!socket.isOutputShutdown() && !socket.isInputShutdown()) {
            log.warn("[SESSION-RESOURCE@{}] Streams are not close", socket);
            return false;
        }
        if (!writeThread.isInterrupted() && !receiveThread.isInterrupted()) {
            log.warn("[SESSION-RESOURCE@{}] 이 스레드 들은 죽지도 않는 좀비입니다", socket);
            return false;
        }
        return true;
    }


    public boolean isStreamClose() {
        return false;
    }

    public void setAuthenticationStatus() {
        this.authenticationStatus.compareAndSet(false, true);
    }

    private boolean sendAuthentication(PropertyDto dto) throws IOException {
        Payload payload = new Payload();
        payload
                .setType(PayloadType.AUTHENTICATION)
                .setMessageUuid(duplicateKey)
                .setData(new Authentication(dto.getUserAgent(), dto.getUserId(), dto.getUserPwd(), dto.getUserEmail()));
        String authenticationData = Jackson.getInstance().getObjectMapper().writeValueAsString(payload);
        authenticationData = authenticationData + "\n";
        writer.write(authenticationData);
        writer.flush();
        log.info("[SESSION-RESOURCE@{}] DATA SEND TO G/W SUCCESS => DATA : {}", socket, dto);
        return true;
    }
}
