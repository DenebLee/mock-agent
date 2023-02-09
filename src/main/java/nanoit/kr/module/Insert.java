package nanoit.kr.module;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.InternalDataType;
import nanoit.kr.TemporaryQueue;
import nanoit.kr.domain.internaldata.InternalDataInsert;
import nanoit.kr.domain.message.Report;
import nanoit.kr.domain.message.SendAck;
import nanoit.kr.service.ReceiveMessageService;

@Slf4j
public class Insert extends ModuleProcess {

    private final TemporaryQueue queue;
    private final ReceiveMessageService receiveMessageService;

    public Insert(String uuid, TemporaryQueue queue, ReceiveMessageService receiveMessageService) {
        super(queue, uuid);

        this.queue = queue;
        this.receiveMessageService = receiveMessageService;
    }


    @Override
    public void run() {
        try {
            while (true) {
                Object object = queue.subscribe(InternalDataType.Filter);

                if (object instanceof InternalDataInsert) {
                    InternalDataInsert internalDataInsert = (InternalDataInsert) object;

                    if (internalDataInsert.getPayload() instanceof SendAck) {
                        SendAck sendAck = (SendAck) internalDataInsert.getPayload();
                        if (receiveMessageService.insertReceiveMessage(sendAck)) {
                            log.debug("[INSERT] SEND_ACK SUCCESS TO INSERT RECEIVE_TABLE data : [{}]", sendAck);
                        }
                    } else if (internalDataInsert.getPayload() instanceof Report) {
                        // some code;
                    }
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
        return this.uuid;
    }
}
