package nanoit.kr.db;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.repository.ReceiveMessageRepository;
import nanoit.kr.repository.SendMessageRepository;
import nanoit.kr.service.ReceiveMessageService;
import nanoit.kr.service.ReceiveMessageServiceImpl;
import nanoit.kr.service.SendMessageService;
import nanoit.kr.service.SendMessageServiceImpl;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public class DatabaseHandler {
    private final Properties properties;


    public DatabaseHandler(Properties properties) {
        this.properties = properties;
    }

    public ReceiveMessageService getReceivedMessageService() throws IOException {
        ReceiveMessageRepository receiveMessageRepository = ReceiveMessageRepository.createReceiveRepository(properties);
        return new ReceiveMessageServiceImpl(receiveMessageRepository);
    }

    public SendMessageService getSendMessageService() throws IOException {
        SendMessageRepository sendMessageRepository = SendMessageRepository.createSendRepository(properties);
        return new SendMessageServiceImpl(sendMessageRepository);
    }

}
