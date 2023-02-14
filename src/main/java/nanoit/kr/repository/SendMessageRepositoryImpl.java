package nanoit.kr.repository;

import nanoit.kr.db.DataBaseSessionManager;
import nanoit.kr.domain.before.SendEntityBefore;
import nanoit.kr.exception.DeleteFailedException;
import nanoit.kr.exception.InsertFailedException;
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
        createTable();
    }

    private void createTable() {
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
    public boolean updateMessageStatus(SendEntityBefore sendEntityBefore) {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            int result = session.update("send_updateMessageStatus", sendEntityBefore);
            if (result > 0) {
                return true;
            }
        } catch (Exception e) {
            throw new UpdateFailedException("Failed to Update MessageStatus => " + e.getMessage());
        }
        return false;
    }

    @Override
    public SendEntityBefore selectById(long id) {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            return session.selectOne("send_selectById", id);
        } catch (Exception e) {
            throw new SelectFailedException("Failed to Select Send Message used Id => " + e.getMessage());
        }
    }

    @Override
    public List<SendEntityBefore> selectAll() {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            List<SendEntityBefore> selectList = session.selectList("send_selectAll");
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
            throw new SelectFailedException("The Send Table is not Created => " + e.getMessage());
        }
    }

    @Override
    public boolean insert(SendEntityBefore send) {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            int result = session.insert("send_insert", send);
            if (result > 0) {
                return true;
            } else if (result == 0) {
                return false;
            }
        } catch (Exception e) {
            throw new InsertFailedException("Failed to Insert Send Table =>" + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean deleteById(long id) {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            int result = session.delete("send_deleteById", id);
            if (result > 0) {
                return true;
            } else if (result == 0) {
                return false;
            }
        } catch (Exception e) {
            throw new DeleteFailedException("Failed to Delete by Id => " + e.getMessage());
        }
        return false;
    }

    @Override
    public List<SendEntityBefore> selectAllById(long id) {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            List<SendEntityBefore> selectList = session.selectList("test", id);
            if (selectList.isEmpty()) {
                return null;
            }
            return selectList;
        } catch (Exception e) {
            throw new SelectFailedException("Failed to Select Send MessagesById => " + e.getMessage());
        }
    }

    @Override
    public boolean insertAll(List<SendEntityBefore> list) {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            int a = session.insert("send_insertAll", list);
            return a == list.size();
        } catch (Exception e) {
            throw new SelectFailedException("Failed to Select Send MessagesById => " + e.getMessage());
        }
    }
}
