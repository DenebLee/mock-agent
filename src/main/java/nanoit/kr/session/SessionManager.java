//package nanoit.kr.session;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import nanoit.kr.domain.PropertyDto;
//import nanoit.kr.exception.SessionManagerException;
//import nanoit.kr.main.ConfigSetting;
//import nanoit.kr.queue.InternalQueueImpl;
//import nanoit.kr.thread.SessionResource;
//
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//@Slf4j
//@RequiredArgsConstructor
//public class SessionManager {
//    private final List<PropertyDto> userList;
//    private final ConcurrentHashMap<String, PropertyDto> currentlySession;
//    private final ConcurrentHashMap<String, SessionResource> currentlyResources;
//    private ScheduledExecutorService scheduledExecutorService;
//    private final ConfigSetting configSetting;
//
//    // For resource
//    private final Socket socket;
//    private final InternalQueueImpl queue;
//
//    // 1. 초기화 단계에서 currentlyUserList에는 아무런 값이 없음
//    // 2. 최초 실행시 currentlyUserList에 List값이 다 담김
//    // 3. curretlyUserList에 값이 없는 경우 세션 생성
//    // 4. currentlyUserList 사이즈 만큼 세션 발생
//    // 5. Sqlsession을 생성하여 공통된 repository와 service를 사용할 수 있도록 함
//    // 6. session마다 모든 자원 생성 및 할당
//
//
//    public SessionManager(Socket socket, InternalQueueImpl queue) {
//        this.configSetting = new ConfigSetting();
//        this.currentlySession = new ConcurrentHashMap<>();
//        this.currentlyResources = new ConcurrentHashMap<>();
//        this.userList = new ArrayList<>();
//        this.socket = socket;
//        this.queue = queue;
//        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(2);
//    }
//
//    public void start() {
//        scheduledExecutorService.scheduleAtFixedRate(makeSession, 0, 5, TimeUnit.MINUTES);
//        scheduledExecutorService.scheduleAtFixedRate(resourceManagement, 0, 1, TimeUnit.SECONDS);
//    }
//
//    private Runnable makeSession = () -> {
//        try {
//            List<PropertyDto> propertyDtoList = configSetting.getList();
//            for (PropertyDto property : propertyDtoList) {
//                String uuid = UUID.randomUUID().toString();
//                if (!currentlySession.containsKey(uuid)) {
//                    SessionResource resource = new SessionResource(socket, queue, property);
//                    if (!registerResource(uuid, resource)) {
//                        throw new SessionManagerException("[SESSION-MANAGER] SessionResource Registed Failed");
//                    }
//                    if (!registerSession(uuid, property)) {
//                        throw new SessionManagerException("[SESSION-MANAGER] Session Registed Failed");
//                    }
//                }
//            }
//        } catch (SessionManagerException e) {
//            log.error("[SESSION-MANAGER] {} ", e.getReason());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    };
//
//    private Runnable resourceManagement = () -> {
//        try {
//            for (Map.Entry<String, PropertyDto> entry : currentlySession.entrySet()) {
//
//            }
//            for (Map.Entry<String, SessionResource> entry : currentlyResources.entrySet()) {
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    };
//
//
//    private boolean registerSession(String uuid, PropertyDto propertyDto) {
//        if (uuid == null) {
//            return false;
//        }
//        if (propertyDto == null) {
//            return false;
//        }
//        return currentlySession.put(uuid, propertyDto) == null;
//    }
//
//    private boolean unregisterSession(String uuid) {
//        return currentlySession.remove(uuid) == null;
//    }
//
//    private boolean registerResource(String uuid, SessionResource resource) {
//        if (uuid == null) {
//            return false;
//        }
//        if (resource == null) {
//            return false;
//        }
//        return currentlyResources.put(uuid, resource) == null;
//    }
//
//    private boolean unregisterResource(String uuid) {
//        return currentlyResources.remove(uuid) == null;
//    }
//
//    public int getCurrentlySessionListSize() {
//        return currentlySession.size();
//    }
//
//    public int getCurrentlyResourceListSize() {
//        return currentlyResources.size();
//    }
//
//    public PropertyDto getSession(String uuid) {
//        return currentlySession.get(uuid);
//    }
//
//    public SessionResource getResource(String uuid) {
//        return currentlyResources.get(uuid);
//    }
//
//
//    private void removeSession(PropertyDto userConfig) {
//        currentlySession.remove(userConfig);
//    }
//}
//
//
//
