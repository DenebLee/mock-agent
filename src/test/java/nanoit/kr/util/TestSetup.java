package nanoit.kr.util;

import nanoit.kr.repository.ReceiveMessageRepository;
import nanoit.kr.repository.SendMessageRepository;
import nanoit.kr.service.ReceiveMessageService;
import nanoit.kr.service.ReceiveMessageServiceImpl;
import nanoit.kr.service.SendMessageService;
import nanoit.kr.service.SendMessageServiceImpl;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.io.IOException;
import java.util.Properties;

public class TestSetup {
    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.5-alpine");
    Properties properties;

    public static ReceiveMessageRepository receiveMessageRepository;
    public static SendMessageRepository sendMessageRepository;

    public static ReceiveMessageService receiveMessageService;
    public static SendMessageService sendMessageService;

    public TestSetup(String type) throws IOException {
        properties = new Properties();
        properties.setProperty("driver", postgreSQLContainer.getDriverClassName());
        properties.setProperty("url", postgreSQLContainer.getJdbcUrl());
        properties.setProperty("username", postgreSQLContainer.getUsername());
        properties.setProperty("password", postgreSQLContainer.getPassword());
        properties.setProperty("mapper", type + "_POSTGRESQL.xml");

        if (type.equals("RECEIVE")) {
            receiveMessageRepository = ReceiveMessageRepository.createReceiveRepository(properties);
            receiveMessageService = new ReceiveMessageServiceImpl(receiveMessageRepository);
        } else if (type.equals("SEND")) {
            sendMessageRepository = SendMessageRepository.createSendRepository(properties);
            sendMessageService = new SendMessageServiceImpl(sendMessageRepository);
        }
    }
}
