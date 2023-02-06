package nanoit.kr.service;

import nanoit.kr.domain.message.MessageStatus;
import nanoit.kr.domain.message.Send;

import java.util.List;

public interface SendMessageService {

    List<Send> selectSendMessages();

    boolean updateSendMessageStatus(long id, MessageStatus messageStatus);

    boolean deleteAllSendMessage();

    long count();

    boolean isAlive();
}
