package nanoit.kr.service;

import nanoit.kr.domain.entity.SendEntity;
import nanoit.kr.domain.message.Send;

import java.util.List;

public interface SendMessageService {

    List<Send> selectSendMessages();

    boolean updateSendMessageStatus(SendEntity sendEntity);

    boolean deleteSendMessage();

    long count();

    boolean isAlive();
}
