package nanoit.kr;

import nanoit.kr.domain.internaldata.*;
import nanoit.kr.domain.message.Send;
import nanoit.kr.domain.message.SendAck;

import java.util.concurrent.LinkedBlockingQueue;


public class TestInternalQueue {
    private final LinkedBlockingQueue<Send> sendQueue;
    private final LinkedBlockingQueue<SendAck> receiveQueue;

    public TestInternalQueue() {
        this.sendQueue = new LinkedBlockingQueue<>();
        this.receiveQueue = new LinkedBlockingQueue<>();
    }

    public boolean publish() {

    }

    public int getQueueSize(InternalDataType type) {
        return sendQueue.size();
    }
}
