package nanoit.kr.queue;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class InternalQueueImpl implements InternalQueue {

    private final ConcurrentHashMap<InternalDataType, LinkedBlockingQueue<Object>> internalDataQueue;

    public InternalQueueImpl() {
        this.internalDataQueue = new ConcurrentHashMap<>();
        for (InternalDataType type : InternalDataType.values()) {
            internalDataQueue.put(type, new LinkedBlockingQueue<>());
        }
    }


    public boolean publish(InternalDataType type, Object obj) {
        if (obj != null) {
            return internalDataQueue.get(type).offer(obj);
        } else {
            return false;
        }
    }

    public Object subscribe(InternalDataType type) throws InterruptedException {
        return internalDataQueue.get(type).poll(1, TimeUnit.SECONDS);
    }

    public int getInternalQueueSize(InternalDataType type) {
        return internalDataQueue.get(type).size();
    }
    
}
