package nanoit.kr.main;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.db.DatabaseHandler;
import nanoit.kr.scheduler.DataBaseScheduler;
import nanoit.kr.service.ReceiveMessageService;
import nanoit.kr.service.SendMessageService;
import nanoit.kr.thread.ModuleProcess;
import nanoit.kr.thread.ModuleProcessManagerImpl;
import nanoit.kr.thread.ReceiveThread;
import nanoit.kr.thread.SendThread;
import org.apache.ibatis.io.Resources;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;


@Slf4j
public class Agent {

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");

    public static void main(String[] args) {
        try {

            Properties properties = new Properties();
            InputStream propertiesStream = Resources.getResourceAsStream("resource.properties");


            if (propertiesStream != null) {
                properties.load(propertiesStream);


                DatabaseHandler databaseHandler = new DatabaseHandler(properties);
                SendMessageService sendMessageService = databaseHandler.getSendMessageService();
                ReceiveMessageService receivedMessageService = databaseHandler.getReceivedMessageService();


                // 마이바티스 연동 완료 후 G/W에 socket 연결 시도
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(properties.getProperty("tcp.url"), Integer.parseInt(properties.getProperty("tcp.port"))));

                // socket 연결 성공시 스케쥴러 실행
                DataBaseScheduler dataBaseScheduler = new DataBaseScheduler(sendMessageService);

                // 스케쥴러에서는 select 된 메시지들 send_queue 에 담고 공유


                new ReceiveThread(getRandomUuid(), receivedMessageService);
                new SendThread(getRandomUuid(), sendMessageService);

                ModuleProcessManagerImpl moduleProcessManager = ModuleProcess.moduleProcessManagerImpl;


                System.out.println();
                System.out.println("==========================================================================================================================================================================================");
                System.out.println("                                                                    AGENT START -- " + SIMPLE_DATE_FORMAT.format(new Date()));
                System.out.println("                                                                    CONNECT SUCCESS  -- " + socket.getInetAddress());
                System.out.println("============================================================1==============================================================================================================================");
                System.out.println();

            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getRandomUuid() {
        return UUID.randomUUID().toString();
    }
}
