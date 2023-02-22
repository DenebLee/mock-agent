package nanoit.kr.db;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.repository.before.MessageRepositoryBefore;
import nanoit.kr.service.before.MessageServiceBefore;
import nanoit.kr.service.before.MessageServiceImplBefore;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public class DatabaseHandler {
    private final Properties prop;

    public DatabaseHandler(Properties prop) {
        this.prop = prop;
    }

    public MessageServiceBefore getMessageService() throws IOException {
        MessageRepositoryBefore messageRepositoryBefore = MessageRepositoryBefore.createMessageRepository(prop);
        return new MessageServiceImplBefore(messageRepositoryBefore);
    }
}
