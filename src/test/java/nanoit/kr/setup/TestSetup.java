package nanoit.kr.setup;


import nanoit.kr.repository.before.MessageRepositoryBefore;
import nanoit.kr.service.before.MessageServiceBefore;
import nanoit.kr.service.before.MessageServiceImplBefore;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.io.IOException;
import java.util.Properties;

public class TestSetup {
    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.5-alpine");
    Properties properties;

    public static MessageRepositoryBefore messageRepositoryBefore;
    public static MessageServiceBefore messageServiceBefore;

    public TestSetup() throws IOException {
        properties = new Properties();
        properties.setProperty("driver", postgreSQLContainer.getDriverClassName());
        properties.setProperty("url", postgreSQLContainer.getJdbcUrl());
        properties.setProperty("username", postgreSQLContainer.getUsername());
        properties.setProperty("password", postgreSQLContainer.getPassword());
        properties.setProperty("mapper", "POSTGRESQL.xml");


        messageRepositoryBefore = MessageRepositoryBefore.createMessageRepository(properties);
        messageServiceBefore = new MessageServiceImplBefore(messageRepositoryBefore);
    }
}