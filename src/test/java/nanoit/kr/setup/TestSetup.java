package nanoit.kr.setup;


import nanoit.kr.repository.MessageRepository;
import nanoit.kr.service.MessageService;
import nanoit.kr.service.MessageServiceImpl;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.io.IOException;
import java.util.Properties;

public class TestSetup {
    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.5-alpine");
    Properties properties;

    public static MessageRepository messageRepository;
    public static MessageService messageService;

    public TestSetup() throws IOException {
        properties = new Properties();
        properties.setProperty("driver", postgreSQLContainer.getDriverClassName());
        properties.setProperty("url", postgreSQLContainer.getJdbcUrl());
        properties.setProperty("username", postgreSQLContainer.getUsername());
        properties.setProperty("password", postgreSQLContainer.getPassword());
        properties.setProperty("mapper", "POSTGRESQL.xml");


        messageRepository = MessageRepository.createMessageRepository(properties);
        messageService = new MessageServiceImpl(messageRepository);
    }
}