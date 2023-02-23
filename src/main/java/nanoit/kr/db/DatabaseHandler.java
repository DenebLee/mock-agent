package nanoit.kr.db;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.service.MessageService;
import nanoit.kr.service.MessageServiceImpl;

@Slf4j
public class DatabaseHandler {
    private final MessageServiceImpl service;

    public DatabaseHandler() {
        this.service = new MessageServiceImpl();
    }

    //    public MessageServiceBefore getMessageService() throws IOException {
//        MessageRepositoryBefore messageRepositoryBefore = MessageRepositoryBefore.createMessageRepository(prop);
//        return new MessageServiceImplBefore(messageRepositoryBefore);
//    }

    public MessageService getMessageService1() {
        return this.service;
    }
}
