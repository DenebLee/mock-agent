package nanoit.kr.module;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.InternalQueue;
import nanoit.kr.service.MessageService;

@Slf4j
public class Insert extends ModuleProcess {

    private final InternalQueue queue;
    private final MessageService messageService;

    public Insert(String uuid, InternalQueue queue, MessageService messageService) {
        super(queue, uuid);
        this.queue = queue;
        this.messageService = messageService;
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
