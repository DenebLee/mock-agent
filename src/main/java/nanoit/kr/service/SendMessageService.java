package nanoit.kr.service;

import nanoit.kr.domain.before.SendEntityBefore;
import nanoit.kr.domain.message.MessageStatus;

import java.util.List;

public interface SendMessageService {

    List<SendEntityBefore> selectSendMessages();

    boolean updateSendMessageStatus(long id, MessageStatus messageStatus);

    boolean deleteAllSendMessage();

    long count();

    boolean isAlive();

    List<SendEntityBefore> selectSendMessagesById(long id);

    boolean insert(SendEntityBefore send);

    boolean insertAll(List<SendEntityBefore> list);
}
