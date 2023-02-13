package nanoit.kr.repository;

import nanoit.kr.db.DataBaseSessionManagerTest;
import nanoit.kr.domain.PropertyDto;
import nanoit.kr.domain.entity.MessageEntity;
import nanoit.kr.exception.SelectFailedException;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;
import java.util.List;

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
    public long commonCount() {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            return session.selectOne("count");
        } catch (Exception e) {
            throw new SelectFailedException("Failed to Total count lookup failed => " + e.getMessage());
        }
    }

    @Override
    public boolean commonDeleteTable() {
        return false;
    }

    @Override
    public boolean commonPing() {
        return false;
    }

    @Override
    public boolean commonDeleteById(long id) {
        return false;
    }

    @Override
    public boolean isExistById(long id) {
        try (SqlSession session = sessionManager.getSqlSession(true)) {


        } catch (Exception e) {
            throw new SelectFailedException("Failed to Delete Receive Message => " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean insert(MessageEntity message) {
        return false;
    }

    @Override
    public boolean insertAll(List<MessageEntity> list) {
        return false;
    }


    // Receive
    @Override
    public long receiveCount() {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            return session.selectOne("receive_count");
        } catch (Exception e) {
            throw new SelectFailedException("Failed to Total count lookup failed => " + e.getMessage());
        }
    }

    @Override
    public MessageEntity receiveSelectById(long id) {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            return session.selectOne("receive_selectById", id);
        } catch (Exception e) {
            throw new SelectFailedException("Failed to Select By Id => " + e.getMessage());
        }
    }

    @Override
    public List<MessageEntity> receiveSelectAll() {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            return session.selectList("receive_selectAll");
        } catch (Exception e) {
            throw new SelectFailedException("Failed to Select By Id => " + e.getMessage());
        }
    }

    @Override
    public boolean receiveUpdate(long id) {
        return false;
    }


    // Send


}
