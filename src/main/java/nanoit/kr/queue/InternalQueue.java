package nanoit.kr.queue;

public interface InternalQueue {

    boolean publish(InternalDataType type, Object obj);

    Object subscribe(InternalDataType type) throws InterruptedException;

    int getInternalQueueSize(InternalDataType type);
}
