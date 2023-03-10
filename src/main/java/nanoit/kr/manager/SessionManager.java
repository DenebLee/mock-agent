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
            // initialize -> PropertyDto ???????????? session ??????
            // ?????? ????????? ?????? ???????????? ???????????? list ??? ??????
            // ???????????? ???????????? ???
            // 1. scheduler (???????????? ??????????????? ?????? ?????? ???????????? ??????????????? ?????? x)
            // 2. SqlSessionFactory (???????????? ??????????????? ?????? ???????????? ?????? ????????? ??????
            settings.addListDtoAndFile();

            for (PropertyDto property : settings.getPropertyDtoList()) {

                // Insert ???????????? key????????? resource??? ?????? ??? ?????? ?????? ?????? ???
                //  Repository??? ??????????????? ????????? dbms??? ??????????????? ????????? ?????? ????????? key?????? ?????? ??????
                String resourceKey = UUID.randomUUID().toString();

                // ?????? ???????????? ?????? ????????? dto??? ?????? InitialSetting ?????? ?????? ?????? ??????
                if (!property.isUsed()) {

                    // resource key?????? uuid ??????
                    // duplicateKey??? resourceKey + dbname;
                    // ???????????? ???????????? resourceKey ????????? dbname??? ???????????? ?????? (?????? DB??? ????????? ?????? ??????)
                    Socket socket = new Socket();

                    // ???????????? ?????? ??????????????? ?????? DBMS??? ???????????? ??? ??????
                    if (isDuplicateScheduler(property) != null) {
                        String duplicateKey = isDuplicateScheduler(property);
                        DataBaseScheduler scheduler = getScheduler(duplicateKey);
                        MessageRepository repository = getRepository(duplicateKey);
                        socket.connect(new InetSocketAddress(property.getTcpUrl(), property.getPort()));
                        SessionResource resource = new SessionResource(resourceKey, socket, property, internalQueue,repository);

                        // PropertyDto ??? resource??? ??????
                        if (!registerResourcePropertyDto(resourceKey, property, resource)) {

                            throw new SessionManagerException("[SESSION-MGR] PropertyDto and Session Resource Reigst Failed");
                        }

                        resource.start();

                    } else {

                        // ?????? ???????????? dbms??? ????????? ????????? ?????? duplicateKey??? resource?????? ?????????
                        String duplicateKey = resourceKey + property.getDbName();

                        // repository ?????? -> ??????????????? pool ?????? ????????? ?????? ????????? dto ?????????
                        MessageRepository messageRepository = MessageRepository.createMessageRepository(property);

                        if (!messageRepository.commonPing()) {

                            messageRepository.createTable();
                        }

                        // DB?????? select ?????? ???????????? ??????
                        DataBaseScheduler scheduler = new DataBaseScheduler(duplicateKey, messageRepository, internalQueue);

                        // Schudler ???????????? ???????????? ???????????? regist
                        if (!schedulerManager.registeScheduler(duplicateKey, scheduler)) {

                            throw new SessionManagerException("[SESSION-MGR] Scheduler reigster to ScheduleManager Failed");
                        }

                        // ????????? ?????? queue ??????
                        internalQueue.registSendQueue(duplicateKey);

                        // ??? ????????? ????????? ?????? SessionResource ??????
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
        // ???????????? ?????? resourceKey??? ????????? messageUuid??? ???????????? DuplicateKey?????? ????????? ???
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
            System.out.println("????????? ?????????? ????????? 4");
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
                // ?????? ????????? ????????? PropertyDto??? DB URL??? ?????? dto??? DB URL??? ?????????
                return entry.getKey(); // ??????????????? ????????? ????????? ???????????? ???????????? ?????? uuid return
            }
        }
        return null; // ????????? ???????????? null ??????
    }
}



