package nanoit.kr.service;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.domain.entity.SendEntity;
import nanoit.kr.domain.message.MessageStatus;
import nanoit.kr.domain.message.Send;
import nanoit.kr.exception.DeleteFailedException;
import nanoit.kr.exception.SelectFailedException;
import nanoit.kr.exception.UpdateFailedException;
import nanoit.kr.repository.SendMessageRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SendMessageServiceImpl implements SendMessageService {
    private final SendMessageRepository sendMessageRepository;
    public boolean isCreateTable;

    public SendMessageServiceImpl(SendMessageRepository sendMessageRepository) {
        this.sendMessageRepository = sendMessageRepository;
        isCreateTable = false;
    }

    @Override
    public List<Send> selectSendMessages() {
        try {
            List<Send> data = new ArrayList<>();
            for (SendEntity entry : sendMessageRepository.selectAll()) {
                data.add(entry.toDto());
            }
            return data;
        } catch (SelectFailedException e) {
            log.error(e.getReason());
        }
        return null;
    }

    @Override
    public boolean updateSendMessageStatus(long id, MessageStatus messageStatus) {
        try {
            SendEntity sendEntity = new SendEntity();
            sendEntity.setStatus(messageStatus);
            return sendMessageRepository.updateMessageStatus(sendEntity);
        } catch (UpdateFailedException e) {
            log.error(e.getReason());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteAllSendMessage() {
        try {
            return sendMessageRepository.deleteAll();
        } catch (DeleteFailedException e) {
            log.error(e.getReason());
        }
        return false;
    }

    @Override
    public long count() {
        try {
            return sendMessageRepository.count();
        } catch (SelectFailedException e) {
            log.error(e.getReason());
        }
        return 0;
    }

    @Override
    public boolean isAlive() {
        return sendMessageRepository.isAlive();
    }
}
