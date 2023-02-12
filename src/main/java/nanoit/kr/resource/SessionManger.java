package nanoit.kr.resource;

import nanoit.kr.domain.PropertyDto;
import nanoit.kr.main.ConfigSetting;
import nanoit.kr.thread.ThreadResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SessionManger {
    private List<PropertyDto> userList;
    private HashMap<PropertyDto, ThreadResource> currentlyUseList;
    private ScheduledExecutorService scheduledExecutorService;
    private ConfigSetting configSetting;

    public void SessionManager(){
        this.configSetting = new ConfigSetting();
        this.currentlyUseList = new HashMap<>();
        this.userList = new ArrayList<>();
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(session,0,5, TimeUnit.MINUTES);
    }

    public Runnable session = new Runnable() {
        @Override
        public void run() {
            try {
                List<PropertyDto> list = configSetting.setting();
                for (PropertyDto userConfig:list) {
                    // 현재 manager 가 보유중인 Property map 에 등록이 되어 있지 않으면
                    if(!currentlyUseList.containsKey(userConfig)){
                        // agent 필요한 설정 세팅한 resource 클래스 생성 후 currentHashmap에 등록하기
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
