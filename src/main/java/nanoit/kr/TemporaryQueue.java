package nanoit.kr;

import nanoit.kr.domain.internaldata.InternalDataFilter;
import nanoit.kr.domain.internaldata.InternalDataInsert;
import nanoit.kr.domain.internaldata.InternalDataMapper;
import nanoit.kr.domain.internaldata.InternalDataSender;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TemporaryQueue {

    private final Map<InternalDataType, LinkedBlockingQueue<Object>> brokerQueue;


    public TemporaryQueue() {
        this.brokerQueue = new HashMap<>();

        for (InternalDataType type : InternalDataType.values()) {
            brokerQueue.put(type, new LinkedBlockingQueue<>());
        }
    }

    public boolean publish(Object object) {
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
