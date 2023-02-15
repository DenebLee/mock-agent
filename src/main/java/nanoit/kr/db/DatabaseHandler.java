package nanoit.kr.db;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.domain.PropertyDto;
import nanoit.kr.repository.MessageRepository;
import nanoit.kr.service.MessageService;
import nanoit.kr.service.MessageServiceImpl;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;

@Slf4j
public class DatabaseHandler {
    private final PropertyDto dto;

    public DatabaseHandler(PropertyDto dto) {
        this.dto = dto;
    }


    public MessageService getMessageService(SqlSession session) throws IOException {
        MessageRepository messageRepository = MessageRepository.createMessageRepository(session);
        return new MessageServiceImpl(messageRepository);
    }

}
