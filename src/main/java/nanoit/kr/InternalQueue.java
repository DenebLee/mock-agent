package nanoit.kr;

import nanoit.kr.domain.internaldata.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class InternalQueue {

    private final Map<InternalDataType, LinkedBlockingQueue<Object>> brokerQueue;


    public InternalQueue() {
        this.brokerQueue = new HashMap<>();
        for (InternalDataType type : InternalDataType.values()) {
            brokerQueue.put(type, new LinkedBlockingQueue<>());
        }
    }

    public boolean publish(Object object) {
        // 단순화 작업
        if (object != null) {
            if (object instanceof InternalDataSender) {
                return brokerQueue.get(InternalDataType.SENDER).offer(object);
            }
            if (object instanceof InternalDataFilter) {
                return brokerQueue.get(InternalDataType.Filter).offer(object);
            }
            if (object instanceof InternalDataInsert) {
                return brokerQueue.get(InternalDataType.INSERT).offer(object);
            }
            if (object instanceof InternalDataMapper) {
                return brokerQueue.get(InternalDataType.MAPPER).offer(object);
            }
        } else {
            return false;
        }
        return false;
    }

    public Object subscribe(InternalDataType type) throws InterruptedException {
        return brokerQueue.get(type).poll(1, TimeUnit.SECONDS);
    }

    public int getQueueSize(InternalDataType type) {
        return brokerQueue.get(type).size();
    }

}
