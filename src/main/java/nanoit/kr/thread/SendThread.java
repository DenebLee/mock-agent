package nanoit.kr.thread;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.TemporaryDataType;
import nanoit.kr.TemporaryQueue;
import nanoit.kr.domain.message.Authentication;
import nanoit.kr.domain.message.Send;
import nanoit.kr.extension.Jackson;
import nanoit.kr.service.SendMessageService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Properties;

@Slf4j
public class SendThread extends ModuleProcess {

    private final SendMessageService sendMessageService;
    private final Socket socket;
    private final BufferedWriter bufferedWriter;
    private final OutputStreamWriter outputStreamWriter;
    private final TemporaryQueue queue;
    private final Properties properties;

    public SendThread(String uuid, SendMessageService sendMessageService, Socket socket, TemporaryQueue queue, Properties properties) throws IOException {
        super(uuid);
        this.socket = socket;
        this.sendMessageService = sendMessageService;
        this.queue = queue;

        // Stream set
        this.outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
        this.bufferedWriter = new BufferedWriter(this.outputStreamWriter);

        this.properties = properties;
    }


    // 인증 무조건 성공하는 조건으로 일단 제작
    @Override
    public void run() {
        try {
            if (socket.isConnected()) {
                String authentication = Jackson.getInstance().getObjectMapper().writeValueAsString(new Authentication(Long.parseLong(properties.getProperty("user.agent")),
                        properties.getProperty("user.name"), properties.getProperty("user.password"), properties.getProperty("user.email")));
                bufferedWriter.write(authentication);
                log.info("[SEND] AUTHENTICATION SEND TO G/W");
            }
            while (true) {
                if (!sendMessageService.isAlive()) {
                    throw new Exception();
                }

//                Object object = queue.subscribe(TemporaryDataType.SEND);
//                if (object instanceof Send) {
//                    Send send = (Send) object;
//                    String
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shoutDown() {

    }

    @Override
    public void sleep() throws InterruptedException {

    }

    @Override
    public String getUuid() {
        return null;
    }
}

