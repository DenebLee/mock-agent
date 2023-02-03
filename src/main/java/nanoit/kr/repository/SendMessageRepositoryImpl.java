package nanoit.kr.repository;

import nanoit.kr.db.DataBaseSessionManager;
import nanoit.kr.domain.entity.SendEntity;
import nanoit.kr.exception.DeleteFailedException;
import nanoit.kr.exception.SelectFailedException;
import nanoit.kr.exception.UpdateFailedException;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class SendMessageRepositoryImpl implements SendMessageRepository {
    private final DataBaseSessionManager sessionManager;

    public SendMessageRepositoryImpl(Properties properties) throws IOException {
        this.sessionManager = new DataBaseSessionManager(properties);
        createSendTable();
    }

    private void createSendTable() {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            session.update("createSendTable");
        } catch (Exception e) {
            throw new UpdateFailedException("Failed to Create Send Table => " + e.getMessage());
        }
    }

    @Override
    public long count() {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            int count = session.selectOne("send_count");
            if (count != 0) {
                return count;
            }
        } catch (Exception e) {
            throw new SelectFailedException("Failed to Check the number of Messages => " + e.getMessage());
        }
        return 0;
    }

    @Override
    public boolean deleteAll() {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            int result = session.delete("send_deleteAll");
            if (result > 0) {
                return true;
            } else if (result == 0) {
                return false;
            }
        } catch (Exception e) {
            throw new DeleteFailedException("Failed to Delete All Send Message => " + e.getMessage());
        }
        return false;
    }

    @Override
    public int updateMessageStatus(SendEntity sendEntity) {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            int result = session.update("send_updateMessageStatus", sendEntity);
            if (result > 0) {
                return result;
            }
        } catch (Exception e) {
            throw new UpdateFailedException("Failed to Update MessageStatus => " + e.getMessage());
        }
        return 0;
    }

    @Override
    public SendEntity selectById(long id) {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            SendEntity sendEntity = session.selectOne("send_selectById", id);
            if (sendEntity != null) {
                return sendEntity;
            }
        } catch (Exception e) {
            throw new SelectFailedException("Failed to Select Send Message used Id => " + e.getMessage());
        }
        return null;
    }


    // 최초 select 할때 마지막 id 기준으로 보다 큰 id 값 들 가져오도록
    // status 가 null 인 것들

    // 두개의 조건이면 뚫리지 않나?
    @Override
    public List<SendEntity> selectAll() {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            List<SendEntity> selectList = session.selectList("send_selectAll");
            if (!selectList.isEmpty()) {
                return selectList;
            }
        } catch (Exception e) {
            throw new SelectFailedException("Failed to Select Send Messages => " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean isAlive() {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            return session.selectOne("send_ping");
        } catch (Exception e) {
            throw new SelectFailedException("The Receive Table is not Created => " + e.getMessage());
        }
    }
}
