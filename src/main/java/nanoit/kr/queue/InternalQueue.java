package nanoit.kr.queue;

public interface InternalQueue {

    void registSendQueue(String key);

    boolean receivePublish(InternalDataType type, Object obj);

    Object receiveSubscribe(InternalDataType type) throws InterruptedException;

    boolean sendPublish(String key,String value);

    String sendSubscribe(String key) throws InterruptedException;

    int getInternalQueueSize(InternalDataType type);
}
