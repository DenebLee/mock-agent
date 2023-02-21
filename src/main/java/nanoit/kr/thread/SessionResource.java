package nanoit.kr.thread;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.domain.PropertyDto;
import nanoit.kr.domain.message.Authentication;
import nanoit.kr.domain.message.Payload;
import nanoit.kr.domain.message.PayloadType;
import nanoit.kr.extension.Jackson;
import nanoit.kr.queue.InternalQueue;
import nanoit.kr.queue.InternalQueueImpl;

import java.io.*;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class SessionResource {
    private AtomicBoolean readThreadStatus;
    private AtomicBoolean writeThreadStatus;
    private AtomicBoolean authenticationStatus;
    private final Socket socket;
    private final PropertyDto dto;

    // Make Internal Resources
    private final Thread writeThread;
    private final Thread receiveThread;
    private final InputStreamReader inputStreamReader;
    private final OutputStreamWriter outputStreamWriter;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final SendThread send;
    private final ReceiveThread receive;

    public SessionResource(Socket socket, InternalQueueImpl queue, PropertyDto dto) throws IOException {
        this.socket = socket;
        this.readThreadStatus = new AtomicBoolean(true);
        this.writeThreadStatus = new AtomicBoolean(true);
        this.authenticationStatus = new AtomicBoolean(false);
        this.dto = dto;

        //initialize Internal Resources
        this.inputStreamReader = new InputStreamReader(socket.getInputStream());
        this.outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        this.reader = new BufferedReader(inputStreamReader);
        this.writer = new BufferedWriter(outputStreamWriter);

        this.send = new SendThread(this::writeThreadCleaner, queue, writer, authenticationStatus, writeThreadStatus);
        this.receive = new ReceiveThread(this::receiveThreadCleaner, queue, reader, readThreadStatus);

        this.receiveThread = new Thread(receive);
        this.receiveThread.setName("RECEIVE-THREAD");
        this.writeThread = new Thread(send);
        this.writeThread.setName("WRITE-THREAD");
    }

    public void start() throws IOException {
        if (!sendAuthentication(dto)) {
            log.error("[SEND] DATA SEND TO G/W FAILED");
        }
        this.receiveThread.start();
        this.writeThread.start();
    }

    public void writeThreadCleaner(String calledClassName) {
        try {
            log.info("[THREAD-RESOURCE@{}] name = {} called cleaner", calledClassName, socket);
            writeThreadStatus.compareAndSet(true, false);
            writeThread.interrupt();
            this.socket.shutdownInput();
            readThreadStatus.compareAndSet(true, false);
            connectClose();
        } catch (IOException e) {
            log.error("[THREAD-RESOURCE@{}] name = {} SOCKET INPUT STREAM CLOSE FAILED", socket, calledClassName, e);
        }
    }

    public void receiveThreadCleaner(String calledClassName) {
        try {
            log.info("[RESOURCE] name = {} called cleaner", calledClassName);
            readThreadStatus.compareAndSet(true, false);
            receiveThread.interrupt();
            this.socket.shutdownOutput();
            writeThreadStatus.compareAndSet(true, false);
            connectClose();
        } catch (IOException e) {
            log.error("[THREAD-RESOURCE@{}] name = {} SOCKET OUT STREAM CLOSE FAILED", socket, calledClassName, e);
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

    public boolean isStreamClose(){
        return false;
    }

    public void setAuthenticationStatus() {
        this.authenticationStatus.compareAndSet(false, true);
    }

    private boolean sendAuthentication(PropertyDto dto) throws IOException {
        Payload payload = new Payload();
        payload
                .setType(PayloadType.AUTHENTICATION)
                .setMessageUuid(UUID.randomUUID().toString())
                .setData(new Authentication(dto.getUserAgent(), dto.getUserId(), dto.getUserPwd(), dto.getUserEmail()));
        String authenticationData = Jackson.getInstance().getObjectMapper().writeValueAsString(payload);
        authenticationData = authenticationData + "\n";
        writer.write(authenticationData);
        writer.flush();
        log.info("[THREAD-RESOURCE@{}] DATA SEND TO G/W SUCCESS => DATA : {}", dto, socket);
        return true;
    }
}
