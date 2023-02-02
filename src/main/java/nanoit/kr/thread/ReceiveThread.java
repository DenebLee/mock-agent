package nanoit.kr.thread;

import ch.qos.logback.core.encoder.EchoEncoder;
import nanoit.kr.service.ReceiveMessageService;

public class ReceiveThread extends ModuleProcess {

    private final ReceiveMessageService receiveMessageService;

    public ReceiveThread(String uuid, ReceiveMessageService receiveMessageService) {
        super(uuid);
        this.receiveMessageService = receiveMessageService;
    }

    @Override
    public void run() {
        try {
            while (true) {

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
