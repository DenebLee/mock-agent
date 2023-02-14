package nanoit.kr.repository;

import nanoit.kr.db.DataBaseSessionManagerTest;
import nanoit.kr.domain.PropertyDto;
import nanoit.kr.domain.entity.MessageEntity;
import nanoit.kr.domain.before.SendAckEntityBefore;
import nanoit.kr.domain.before.SendEntityBefore;
import nanoit.kr.exception.DeleteFailedException;
import nanoit.kr.exception.InsertFailedException;
import nanoit.kr.exception.SelectFailedException;
import nanoit.kr.exception.UpdateFailedException;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;
import java.util.List;

/*
    1. agent_table 생성
    2. message select 여부에 대한 테이블 생성
    3. message 전송과 응답에 대한 테이블 생성
    4. 외래키 세팅
    5. 외래키를 담당하는 테이블에 초기값 insert
 */

public class MessageRepositoryImpl implements MessageRepository {

    private final DataBaseSessionManagerTest sessionManager;

    public MessageRepositoryImpl(PropertyDto dto) throws IOException {
        this.sessionManager = new DataBaseSessionManagerTest(dto);
        settingPreferences();
    }

    @Override
    public void settingPreferences() {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            session.update("createTable");
            session.update("createdMessage_selected");
            session.update("createMessageResultTable");
            session.update("foreignKeySet");
            session.insert("insertResult");
            session.insert("message_selected_insert");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean insertAgentId(long agentId) {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            int a = session.insert("agent_id_insert", agentId);
            if (a > 0) {
                return true;
            }
        } catch (Exception e) {
            throw new InsertFailedException("Failed to Insert Agent Id => " + e.getMessage());
        }
        return false;
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
    public boolean commonPing() {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            return session.selectOne("ping");
        } catch (Exception e) {
            throw new SelectFailedException("The agent Table is not Created => " + e.getMessage());
        }
    }

    @Override
    public boolean commonDeleteTable() {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            int a = session.delete("deleteTable");
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
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            int a = session.delete("deleteByid", id);
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
        // 프로시저 이용해서 select 한 직후 바로 update 해서 selected 데이터 삽입 가능 하도록
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            return session.selectOne("selectById", id);
        } catch (Exception e) {
            throw new SelectFailedException("Failed to Delete Receive Message => " + e.getMessage());
        }
    }

    @Override
    public boolean insert(MessageEntity message) {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            int a = session.insert("insert", message);
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
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            int a = session.insert("insertAll", list);
            if (a > 0) {
                return true;
            }
        } catch (Exception e) {
            throw new InsertFailedException("Failed to Insert All Data => " + e.getMessage());
        }
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
    public SendAckEntityBefore receiveSelectById(long id) {
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
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            int a = session.update("receive_update", id);
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
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            return session.selectList("send_selectAll");
        } catch (Exception e) {
            throw new SelectFailedException("Failed to Select All Message => " + e.getMessage());
        }
    }

    @Override
    public boolean selectedUpdate(List<MessageEntity> list) {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            int a = session.update("send_select_update", list);
            if (a > 0) {
                return true;
            }
        } catch (Exception e) {
            throw new UpdateFailedException("Failed to Update Send Message Result => " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean sendResultUpdate(long id) {
        try (SqlSession session = sessionManager.getSqlSession(true)) {
            int a = session.update("send_result_update", id);
            if (a > 0) {
                return true;
            }
        } catch (Exception e) {
            throw new UpdateFailedException("Failed to Update Send Result => " + e.getMessage());
        }
        return false;
    }
}
