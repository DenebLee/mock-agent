package nanoit.kr.repository;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.io.IOException;
import java.util.Properties;

public class RepositoryTestSetUp {
    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.5-alpine");
    Properties properties;

    public static ReceiveMessageRepository receiveMessageRepository;
    public static SendMessageRepository sendMessageRepository;

    public RepositoryTestSetUp(String type) throws IOException {
        properties = new Properties();
        properties.setProperty("driver", postgreSQLContainer.getDriverClassName());
        properties.setProperty("url", postgreSQLContainer.getJdbcUrl());
        properties.setProperty("username", postgreSQLContainer.getUsername());
        properties.setProperty("password", postgreSQLContainer.getPassword());
        properties.setProperty("mapper", type + "_POSTGRESQL.xml");

        if (type.equals("RECEIVE")) {
            receiveMessageRepository = ReceiveMessageRepository.createReceiveRepository(properties);
        } else if (type.equals("SEND")) {
            sendMessageRepository = SendMessageRepository.createSendRepository(properties);
        }
    }
}
