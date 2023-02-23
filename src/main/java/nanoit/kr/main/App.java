package nanoit.kr.main;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.manager.SchedulerManager;
import nanoit.kr.manager.SessionManager;
import nanoit.kr.module.Filter;
import nanoit.kr.module.Insert;
import nanoit.kr.module.Mapper;
import nanoit.kr.queue.InternalQueueImpl;
import nanoit.kr.unclassified.InitialSettings;

import java.util.UUID;


@Slf4j
public class App {
    public static void main(String[] args) {
        try {
            InitialSettings settings = new InitialSettings();
            // config 파일에 SMS.xml 파일이 존재하지 않으면 사용자가 설정 값을 넣을 수 있도록 xml 예제 파일 하나 생성해줌
            if (!settings.isConfigDirectoryExist()) {
                log.info("[SYSTEM] Systemfile does not exist in config Folder");
                settings.makeConfigSettingXmlFile();
            }
            // list에 add하는 부분 오류나면 시작단계이기에 프로그램 중단되도록
            if (!settings.addListDtoAndFile()) {
                System.exit(-1);
            }

            InternalQueueImpl queue = new InternalQueueImpl();

            SchedulerManager schedulerMGR = new SchedulerManager();
            SessionManager sessionMGR = new SessionManager(schedulerMGR, queue);

            new Mapper(getRandomUuid(), queue);
            new Filter(getRandomUuid(), queue, sessionMGR);
            new Insert(getRandomUuid(), queue, sessionMGR);

            schedulerMGR.start();
            sessionMGR.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getRandomUuid() {
        return UUID.randomUUID().toString();
    }
}