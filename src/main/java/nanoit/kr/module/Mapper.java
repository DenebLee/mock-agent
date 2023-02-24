package nanoit.kr.module;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import nanoit.kr.domain.message.Payload;
import nanoit.kr.extension.Jackson;
import nanoit.kr.queue.InternalDataType;
import nanoit.kr.queue.InternalQueueImpl;

@Slf4j
public class Mapper extends ModuleProcess {

    private final InternalQueueImpl queue;

    public Mapper(String uuid, InternalQueueImpl queue) {
        super(queue, uuid);
        this.queue = queue;
    }


    @Override
    public void run() {
        try {
            while (this.flag) {
                Object object = queue.receiveSubscribe(InternalDataType.RECEIVE_MAPPER);
                if (object != null) {
                    if (object instanceof String) {
                        String message = (String) object;
                        Payload payload = Jackson.getInstance().getObjectMapper().readValue(message, Payload.class);
                        if (queue.receivePublish(InternalDataType.FILTER, payload)) {
                            log.debug("[MAPPER] Message Sent To Filter Success data : [{}]", payload);
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
