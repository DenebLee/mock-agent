package nanoit.kr.module;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.db.DatabaseHandler;
import nanoit.kr.domain.entity.SendAckEntity;
import nanoit.kr.domain.message.InsertMessage;
import nanoit.kr.manager.SessionManager;
import nanoit.kr.queue.InternalDataType;
import nanoit.kr.queue.InternalQueueImpl;
import nanoit.kr.service.MessageService;

@Slf4j
public class Insert extends ModuleProcess {


    private final InternalQueueImpl queue;
    private final MessageService messageService;
    private final DatabaseHandler handler;
    private final SessionManager manager;

    public Insert(String uuid, InternalQueueImpl queue, SessionManager manager) {
        super(queue, uuid);
        this.manager = manager;
        this.queue = queue;
        this.handler = new DatabaseHandler();
        this.messageService = handler.getMessageService1();
    }


    @Override
    public void run() {
        try {
            while (this.flag) {
                Object object = queue.receiveSubscribe(InternalDataType.INSERT);
                if (object == null) {
                    continue;
                }
                if (object instanceof InsertMessage) {
                    InsertMessage message = (InsertMessage) object;
//                    if (messageService.updateReceiveResult(manager.getRepository(message.getUuid()), makeMessageEntity(message))) {
//                        log.debug("[INSERT] Receive Message update DB success result - code : [{}]", "1");
//                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("[INSERT] Error occurred while taking message from queue: {}", e.getMessage());
            Thread.currentThread().interrupt();
        } catch (NullPointerException e) {
            log.debug("[INSERT] Null Pointer Exception occurred while processing message: {}", e.getMessage());
        } catch (Exception e) {
            log.error("[INSERT] Error occurred while processing message: {}", e.getMessage());
        }
    }

    private SendAckEntity makeMessageEntity(InsertMessage data) {
        SendAckEntity message = new SendAckEntity();
        message
                .setMessageId(data.getSendAck().getMessageId())
                .setResult(data.getSendAck().getResult().getProperty());
        return message;
    }

    @Override
    public void shoutDown() {
        Thread.currentThread().interrupt();
        this.flag = false;
    }

    @Override
    public void sleep() throws InterruptedException {
        log.debug("[INSERT] Putting module to sleep...");
        Thread.sleep(1000);
    }

    @Override
    public String getUuid() {
        return this.uuid;
    }
}
