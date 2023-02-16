package nanoit.kr.db;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.repository.MessageRepository;
import nanoit.kr.service.MessageService;
import nanoit.kr.service.MessageServiceImpl;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public class DatabaseHandler {
    private final Properties prop;

    public DatabaseHandler(Properties prop) {
        this.prop = prop;
    }

    public MessageService getMessageService() throws IOException {
        MessageRepository messageRepository = MessageRepository.createMessageRepository(prop);
        return new MessageServiceImpl(messageRepository);
    }
}
