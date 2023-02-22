package nanoit.kr.service.before;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.domain.entity.MessageEntity;
import nanoit.kr.domain.entity.SendAckEntity;
import nanoit.kr.domain.message.Send;
import nanoit.kr.exception.SelectFailedException;
import nanoit.kr.exception.UpdateFailedException;
import nanoit.kr.repository.before.MessageRepositoryBefore;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class MessageServiceImplBefore implements MessageServiceBefore {
    private final MessageRepositoryBefore messageRepositoryBefore;

    public MessageServiceImplBefore(MessageRepositoryBefore messageRepositoryBefore) {
        this.messageRepositoryBefore = messageRepositoryBefore;
    }


    @Override
    public boolean isAlive() {
        try {
            return messageRepositoryBefore.commonPing();
        } catch (SelectFailedException e) {
            log.error(e.getReason());
            messageRepositoryBefore.createTable();
        }
        return false;
    }

    @Override
    public long count() {
        try {
            return messageRepositoryBefore.commonCount();
        } catch (SelectFailedException e) {
            log.error(e.getReason());
        }
        return 0;
    }

    @Override
    public List<Send> selectAll() {
        try {
            List<MessageEntity> messageEntities = messageRepositoryBefore.sendSelectAll();
            if (messageEntities.isEmpty()) {
                throw new SelectFailedException("[MSG-SERVICE] No messages found");
            }

            if (!messageRepositoryBefore.selectedUpdate(messageEntities)) {
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
    public boolean updateSendResults(List<Long> ids) {
        try {
            return messageRepositoryBefore.sendResultUpdates(ids);
        } catch (UpdateFailedException e) {
            log.error(e.getReason());
        }
        return false;
    }

    @Override
    public boolean updateSendResult(long id) {
        try {
            return messageRepositoryBefore.sendResultUpdate(id);
        } catch (UpdateFailedException e) {
            log.error(e.getReason());
        }
        return false;
    }

    @Override
    public boolean updateReceiveResult(SendAckEntity sendAck) {
        try {
            return messageRepositoryBefore.receiveUpdate(sendAck);
        } catch (UpdateFailedException e) {
            log.error(e.getReason());

        }
        return false;
    }

}
