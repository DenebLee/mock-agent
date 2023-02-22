package nanoit.kr.session;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nanoit.kr.db.DatabaseHandler;
import nanoit.kr.domain.PropertyDto;
import nanoit.kr.exception.SessionManagerException;
import nanoit.kr.queue.InternalQueueImpl;
import nanoit.kr.repository.MessageRepository;
import nanoit.kr.scheduler.DataBaseScheduler;
import nanoit.kr.thread.SessionResource;
import nanoit.kr.unclassified.InitialSettings;

import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class SessionManager {
    private List<PropertyDto> userList;
    private ConcurrentHashMap<String, PropertyDto> currentlySession;
    private ConcurrentHashMap<String, SessionResource> currentlyResources;
    private ConcurrentHashMap<PropertyDto, DataBaseScheduler> schedulerConcurrentHashMap;
    private ScheduledExecutorService scheduledExecutorService;
    private final InitialSettings settings;
    private DataBaseScheduler scheduler;

    // For resource
    private InternalQueueImpl queue;

    // 1. 초기화 단계에서 currentlyUserList에는 아무런 값이 없음
    // 2. 최초 실행시 currentlyUserList에 List값이 다 담김
    // 3. curretlyUserList에 값이 없는 경우 세션 생성
    // 4. currentlyUserList 사이즈 만큼 세션 발생
    // 5. Sqlsession을 생성하여 공통된 repository와 service를 사용할 수 있도록 함
    // 6. session마다 모든 자원 생성 및 할당


    public SessionManager(InternalQueueImpl queue, List<PropertyDto> list) {
        this.settings = new InitialSettings();
        this.currentlySession = new ConcurrentHashMap<>();
        this.currentlyResources = new ConcurrentHashMap<>();
        this.schedulerConcurrentHashMap = new ConcurrentHashMap<>();
        this.userList = list;

        this.queue = queue;
        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(2);
    }

    public void start() {
        System.out.println("세션 매니저 시작");
        scheduledExecutorService.scheduleAtFixedRate(makeSession, 0, 5, TimeUnit.MINUTES);
        scheduledExecutorService.scheduleAtFixedRate(resourceManagement, 0, 1, TimeUnit.SECONDS);
    }

    private Runnable makeSession = () -> {
        try {
            // initialize -> PropertyDto 갯수만큼 session 생성

            // 최초 실행은 메인 스레드가 넘겨주는 list 로 해결
            // 세션마다 할당되는 것
            // 1. scheduler (계정마다 생성되지만 만약 같은 테이블로 접근한다면 생성 x)
            // 2. SqlSessionFactory (계정마다 생성되지만 같은 테이블일 경우 하나만 생성
            for (PropertyDto property : userList) {
                String uuid = UUID.randomUUID().toString();
                if (!currentlySession.containsKey(uuid)) {
                    MessageRepository messageRepository = MessageRepository.createMessageRepository(property);

                    if (!messageRepository.commonPing()) {
                        throw new SessionManagerException("[SESSION-MANAGER] DB is not Alive");
                    }

                    if (isDuplicateScheduler(property)) {

                    }
                    scheduler = new DataBaseScheduler(messageRepository);

                    Socket socket = new Socket();
                    SessionResource resource = new SessionResource(socket, queue, property, messageRepository,scheduler);
                    if (!registerResource(uuid, resource)) {
                        throw new SessionManagerException("[SESSION-MANAGER] SessionResource Registed Failed");
                    }
                    if (!registerSession(uuid, property)) {
                        throw new SessionManagerException("[SESSION-MANAGER] Session Registed Failed");
                    }
                }
            }
        } catch (SessionManagerException e) {
            log.error("[SESSION-MANAGER] {} ", e.getReason());
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    private Runnable resourceManagement = () -> {
        try {
            for (Map.Entry<String, PropertyDto> entry : currentlySession.entrySet()) {

            }
            for (Map.Entry<String, SessionResource> entry : currentlyResources.entrySet()) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    private boolean isDuplicateScheduler(PropertyDto dto) {
        for (Map.Entry<String, PropertyDto> data : currentlySession.entrySet()) {
            if (data.getValue().getUrl().equals(dto.getUrl())) {
                return true;
            }
        }
        return false;
    }


    private boolean registerSession(String uuid, PropertyDto propertyDto) {
        if (uuid == null) {
            return false;
        }
        if (propertyDto == null) {
            return false;
        }
        return currentlySession.put(uuid, propertyDto) == null;
    }

    private boolean unregisterSession(String uuid) {
        return currentlySession.remove(uuid) == null;
    }

    private boolean registerResource(String uuid, SessionResource resource) {
        if (uuid == null) {
            return false;
        }
        if (resource == null) {
            return false;
        }
        return currentlyResources.put(uuid, resource) == null;
    }

    private boolean unregisterResource(String uuid) {
        return currentlyResources.remove(uuid) == null;
    }

    public int getCurrentlySessionListSize() {
        return currentlySession.size();
    }

    public int getCurrentlyResourceListSize() {
        return currentlyResources.size();
    }

    public PropertyDto getSession(String uuid) {
        return currentlySession.get(uuid);
    }

    public SessionResource getResource(String uuid) {
        return currentlyResources.get(uuid);
    }


    private void removeSession(PropertyDto userConfig) {
        currentlySession.remove(userConfig);
    }
}



