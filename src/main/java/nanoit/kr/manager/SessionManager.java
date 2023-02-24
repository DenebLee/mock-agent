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
import java.net.InetSocketAddress;
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

    private final ConcurrentHashMap<String, Pair<PropertyDto, SessionResource>> currentlySessionList;
    private final ConcurrentHashMap<String, Pair<DataBaseScheduler, MessageRepository>> schedulerRepositoryList;
    private ScheduledExecutorService scheduledExecutorService;

    private InitialSettings settings;
    private DataBaseScheduler scheduler;
    private SchedulerManager schedulerManager;
    private InternalQueueImpl internalQueue;


    public SessionManager(SchedulerManager schedulerManager, InternalQueueImpl internalQueue) {
        this.internalQueue = internalQueue;
        this.schedulerManager = schedulerManager;
        this.settings = new InitialSettings();
        this.currentlySessionList = new ConcurrentHashMap<>();
        this.schedulerRepositoryList = new ConcurrentHashMap<>();
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

                // Insert 모듈에서 key값으로 resource를 찾을 때 사용 혹은 관리 용
                //  Repository와 스케줄러는 동일한 dbms를 사용한다면 같아야 하기 때문에 key값을 따로 나눔
                String resourceKey = UUID.randomUUID().toString();

                // 현재 사용되지 않는 계정의 dto값 앞서 InitialSetting 에서 검증 단계 거침
                if (!property.isUsed()) {

                    // resource key값은 uuid 원값
                    // duplicateKey는 resourceKey + dbname;
                    // 메시지를 보낼때는 resourceKey 값에서 dbname을 추가해서 보냄 (같은 DB를 사용할 경우 유용)
                    Socket socket = new Socket();

                    // 사용되지 않는 계정이지만 같은 DBMS를 사용하는 지 여부
                    if (isDuplicateScheduler(property) != null) {
                        String duplicateKey = isDuplicateScheduler(property);
                        DataBaseScheduler scheduler = getScheduler(duplicateKey);
                        MessageRepository repository = getRepository(duplicateKey);
                        socket.connect(new InetSocketAddress(property.getTcpUrl(), property.getPort()));
                        SessionResource resource = new SessionResource(resourceKey, socket, property, internalQueue,repository);

                        // PropertyDto 와 resource만 등록
                        if (!registerResourcePropertyDto(resourceKey, property, resource)) {

                            throw new SessionManagerException("[SESSION-MGR] PropertyDto and Session Resource Reigst Failed");
                        }

                        resource.start();

                    } else {

                        // 최초 실행이며 dbms의 유일한 계정이 경우 duplicateKey는 resource키와 동일함
                        String duplicateKey = resourceKey + property.getDbName();

                        // repository 생성 -> 마이바티스 pool 생성 로직도 포함 그래서 dto 넘겨줌
                        MessageRepository messageRepository = MessageRepository.createMessageRepository(property);

                        if (!messageRepository.commonPing()) {

                            messageRepository.createTable();
                        }

                        // DB에서 select 하는 스케줄러 생성
                        DataBaseScheduler scheduler = new DataBaseScheduler(duplicateKey, messageRepository, internalQueue);

                        // Schudler 단독으로 관리하는 매니저에 regist
                        if (!schedulerManager.registeScheduler(duplicateKey, scheduler)) {

                            throw new SessionManagerException("[SESSION-MGR] Scheduler reigster to ScheduleManager Failed");
                        }

                        // 사용할 전송 queue 등록
                        internalQueue.registSendQueue(duplicateKey);

                        // 한 계정의 정보를 담은 SessionResource 생성
                        socket.connect(new InetSocketAddress(property.getTcpUrl(), property.getPort()));
                        SessionResource resource = new SessionResource(resourceKey, socket, property, internalQueue,messageRepository);

                        resource.start();
                        if (!registerResourcePropertyDto(resourceKey, property, resource)) {
                            throw new SessionManagerException("[SESSION-MGR] PropertyDto and Session Resource Reigst Failed");
                        }
                        if (!registerSchedulerRepository(duplicateKey, scheduler, messageRepository)) {
//                            throw new SessionManagerException("[SESSION-MGR] Scheduler and Repository Reigst Failed");
                        }
                    }
                }
            }
        } catch (SessionManagerException e) {
            log.error(e.getReason());
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    private Runnable resourceManagement = () -> {
//        for (Map.Entry<String, Pair<PropertyDto, SessionResource>> entry : currentlySessionList.entrySet()) {
//            String uuid = entry.getKey();
//            Pair<PropertyDto, SessionResource> pair = entry.getValue();
//            PropertyDto property = pair.getKey();
//            SessionResource resource = pair.getValue();
//        }
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
        // 범용성을 위해 resourceKey로 줄때와 messageUuid로 들어가는 DuplicateKey둘다 받도록 함
        if (key.length() > 36) {
            key = key.substring(0, 36);
        }
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
        log.info("[SESSION-MGR] Scheduler and Repository Registed !!");
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
        if (pair.getKey().equals(dto) || pair.getValue().equals(resource)) {
            System.out.println("여기서 걸리나? 확인용 4");
            return false;
        }
        dto.setUsed(true);
        currentlySessionList.put(uuid, new Pair<>(dto, resource));
        log.info("[SESSION-MGR] PropertyDto and SessionResource Registed !!");
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



