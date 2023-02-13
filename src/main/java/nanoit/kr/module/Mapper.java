package nanoit.kr.module;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import nanoit.kr.domain.internaldata.InternalDataType;
import nanoit.kr.TemporaryQueue;
import nanoit.kr.domain.internaldata.InternalDataFilter;
import nanoit.kr.domain.internaldata.InternalDataMapper;
import nanoit.kr.domain.internaldata.InternalDataSender;
import nanoit.kr.domain.message.Payload;
import nanoit.kr.extension.Jackson;

@Slf4j
public class Mapper extends ModuleProcess {

    private final TemporaryQueue queue;

    public Mapper(String uuid, TemporaryQueue queue) {
        super(queue, uuid);
        this.queue = queue;
    }


    @Override
    public void run() {
        try {
            while (true) {
                Object object = queue.subscribe(InternalDataType.MAPPER);
                if (object instanceof InternalDataMapper) {
                    InternalDataMapper mapperData = (InternalDataMapper) object;

                    // String 으로 들어오는 값 Send_ack , Report , Alive_ack
                    if (mapperData.getData() instanceof String) {
                        String data = (String) mapperData.getData();
                        Payload payload = toPayload(data);
                        if (queue.publish(new InternalDataFilter(payload))) {
                            log.debug("[MAPPER] DATA SEND TO WRITE-THREAD SUCCESS data :[{}]", payload);
                        } else {
                            log.warn("[MAPPER] DATA SEND TO WRITE-THREAD FAILED");
                        }
                        
                        // Payload 으로 들어오는 값 Send , Report_ack , Alive,
                    } else if (mapperData.getData() instanceof Payload) {
                        Payload payload = (Payload) mapperData.getData();
                        String dataToSend = toString(payload);
                        if (queue.publish(new InternalDataSender(dataToSend))) {
                            log.debug("[MAPPER] DATA SEND TO WRITE-THREAD SUCCESS data :[{}]", dataToSend);
                        } else {
                            log.warn("[MAPPER] DATA SEND TO WRITE-THREAD FAILED");
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Payload toPayload(String str) throws JsonProcessingException {
        return Jackson.getInstance().getObjectMapper().readValue(str, Payload.class);
    }

    private String toString(Payload payload) throws JsonProcessingException {
        return Jackson.getInstance().getObjectMapper().writeValueAsString(payload);
    }

    @Override
    public void shoutDown() {

    }

    @Override
    public void sleep() throws InterruptedException {

    }

    @Override
    public String getUuid() {
        return this.uuid;
    }
}
