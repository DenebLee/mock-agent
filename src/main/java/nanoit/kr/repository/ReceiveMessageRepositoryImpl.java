package nanoit.kr.repository;

import ch.qos.logback.core.encoder.EchoEncoder;
import nanoit.kr.db.DataBaseSessionManager;
import nanoit.kr.domain.entity.SendAckEntity;
import nanoit.kr.exception.DeleteFailedException;
import nanoit.kr.exception.InsertFailedException;
import nanoit.kr.exception.SelectFailedException;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class ReceiveMessageRepositoryImpl implements ReceiveMessageRepository {

    private final DataBaseSessionManager sessionManager;

    public ReceiveMessageRepositoryImpl(Properties properties) throws IOException {
        this.sessionManager = new DataBaseSessionManager(properties);

    }

    @Override
    public long count() {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            return session.selectOne("count");
        }
    }

    @Override
    public boolean deleteById(long messageId) {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            int result = session.delete("deleteById");
            if (result == 1) {
                return true;
            } else if (result < 1) {
                return false;
            }
        } catch (Exception e) {
            throw new DeleteFailedException("Failed to Delete Receive Message => " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deleteAll() {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            int result = session.delete("deleteAll");
            if (result > 0) {
                return true;
            } else if (result == 0) {
                return false;
            }
        } catch (Exception e) {
            throw new DeleteFailedException("Failed to Delete All Receive Message => " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<SendAckEntity> selectAll() throws SelectFailedException {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            List<SendAckEntity> listData = session.selectList("selectAll");
            if (!listData.isEmpty()) {
                return listData;
            }
        } catch (Exception e) {
            throw new SelectFailedException("Failed to Select Receive Messages => " + e.getMessage());
        }
        return null;
    }


    @Override
    public boolean insert(SendAckEntity sendAck) {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            int result = session.insert("insert", sendAck);
            if (result > 0) {
                return true;
            } else if (result == 0) {
                return false;
            }
        } catch (Exception e) {
            throw new InsertFailedException("Failed to Insert Receive Message");
        }
        return false;
    }

    @Override
    public boolean alive() {
        try (SqlSession session = sessionManager.getSqlSession(true)) {

        }
        return false;
    }
}
