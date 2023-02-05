package nanoit.kr.thread;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.TemporaryQueue;
import nanoit.kr.service.ReceiveMessageService;

import java.net.Socket;

@Slf4j
public class ReceiveThread extends ModuleProcess {

    private final ReceiveMessageService receiveMessageService;
    private final Socket socket;
    private final TemporaryQueue queue;

    public ReceiveThread(String uuid, ReceiveMessageService receiveMessageService, Socket socket, TemporaryQueue queue) {
        super(uuid);
        this.socket = socket;
        this.receiveMessageService = receiveMessageService;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            log.info("[RECEIVE- {}] THREAD START", uuid);


            // 인증 메시지를 보낸다음 authentication 성공 여부에 따라 루프문 실행?
            // 최초 실행시 start log , 인증 메시지 전송 , 최초 select 후 queue 에 담기

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
