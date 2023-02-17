package nanoit.kr.service;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.domain.entity.MessageEntity;
import nanoit.kr.domain.entity.SendAckEntity;
import nanoit.kr.domain.message.Send;
import nanoit.kr.exception.SelectFailedException;
import nanoit.kr.exception.UpdateFailedException;
import nanoit.kr.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }


    @Override
    public boolean isAlive() {
        try {
            return messageRepository.commonPing();
        } catch (SelectFailedException e) {
            log.error(e.getReason());
            messageRepository.createTable();
        }
        return false;
    }

    @Override
    public long count() {
        try {
            return messageRepository.commonCount();
        } catch (SelectFailedException e) {
            log.error(e.getReason());
        }
        return 0;
    }

    @Override
    public List<Send> selectAll() {
        List<MessageEntity> messageEntities = new ArrayList<>();
        List<Send> sendList = new ArrayList<>();
        try {
            messageEntities = messageRepository.sendSelectAll();
            if (messageEntities.isEmpty()) {
                throw new Exception();
            }
            if (!messageRepository.selectedUpdate(messageEntities)) {
                throw new Exception();
            }
            for (MessageEntity message : messageEntities) {
                Send send = new Send();
                send
                        .setMessageId(message.getId())
                        .setSenderName(message.getSenderName())
                        .setCallbackNumber(message.getCallbackNumber())
                        .setPhoneNumber(message.getPhoneNumber())
                        .setContent(message.getContent());
                sendList.add(send);
            }
            return sendList;

        } catch (SelectFailedException e) {
            log.error(e.getReason());
        } catch (UpdateFailedException e) {
            log.error(e.getReason());
            messageEntities.clear();
        } catch (Exception e) {
            log.error(e.getMessage());
            messageEntities.clear();
            sendList.clear();
            return null;
        }
        return null;
    }

    @Override
    public boolean updateSendResult(long id) {
        try {
            return messageRepository.sendResultUpdate(id);
        } catch (UpdateFailedException e) {
            log.error(e.getReason());
        }
        return false;
    }

    @Override
    public boolean updateReceiveResult(SendAckEntity sendAck) {
        try {
            return messageRepository.receiveUpdate(sendAck);
        } catch (UpdateFailedException e) {
            log.error(e.getReason());

        }
        return false;
    }

}
