package nanoit.kr.thread;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nanoit.kr.InternalQueue;
import nanoit.kr.db.DataBaseSessionManager;
import nanoit.kr.module.ModuleProcessManagerImpl;
import nanoit.kr.scheduler.DataBaseScheduler;
import nanoit.kr.service.MessageService;
import org.apache.ibatis.session.SqlSession;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class SessionResource {
    private AtomicBoolean readThreadStatus;
    private AtomicBoolean writeThreadStatus;
    private AtomicBoolean authenticationStatus;

    @Getter
    private final MessageService messageService;
    @Getter
    private SqlSession sqlSession;
    @Getter
    private final InternalQueue queue;
    @Getter
    private final ModuleProcessManagerImpl moduleProcessManager;
    @Getter
    private final DataBaseScheduler dataBaseScheduler;

    @Getter
    private final DataBaseSessionManager dataBaseSessionManager;

    public SessionResource(MessageService messageService, SqlSession sqlSession, InternalQueue queue, ModuleProcessManagerImpl moduleProcessManager, DataBaseScheduler dataBaseScheduler, DataBaseSessionManager dataBaseSessionManager) {
        this.messageService = messageService;
        this.sqlSession = sqlSession;
        this.queue = queue;
        this.moduleProcessManager = moduleProcessManager;
        this.dataBaseScheduler = dataBaseScheduler;
        this.dataBaseSessionManager = dataBaseSessionManager;
    }


    public void reStartSqlSession() throws SQLException {
        sqlSession.clearCache();
        sqlSession.close();
        if (sqlSession.getConnection().isClosed()) {
            sqlSession = dataBaseSessionManager.getSqlSession(true);
        }
    }

}
