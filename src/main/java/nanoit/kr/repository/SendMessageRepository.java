package nanoit.kr.repository;

import nanoit.kr.domain.entity.SendEntity;
import nanoit.kr.domain.message.MessageStatus;
import nanoit.kr.domain.message.Send;
import nanoit.kr.exception.SelectFailedException;

import java.io.IOException;
import java.util.List;
import java.util.Properties;


public interface SendMessageRepository {
    static SendMessageRepository createSendRepository(Properties properties) throws IOException {
        return new SendMessageRepositoryImpl(properties);
    }

    long count();

    boolean deleteAll();

    int updateMessageStatus(SendEntity sendEntity);

    Object selectById(long id);

    List<SendEntity> selectAll() throws SelectFailedException;

    boolean isAlive();

}

