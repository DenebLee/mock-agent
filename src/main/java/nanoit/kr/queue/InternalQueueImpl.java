package nanoit.kr.queue;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class InternalQueueImpl implements InternalQueue {

    private final ConcurrentHashMap<InternalDataType, LinkedBlockingQueue<Object>> receiveQueue;
    private final ConcurrentHashMap<String, LinkedBlockingQueue<String>> sendQueue;


    public InternalQueueImpl() {
        this.receiveQueue = new ConcurrentHashMap<>();
        this.sendQueue = new ConcurrentHashMap<>();
        for (InternalDataType type : InternalDataType.values()) {
            receiveQueue.put(type, new LinkedBlockingQueue<>());
        }
    }

    @Override
    public void registSendQueue(String key) {
        sendQueue.put(key, new LinkedBlockingQueue<>());
    }

    @Override
    public boolean receivePublish(InternalDataType type, Object obj) {
        if (type == null || obj == null) {
            return false;
        }
        return receiveQueue.get(type).offer(obj);
    }

    @Override
    public Object receiveSubscribe(InternalDataType type) throws InterruptedException {
        if (type == null) {
            return false;
        }
        return receiveQueue.get(type).poll(1, TimeUnit.SECONDS);
    }

    @Override
    public boolean sendPublish(String key, String value) {
        if (key == null || key.length() < 36) {
            return false;
        }
        return sendQueue.get(key).offer(value);
    }

    @Override
    public String sendSubscribe(String key) throws InterruptedException {
        return sendQueue.get(key).poll(1, TimeUnit.SECONDS);
    }

    @Override
    public int getInternalQueueSize(InternalDataType type) {
        return receiveQueue.get(type).size();
    }
}
