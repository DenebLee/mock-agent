package nanoit.kr.main;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.TemporaryQueue;
import nanoit.kr.db.DatabaseHandler;
import nanoit.kr.module.Filter;
import nanoit.kr.module.Insert;
import nanoit.kr.module.Mapper;
import nanoit.kr.scheduler.DataBaseSchedulerBefore;
import nanoit.kr.scheduler.DataBaseSchedulerForInsertData;
import nanoit.kr.service.ReceiveMessageService;
import nanoit.kr.service.SendMessageService;
import nanoit.kr.thread.ThreadResource;
import org.apache.ibatis.io.Resources;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;


@Slf4j
public class AppBackUp {



    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");

    public static void main(String[] args) {
        try {

            Properties properties = new Properties();
            Socket socket = new Socket();
            InputStream propertiesStream = Resources.getResourceAsStream("resource.properties");


            if (propertiesStream != null) {
                properties.load(propertiesStream);

                DatabaseHandler databaseHandler = new DatabaseHandler(properties);
                TemporaryQueue queue = new TemporaryQueue();
                SendMessageService sendMessageService = databaseHandler.getSendMessageService();
                ReceiveMessageService receivedMessageService = databaseHandler.getReceivedMessageService();


                if (sendMessageService.isAlive() && receivedMessageService.isAlive()) {


                    socket.connect(new InetSocketAddress(properties.getProperty("tcp.url"), Integer.parseInt(properties.getProperty("tcp.port"))));


                    DataBaseSchedulerBefore dataBaseSchedulerBefore = new DataBaseSchedulerBefore(sendMessageService, queue);

                    ThreadResource threadResource = new ThreadResource(receivedMessageService, sendMessageService, socket, queue, properties);


                    if (socket.isConnected()) {
                        threadResource.start();
                        new Mapper(getRandomUuid(), queue);
                        new Filter(getRandomUuid(), queue, threadResource);
                        new Insert(getRandomUuid(), queue, receivedMessageService);
                    }
                    DataBaseSchedulerForInsertData insertDataScheduler = new DataBaseSchedulerForInsertData(sendMessageService);

                    System.out.println();
                    System.out.println("=================================================================================================================================================================================================");
                    System.out.println("                                                                    AGENT START -- " + SIMPLE_DATE_FORMAT.format(new Date()));
                    System.out.println("                                                                    CONNECT SUCCESS  -- " + socket.getInetAddress());
                    System.out.println("                                                                                                                                                                                          ");
                    System.out.println("                                                                                                                                                                                          ");
                    System.out.println("                                                                    USER ID         ===    " + properties.getProperty("user.name"));
                    System.out.println("                                                                    USER AGENT      ===    " + properties.getProperty("user.agent"));
                    System.out.println("                                                                    USER DATABASE   ===    " + properties.getProperty("user.database"));
                    System.out.println("=================================================================================================================================================================================================");
                    System.out.println();
                } else {
                    log.error("[APP] Error creating table and setting environment!!");
                    throw new Exception();
                }
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