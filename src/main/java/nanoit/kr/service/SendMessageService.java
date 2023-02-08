package nanoit.kr.service;

import nanoit.kr.domain.entity.SendEntity;
import nanoit.kr.domain.message.MessageStatus;
import nanoit.kr.domain.message.Send;

import java.util.List;

public interface SendMessageService {

    List<SendEntity> selectSendMessages();

    boolean updateSendMessageStatus(long id, MessageStatus messageStatus);

    boolean deleteAllSendMessage();

    long count();

    boolean isAlive();

    List<SendEntity> selectSendMessagesById(long id);

    boolean insert(SendEntity send);

    boolean insertAll(List<SendEntity> list);
}
