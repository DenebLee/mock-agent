package nanoit.kr.module;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.queue.InternalDataType;
import nanoit.kr.queue.InternalQueueImpl;
import nanoit.kr.domain.message.ErrorPayload;
import nanoit.kr.domain.message.Payload;
import nanoit.kr.domain.message.Report;
import nanoit.kr.extension.Jackson;

@Slf4j
public class Filter extends ModuleProcess {

    private final InternalQueueImpl queue;

    public Filter(String uuid, InternalQueueImpl queue) {
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
