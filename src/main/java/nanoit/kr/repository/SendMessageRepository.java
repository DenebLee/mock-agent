package nanoit.kr.repository;

import nanoit.kr.domain.before.SendEntityBefore;
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

    boolean updateMessageStatus(SendEntityBefore sendEntityBefore);

    SendEntityBefore selectById(long id);

    List<SendEntityBefore> selectAll() throws SelectFailedException;

    boolean isAlive();

    boolean insert(SendEntityBefore sendEntityBefore);

    boolean deleteById(long id);

    List<SendEntityBefore> selectAllById(long id);

    boolean insertAll(List<SendEntityBefore> list);
}

