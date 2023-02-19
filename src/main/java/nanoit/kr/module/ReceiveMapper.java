package nanoit.kr.module;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import nanoit.kr.InternalQueue;
import nanoit.kr.domain.message.Payload;
import nanoit.kr.extension.Jackson;

@Slf4j
public class ReceiveMapper extends ModuleProcess {

    private final InternalQueue queue;

    public ReceiveMapper(String uuid, InternalQueue queue) {
        super(queue, uuid);
        this.queue = queue;
    }


    @Override
    public void run() {
        try {
            while (true) {

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
