package nanoit.kr.service;

import nanoit.kr.domain.before.SendAckBefore;

public interface ReceiveMessageService {

    boolean insertReceiveMessage(SendAckBefore sendAckBefore);

    boolean deleteReceiveMessage(long id);

    boolean deleteAllReceiveMessage();

    boolean isAlive();
}
