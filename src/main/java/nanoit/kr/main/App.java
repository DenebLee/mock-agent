package nanoit.kr.main;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.TemporaryQueue;
import nanoit.kr.resource.SessionManger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


@Slf4j
public class App {

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");

    public static void main(String[] args) {
        try {
            // agent 기본 세팅 값 로딩 및 설정
            TemporaryQueue queue = new TemporaryQueue();
            // Session Manager start
            SessionManger sessionManger = new SessionManger();

            System.out.println();
            System.out.println("=================================================================================================================================================================================================");
            System.out.println("=================================================================================================================================================================================================");
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getRandomUuid() {
        return UUID.randomUUID().toString();
    }
}