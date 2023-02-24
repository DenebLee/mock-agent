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


    @Override
    public boolean isAlive(MessageRepository repository) {
        try {
            return repository.commonPing();
        } catch (SelectFailedException e) {
            log.error(e.getReason());
            repository.createTable();
        }
        return false;
    }

    @Override
    public long count(MessageRepository repository) {
        try {
            return repository.commonCount();
        } catch (SelectFailedException e) {
            log.error(e.getReason());
        }
        return 0;
    }

    @Override
    public List<Send> selectAll(MessageRepository repository) {
        try {
            List<MessageEntity> messageEntities = repository.sendSelectAll();
            if (!repository.selectedUpdate(messageEntities)) {
                throw new UpdateFailedException("[MSG-SERVICE] Error in updating messages");
            }

            return messageEntities.stream()
                    .map(message -> new Send()
                            .setMessageId(message.getId())
                            .setSenderName(message.getSenderName())
                            .setCallbackNumber(message.getCallbackNumber())
                            .setPhoneNumber(message.getPhoneNumber())
                            .setContent(message.getContent()))
                    .collect(Collectors.toList());

        } catch (SelectFailedException | UpdateFailedException e) {
            log.error(e.getMessage());
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("[MSG-SERVICE] Error in selecting messages", e);
            return null;
        }
    }

    @Override
    public boolean updateSendResults(MessageRepository repository, List<Long> ids) {
        try {
            return repository.sendResultUpdates(ids);
        } catch (UpdateFailedException e) {
            log.error(e.getReason());
        }
        return false;
    }

    @Override
    public boolean updateSendResult(MessageRepository repository, long id) {
        try {
            return repository.sendResultUpdate(id);
        } catch (UpdateFailedException e) {
            log.error(e.getReason());
        }
        return false;
    }

    @Override
    public boolean updateReceiveResult(MessageRepository repository, SendAckEntity sendAck) {
        try {
            return repository.receiveUpdate(sendAck);
        } catch (UpdateFailedException e) {
            log.error(e.getReason());

        }
        return false;
    }

}
