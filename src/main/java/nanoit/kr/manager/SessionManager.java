package nanoit.kr.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nanoit.kr.InternalQueue;
import nanoit.kr.db.DataBaseSessionManager;
import nanoit.kr.db.DatabaseHandler;
import nanoit.kr.domain.PropertyDto;
import nanoit.kr.exception.SessionManagerException;
import nanoit.kr.main.ConfigSetting;
import nanoit.kr.module.Filter;
import nanoit.kr.module.Insert;
import nanoit.kr.module.Mapper;
import nanoit.kr.module.ModuleProcessManagerImpl;
import nanoit.kr.scheduler.DataBaseScheduler;
import nanoit.kr.service.MessageService;
import nanoit.kr.thread.SessionResource;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class SessionManager {
    private final List<PropertyDto> userList;
    private final ConcurrentHashMap<PropertyDto, SessionResource> currentlySession;
    private ScheduledExecutorService scheduledExecutorService;
    private final ConfigSetting configSetting;

    // 1. 초기화 단계에서 currentlyUserList에는 아무런 값이 없음
    // 2. 최초 실행시 currentlyUserList에 List값이 다 담김
    // 3. curretlyUserList에 값이 없는 경우 세션 생성
    // 4. currentlyUserList 사이즈 만큼 세션 발생
    // 5. Sqlsession을 생성하여 공통된 repository와 service를 사용할 수 있도록 함
    // 6. session마다 모든 자원 생성 및 할당

    public SessionManager() {
        this.configSetting = new ConfigSetting();
        this.currentlySession = new ConcurrentHashMap<>();
        this.userList = new ArrayList<>();
        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
    }

    public void start() {
        scheduledExecutorService.scheduleAtFixedRate(makeSession, 0, 5, TimeUnit.MINUTES);
        scheduledExecutorService.scheduleAtFixedRate(resourceManagement, 0, 1, TimeUnit.SECONDS);

    }

    public Runnable makeSession = () -> {

        try {
            List<PropertyDto> list = configSetting.setting();
            for (PropertyDto userConfig : list) {
                if (!currentlySession.containsKey(userConfig)) {

                    // 관리 되어야 하는 것
                    // Sqlsession, Queue,MessageService, databaseScheduler, ModuleProccessManager

                    // Config Setting
                    DataBaseSessionManager dataBaseSessionManager = new DataBaseSessionManager(userConfig);
                    // sqlSession 따로 빼서 resource 에 넣고 관리하기

                    SqlSession sqlSession = dataBaseSessionManager.getSqlSession(true);

                    DatabaseHandler databaseHandler = new DatabaseHandler(userConfig);
                    InternalQueue queue = new InternalQueue();
                    MessageService messageService = databaseHandler.getMessageService(sqlSession);

                    if (!messageService.isAlive()) {
                        throw new SessionManagerException("세션을 생성 하는 과정에서 테이블이 생성되지 않아 alive 체크 실패됨");
                    }


                    DataBaseScheduler dataBaseScheduler = new DataBaseScheduler(messageService, queue, userConfig);

                    new Mapper(getRandomUuid(), queue);
                    new Filter(getRandomUuid(), queue);
                    new Insert(getRandomUuid(), queue, messageService);


                    ModuleProcessManagerImpl moduleProcessManager = new ModuleProcessManagerImpl();
                    if (!moduleProcessManager.isSuccessToStart()) {
                        throw new SessionManagerException("Module 생성에 대한 오류 발생 ");
                    }

                    SessionResource sessionResource = new SessionResource(messageService, sqlSession, queue, moduleProcessManager, dataBaseScheduler, dataBaseSessionManager);
                    currentlySession.put(userConfig, sessionResource);
                }
            }
        } catch (SessionManagerException e) {
            log.error("[SESSION-MANAGER] {} ", e.getReason());
            //TODO 예외처리에 대한 핸들링 로직 추가해야됨 , SqlSession에 대한 예외처리 클래스 생성 혹은 관리 하는 주체 구현
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public Runnable resourceManagement = () -> {
        try {
            for (SessionResource resource : currentlySession.values()) {
                // TODO 생성된 session에 exception이나 RuntimeExceptino이 발생시 핸들링 할수 있는 코드 성 해야됨
                if (resource.getSqlSession().getConnection().isClosed()) {
                    resource.reStartSqlSession();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public void removeSession(PropertyDto userConfig) {
        currentlySession.remove(userConfig);
    }

    private String getRandomUuid() {
        return UUID.randomUUID().toString();
    }
}


