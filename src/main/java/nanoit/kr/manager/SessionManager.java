package nanoit.kr.manager;

import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import nanoit.kr.domain.PropertyDto;
import nanoit.kr.exception.SessionManagerException;
import nanoit.kr.queue.InternalQueueImpl;
import nanoit.kr.repository.MessageRepository;
import nanoit.kr.scheduler.DataBaseScheduler;
import nanoit.kr.session.SessionResource;
import nanoit.kr.unclassified.InitialSettings;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


@Slf4j
public class SessionManager {
    private List<PropertyDto> initializeList;
    private ConcurrentHashMap<String, Pair<PropertyDto, SessionResource>> currentlySessionList;
    private ConcurrentHashMap<String, Pair<DataBaseScheduler, MessageRepository>> schedulerRepositoryList;
    private ScheduledExecutorService scheduledExecutorService;

    private InitialSettings settings;
    private DataBaseScheduler scheduler;
    private SchedulerManager manager;

    // For resource
    private InternalQueueImpl queue;

    public SessionManager(SchedulerManager manager, InternalQueueImpl queue) {
        this.manager = manager;
        this.settings = new InitialSettings();
        this.currentlySessionList = new ConcurrentHashMap<>();
        this.schedulerRepositoryList = new ConcurrentHashMap<>();
        this.queue = queue;
        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(2);
    }

    public void start() {
        List<PropertyDto> initialDtoList = settings.getPropertyDtoList();
        log.info("[SESSION-MGR] Session manager Start");
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
            settings.addListDtoAndFile();

            for (PropertyDto property : settings.getPropertyDtoList()) {
                if (property.isUsed()) {
                    String uuid = UUID.randomUUID().toString();
                    property.setUsed(true);

                    log.info("들어오는 DTO 값 확인  : {} ", property);
                    if (!currentlySessionList.containsKey(uuid)) {
                        MessageRepository messageRepository = MessageRepository.createMessageRepository(property);

                        if (!messageRepository.commonPing()) {
                            throw new SessionManagerException("[SESSION-MGR] DB is not Alive");
                        }
                        Socket socket = new Socket();

                        if (isDuplicateScheduler(property) != null) {
                            // 만약 계정의 dto에 url값이 currentlyList 에 있는 dto값과 일치할 경우 해당 계정의 scheduler 인스턴스를 가짐
                            String duplicateKey = isDuplicateScheduler(property);
                            DataBaseScheduler scheduler = getScheduler(duplicateKey);
                            MessageRepository repository = getRepository(duplicateKey);

                        }


                        DataBaseScheduler dataBaseScheduler = new DataBaseScheduler(resource);

                        manager.registeScheduler(uuid, new DataBaseScheduler(resource));

                        if (!registerResourcePropertyDto(uuid, property, resource)) {
                            throw new SessionManagerException("[SESSION-MGR] SessionResource Registed Failed");
                        }
                        if (!registerSchedulerRepository(uuid, scheduler, messageRepository)) {
                            throw new SessionManagerException("[SESSION-MGR] Session Registed Failed");
                        }
                    }
                }
            }
        } catch (SessionManagerException e) {
            log.error("[SESSION-MGR] {} ", e.getReason());
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    private Runnable resourceManagement = () -> {
        for (Map.Entry<String, Pair<PropertyDto, SessionResource>> entry : currentlySessionList.entrySet()) {
            String uuid = entry.getKey();
            Pair<PropertyDto, SessionResource> pair = entry.getValue();
            PropertyDto property = pair.getKey();
            SessionResource resource = pair.getValue();


        }
        for (Map.Entry<String, Pair<DataBaseScheduler, MessageRepository>> entry : schedulerRepositoryList.entrySet()) {
            String uuid = entry.getKey();
            Pair<DataBaseScheduler, MessageRepository> pair = entry.getValue();
            DataBaseScheduler scheduler = pair.getKey();
            MessageRepository messageRepository = pair.getValue();


        }
    };

    //    =============================================================================================================================================================================================================
    //    =============================================================================================================================================================================================================
    public void cleanUpResourcesWhenAuthenticationFailure(String key) throws IOException {
        SessionResource resource = getResource(key);
        if (resource.kill()) {
            unregisterResource(key);
            unregisterSession(key);
        }
    }

    public PropertyDto getPropertyDto(String key) {
        Pair<PropertyDto, SessionResource> pair = currentlySessionList.get(key);
        return pair.getKey();
    }
    public SessionResource getResource(String key) {
        Pair<PropertyDto, SessionResource> pair = currentlySessionList.get(key);
        return pair.getValue();
    }

    public DataBaseScheduler getScheduler(String key) {
        Pair<DataBaseScheduler, MessageRepository> pair = schedulerRepositoryList.get(key);
        return pair.getKey();
    }

    public MessageRepository getRepository(String key) {
        Pair<DataBaseScheduler, MessageRepository> pair = schedulerRepositoryList.get(key);
        return pair.getValue();
    }

    private boolean registerSchedulerRepository(String uuid, DataBaseScheduler scheduler, MessageRepository repository) {
        if (uuid == null || scheduler == null || repository == null) {
            return false;
        }
        if (schedulerRepositoryList.containsKey(uuid)) {
            return false;
        }
        Pair<DataBaseScheduler, MessageRepository> pair = schedulerRepositoryList.get(uuid);
        if (pair == null) {
            return false;
        }
        if (pair.getKey().equals(scheduler) || pair.getValue().equals(repository)) {
            return false;
        }
        schedulerRepositoryList.put(uuid, new Pair<>(scheduler, repository));
        return true;
    }

    private void unregisterSession(String uuid) {
        schedulerRepositoryList.remove(uuid);
    }

    private boolean registerResourcePropertyDto(String uuid, PropertyDto dto, SessionResource resource) {
        if (uuid == null || resource == null || dto == null) {
            return false;
        }
        if (currentlySessionList.containsKey(uuid)) {
            return false;
        }
        Pair<PropertyDto, SessionResource> pair = currentlySessionList.get(uuid);
        if (pair == null) {
            return false;
        }
        if (pair.getKey().equals(dto) || pair.getValue().equals(resource)) {
            return false;
        }
        currentlySessionList.put(uuid, new Pair<>(dto, resource));
        return true;
    }

    private void unregisterResource(String key) {
        currentlySessionList.remove(key);
    }

    private String isDuplicateScheduler(PropertyDto dto) {
        for (Map.Entry<String, Pair<PropertyDto, SessionResource>> entry : currentlySessionList.entrySet()) {
            PropertyDto existingDto = entry.getValue().getKey();
            if (existingDto.getUrl().equals(dto.getUrl())) {
                // 만약 기존에 저장된 PropertyDto의 DB URL이 현재 dto의 DB URL과 같다면
                return entry.getKey(); // 중복이므로 기존에 생성된 인스턴스 가져오기 위한 uuid return
            }
        }
        return null; // 중복이 아니므로 null 반환
    }
}



