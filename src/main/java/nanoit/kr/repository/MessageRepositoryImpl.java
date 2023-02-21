package nanoit.kr.repository;

import nanoit.kr.db.DataBaseSessionManager;
import nanoit.kr.domain.entity.MessageEntity;
import nanoit.kr.domain.entity.SendAckEntity;
import nanoit.kr.exception.DeleteFailedException;
import nanoit.kr.exception.InsertFailedException;
import nanoit.kr.exception.SelectFailedException;
import nanoit.kr.exception.UpdateFailedException;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class MessageRepositoryImpl implements MessageRepository {

    private final DataBaseSessionManager sessionManager;

    public MessageRepositoryImpl(Properties prop) throws IOException {
        this.sessionManager = new DataBaseSessionManager(prop);
        createTable();
    }

    @Override
    public void createTable() {
        try (SqlSession sqlSession = sessionManager.getSqlSession(true)) {
            sqlSession.update("createTable");
        } catch (Exception e) {
            throw new UpdateFailedException("Failed to Create Table => " + e.getMessage());
        }
    }


    @Override
    public long commonCount() {
        try (SqlSession sqlSession = sessionManager.getSqlSession(true)) {
            return sqlSession.selectOne("count");
        } catch (Exception e) {
            throw new SelectFailedException("Failed to Total count lookup failed => " + e.getMessage());
        }
    }

    @Override
    public boolean commonPing() {
        try (SqlSession sqlSession = sessionManager.getSqlSession(true)) {
            return sqlSession.selectOne("ping");
        } catch (Exception e) {
            throw new SelectFailedException("The agent Table is not Created => " + e.getMessage());
        }
    }

    @Override
    public boolean commonDeleteTable() {
        try (SqlSession sqlSession = sessionManager.getSqlSession(true)) {
            int a = sqlSession.delete("deleteTable");
            if (a > 0) {
                return true;
            }
        } catch (Exception e) {
            throw new DeleteFailedException("Failed to Delete Data => " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean commonDeleteById(long id) {
        try (SqlSession sqlSession = sessionManager.getSqlSession(true)) {
            int a = sqlSession.delete("deleteById", id);
            if (a > 0) {
                return true;
            }
        } catch (Exception e) {
            throw new DeleteFailedException("Failed to Delete Data by Id => " + e.getMessage());
        }
        return false;
    }

    @Override
    public MessageEntity selectById(long id) {
        try (SqlSession sqlSession = sessionManager.getSqlSession(true)) {
            return sqlSession.selectOne("selectById", id);
        } catch (Exception e) {
            throw new SelectFailedException("Failed to Delete Receive Message => " + e.getMessage());
        }
    }

    @Override
    public boolean insert(MessageEntity message) {
        try (SqlSession sqlSession = sessionManager.getSqlSession(true)) {
            int a = sqlSession.insert("insert", message);
            if (a > 0) {
                return true;
            }
        } catch (Exception e) {
            throw new InsertFailedException("Failed to Insert Data => " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean insertAll(List<MessageEntity> list) {
        try (SqlSession sqlSession = sessionManager.getSqlSession(true)) {
            int a = sqlSession.insert("insertAll", list);
            if (a > 0) {
                return true;
            }
        } catch (Exception e) {
            throw new InsertFailedException("Failed to Insert All Data => " + e.getMessage());
        }
        return false;
    }


    @Override
    public long receiveCount() {
        try (SqlSession sqlSession = sessionManager.getSqlSession(true)) {
            return sqlSession.selectOne("receive_count");
        } catch (Exception e) {
            throw new SelectFailedException("Failed to Total count lookup failed => " + e.getMessage());
        }
    }

    @Override
    public MessageEntity receiveSelectById(long id) {
        try (SqlSession sqlSession = sessionManager.getSqlSession(true)) {
            return sqlSession.selectOne("receive_selectById", id);
        } catch (Exception e) {
            throw new SelectFailedException("Failed to Select By Id => " + e.getMessage());
        }
    }

    @Override
    public List<MessageEntity> receiveSelectAll() {
        try (SqlSession sqlSession = sessionManager.getSqlSession(true)) {
            return sqlSession.selectList("receive_selectAll");
        } catch (Exception e) {
            throw new SelectFailedException("Failed to Select By Id => " + e.getMessage());
        }
    }


    @Override
    public boolean receiveUpdate(SendAckEntity sendAck) {
        try (SqlSession sqlSession = sessionManager.getSqlSession(true)) {
            int a = sqlSession.update("receive_update", sendAck);
            if (a > 0) {
                return true;
            }
        } catch (Exception e) {
            throw new UpdateFailedException("Failed to Update Receive Message => " + e.getMessage());
        }
        return false;
    }


    // Send
    @Override
    public List<MessageEntity> sendSelectAll() {
        try (SqlSession sqlSession = sessionManager.getSqlSession(true)) {
            return sqlSession.selectList("send_selectAll");
        } catch (Exception e) {
            throw new SelectFailedException("Failed to Select All Message => " + e.getMessage());
        }
    }

    @Override
    public boolean selectedUpdate(List<MessageEntity> list) {
        try (SqlSession sqlSession = sessionManager.getSqlSession(true)) {
            int a = sqlSession.update("send_select_update", list);
            if (a > 0) {
                return true;
            }
        } catch (Exception e) {
            throw new UpdateFailedException("Failed to Update Send Message Result => " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean sendResultUpdates(List<Long> ids) {
        try (SqlSession sqlSession = sessionManager.getSqlSession(true)) {
            int a = sqlSession.update("send_result_updates", ids);
            if (a > 0) {
                return true;
            }
        } catch (Exception e) {
            throw new UpdateFailedException("Failed to Update Send Result => " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean sendResultUpdate(long id) {
        try (SqlSession sqlSession = sessionManager.getSqlSession(true)) {
            int a = sqlSession.update("send_result_update", id);
            if (a > 0) {
                return true;
            }
        } catch (Exception e) {
            throw new UpdateFailedException("Failed to update Send Result => " + e.getMessage());
        }
        return false;
    }
}
