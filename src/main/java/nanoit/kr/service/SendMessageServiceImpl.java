package nanoit.kr.service;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.domain.before.SendEntityBefore;
import nanoit.kr.domain.message.MessageStatus;
import nanoit.kr.exception.DeleteFailedException;
import nanoit.kr.exception.InsertFailedException;
import nanoit.kr.exception.SelectFailedException;
import nanoit.kr.exception.UpdateFailedException;
import nanoit.kr.repository.SendMessageRepository;

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
    public List<SendEntityBefore> selectSendMessages() {
        try {
            return sendMessageRepository.selectAll();
        } catch (SelectFailedException e) {
            log.error(e.getReason());
        }
        return null;
    }

    @Override
    public boolean updateSendMessageStatus(long id, MessageStatus messageStatus) {
        try {
            SendEntityBefore sendEntityBefore = new SendEntityBefore();
            sendEntityBefore.setStatus(messageStatus);
            return sendMessageRepository.updateMessageStatus(sendEntityBefore);
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

    @Override
    public List<SendEntityBefore> selectSendMessagesById(long id) {
        try {
            return sendMessageRepository.selectAllById(id);
        } catch (SelectFailedException e) {
            log.error(e.getReason());
        }
        return null;
    }

    @Override
    public boolean insert(SendEntityBefore send) {
        try {
            return sendMessageRepository.insert(send);
        } catch (InsertFailedException e) {
            log.error(e.getReason());
        }
        return false;
    }

    @Override
    public boolean insertAll(List<SendEntityBefore> list) {
        try {
            return sendMessageRepository.insertAll(list);
        } catch (InsertFailedException e) {
            log.error(e.getReason());
        }
        return false;
    }
}
