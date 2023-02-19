package nanoit.kr.service;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.domain.entity.MessageEntity;
import nanoit.kr.domain.entity.SendAckEntity;
import nanoit.kr.domain.message.Send;
import nanoit.kr.exception.SelectFailedException;
import nanoit.kr.exception.UpdateFailedException;
import nanoit.kr.repository.MessageRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        try {
            List<MessageEntity> messageEntities = messageRepository.sendSelectAll();
            if (messageEntities.isEmpty()) {
                throw new SelectFailedException("[MSG-SERVICE] No messages found");
            }

            if (!messageRepository.selectedUpdate(messageEntities)) {
                throw new UpdateFailedException("[MSG-SERVICE] Error in updating messages");
            }

            List<Send> sendList = messageEntities.stream()
                    .map(message -> new Send()
                            .setMessageId(message.getId())
                            .setAgentId(message.getAgentId())
                            .setSenderName(message.getSenderName())
                            .setCallbackNumber(message.getCallbackNumber())
                            .setPhoneNumber(message.getPhoneNumber())
                            .setContent(message.getContent()))
                    .collect(Collectors.toList());

            return sendList;

        } catch (SelectFailedException | UpdateFailedException e) {
            log.error(e.getMessage());
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("[MSG-SERVICE] Error in selecting messages", e);
            return null;
        }
    }

    @Override
    public boolean updateSendResults(List<Long> ids) {
        try {
            return messageRepository.sendResultUpdates(ids);
        } catch (UpdateFailedException e) {
            log.error(e.getReason());
        }
        return false;
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
