package nanoit.kr.thread;

import nanoit.kr.service.SendMessageService;

public class SendThread extends ModuleProcess {

    private final SendMessageService sendMessageService;

    public SendThread(String uuid, SendMessageService sendMessageService) {
        super(uuid);
        this.sendMessageService = sendMessageService;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (sendMessageService.isAlive()) {
                    
                }
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

