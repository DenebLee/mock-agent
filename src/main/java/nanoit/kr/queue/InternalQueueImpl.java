package nanoit.kr.queue;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class InternalQueueImpl implements InternalQueue {

    private final ConcurrentHashMap<InternalDataType, LinkedBlockingQueue<Object>> receiveQueue;

    public InternalQueueImpl() {
        this.receiveQueue = new ConcurrentHashMap<>();

    }

    private void makeQueue() {
        for (InternalDataType type : InternalDataType.values()) {
            receiveQueue.put(type, new LinkedBlockingQueue<>());
        }
    }

    public boolean publish(InternalDataType type, Object obj) {
        if (type == null || obj == null) {
            return false;
        }
        return receiveQueue.get(type).offer(obj);
    }

    public Object subscribe(InternalDataType type) throws InterruptedException {
        if (type == null) {
            return false;
        }
        return receiveQueue.get(type).poll(2, TimeUnit.SECONDS);
    }

    public int getInternalQueueSize(InternalDataType type) {
        return receiveQueue.get(type).size();
    }
}
