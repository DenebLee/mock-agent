package nanoit.kr.thread;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import nanoit.kr.TemporaryQueue;
import nanoit.kr.domain.message.Authentication;
import nanoit.kr.domain.message.Payload;
import nanoit.kr.domain.message.PayloadType;
import nanoit.kr.extension.Jackson;
import nanoit.kr.service.ReceiveMessageService;
import nanoit.kr.service.SendMessageService;
import org.checkerframework.checker.units.qual.A;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ThreadResource {

    private final ReceiveMessageService receiveMessageService;
    private final SendMessageService sendMessageService;
    private final Socket socket;
    private final TemporaryQueue queue;
    private final Properties properties;

    private final Thread receiveThread;
    private final Thread writeThread;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;
    private final InputStreamReader inputStreamReader;
    private final OutputStreamWriter outputStreamWriter;
    private AtomicBoolean readThreadStatus;
    private AtomicBoolean writeThreadStatus;
    private AtomicBoolean authenticationStatus;

    public ThreadResource(ReceiveMessageService receiveMessageService, SendMessageService sendMessageService, Socket socket, TemporaryQueue queue, Properties properties) throws IOException {
        // param setting
        this.receiveMessageService = receiveMessageService;
        this.sendMessageService = sendMessageService;
        this.socket = socket;
        this.queue = queue;
        this.properties = properties;

        // util setting
        this.inputStreamReader = new InputStreamReader(socket.getInputStream());
        this.outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

        this.bufferedReader = new BufferedReader(this.inputStreamReader);
        this.bufferedWriter = new BufferedWriter(this.outputStreamWriter);

        this.readThreadStatus = new AtomicBoolean(true);
        this.writeThreadStatus = new AtomicBoolean(true);
        this.authenticationStatus = new AtomicBoolean(false);


        this.writeThread = new Thread(new SendThread(this::writeThreadCleaner, this.sendMessageService, this.socket, this.queue, this.properties, this.bufferedWriter, this.authenticationStatus,this.writeThreadStatus));
        writeThread.setName("WRITE-THREAD");

        this.receiveThread = new Thread(new ReceiveThread(this::receiveThreadCleaner, this.receiveMessageService, this.socket, this.queue, this.bufferedReader, this.authenticationStatus,this.readThreadStatus));
        receiveThread.setName("RECEIVE-THREAD");
    }

    public void start() throws Exception {
        if (!sendAuthentication(properties)) {
            log.error("[SEND] DATA SEND TO G/W FAILED");
        }
        this.receiveThread.start();
        this.writeThread.start();
    }

    public void writeThreadCleaner(String calledClassName) {
        try {
            log.info("[RESOURCE] name = {} called cleaner", calledClassName);
            writeThreadStatus.compareAndSet(true, false);
            writeThread.interrupt();
            this.socket.shutdownInput();
            readThreadStatus.compareAndSet(true, false);
            connectClose();
        } catch (IOException e) {
            log.error("[RESOURCE] name = {} SOCKET INPUT STREAM CLOSE FAILED", calledClassName, e);
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
            log.error("[RESOURCE] name = {} SOCKET OUT STREAM CLOSE FAILED", calledClassName, e);
        }
    }

    public boolean isTerminated() {
        return writeThread.getState().equals(Thread.State.TERMINATED) && receiveThread.getState().equals(Thread.State.TERMINATED);
    }

    public void connectClose() throws IOException {
        this.socket.close();
    }

    private boolean sendAuthentication(Properties properties) throws IOException {
        String payload = Jackson.getInstance().getObjectMapper().writeValueAsString(
                new Payload(
                        PayloadType.AUTHENTICATION, "123",
                        new Authentication(Long.parseLong(properties.getProperty("user.agent")),
                                properties.getProperty("user.name"),
                                properties.getProperty("user.password"),
                                properties.getProperty("user.email"))));
        payload = payload + "\n";
        bufferedWriter.write(payload);
        bufferedWriter.flush();
        log.info("[SEND] DATA SEND TO G/W SUCCESS => DATA : {}", payload);
        return true;
    }
}
