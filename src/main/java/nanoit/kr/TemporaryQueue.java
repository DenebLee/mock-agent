package nanoit.kr;

import nanoit.kr.domain.message.Report;
import nanoit.kr.domain.message.ReportAck;
import nanoit.kr.domain.message.Send;
import nanoit.kr.domain.message.SendAck;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TemporaryQueue {

    private final Map<TemporaryDataType, LinkedBlockingQueue<Object>> brokerQueue;


    public TemporaryQueue() {
        this.brokerQueue = new HashMap<>();

        for (TemporaryDataType type : TemporaryDataType.values()) {
            brokerQueue.put(type, new LinkedBlockingQueue<>());
        }
    }

    public boolean publish(Object object) {
        if (object != null) {
            if (object instanceof Send) {
                return brokerQueue.get(TemporaryDataType.SEND).offer(object);
            }
            if (object instanceof SendAck) {
                return brokerQueue.get(TemporaryDataType.SEND_ACK).offer(object);
            }
            if (object instanceof Report) {
                return brokerQueue.get(TemporaryDataType.REPORT).offer(object);
            }
            if (object instanceof ReportAck) {
                return brokerQueue.get(TemporaryDataType.REPORT_ACK).offer(object);
            }
        } else {
            return false;
        }
        return false;
    }

    public Object subscribe(TemporaryDataType type) throws InterruptedException {
        return brokerQueue.get(type).poll(1, TimeUnit.SECONDS);
    }

}
