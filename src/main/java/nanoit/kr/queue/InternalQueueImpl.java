package nanoit.kr.queue;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class InternalQueueImpl implements InternalQueue {

    // receiveQueue 는 응답 메시지들 내부에서 통합적으로 사용하기 위한 Queue
    private final ConcurrentHashMap<InternalDataType, LinkedBlockingQueue<Object>> receiveQueue;

    // Account 마다 줄 Queue
    // String은 account 식별 uuid
    private final ConcurrentHashMap<String, LinkedBlockingQueue<Object>> accountQueue;

    public InternalQueueImpl() {
        this.receiveQueue = new ConcurrentHashMap<>();
        this.accountQueue = new ConcurrentHashMap<>();
    }

    private void makeQueue() {
        for (InternalDataType type : InternalDataType.values()) {
            receiveQueue.put(type, new LinkedBlockingQueue<>());
        }
    }

    public boolean publish(InternalDataType type, Object obj) {
        if (obj != null) {
            return receiveQueue.get(type).offer(obj);
        } else {
            return false;
        }
    }

    public Object subscribe(InternalDataType type) throws InterruptedException {
        return receiveQueue.get(type).poll(1, TimeUnit.SECONDS);
    }

    public int getInternalQueueSize(InternalDataType type) {
        return receiveQueue.get(type).size();
    }

}
