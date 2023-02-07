package nanoit.kr.repository;

import nanoit.kr.domain.entity.SendEntity;
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

    boolean updateMessageStatus(SendEntity sendEntity);

    SendEntity selectById(long id);

    List<SendEntity> selectAll() throws SelectFailedException;

    boolean isAlive();

    boolean insert(SendEntity sendEntity);

    boolean deleteById(long id);

    List<SendEntity> selectAllById(long id);


}

