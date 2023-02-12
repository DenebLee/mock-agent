package nanoit.kr.repository;

import com.google.protobuf.Message;
import nanoit.kr.db.DataBaseSessionManager;
import nanoit.kr.db.DataBaseSessionManagerTest;
import nanoit.kr.domain.PropertyDto;
import nanoit.kr.domain.entity.SendAckEntity;
import nanoit.kr.exception.DeleteFailedException;
import nanoit.kr.exception.InsertFailedException;
import nanoit.kr.exception.SelectFailedException;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class MessageRepositoryImpl implements MessageRepository {

    private final DataBaseSessionManagerTest sessionManager;

    public MessageRepositoryImpl(PropertyDto dto) throws IOException {
        this.sessionManager = new DataBaseSessionManagerTest(dto);
        settingPreferences();
    }

    private void settingPreferences() {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            session.update("createTable");
            session.update("createMessageResultTable");
            session.update("foreignKeySet");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public SendAckEntity selectById(long id) {
        return null;
    }

    @Override
    public boolean deleteById(long id) {
        return false;
    }

    @Override
    public boolean deleteAll() {
        return false;
    }

    @Override
    public List<SendAckEntity> selectAll() {
        return null;
    }

    @Override
    public boolean insert(SendAckEntity sendAck) {
        return false;
    }

    @Override
    public boolean isAlive() {
        return false;
    }
}
