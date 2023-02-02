package nanoit.kr.service;

import nanoit.kr.domain.message.SendAck;

import java.util.List;

public interface ReceiveMessageService {

    boolean insertReceiveMessage(SendAck sendAck);

    boolean deleteReceiveMessage(long id);

    boolean deleteAllReceiveMessage();

    boolean isAlive();
}
