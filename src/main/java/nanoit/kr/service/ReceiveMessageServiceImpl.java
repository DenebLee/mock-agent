package nanoit.kr.service;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.domain.entity.SendAckEntity;
import nanoit.kr.domain.message.SendAck;
import nanoit.kr.exception.DeleteFailedException;
import nanoit.kr.exception.InsertFailedException;
import nanoit.kr.repository.ReceiveMessageRepository;

import java.sql.Timestamp;


@Slf4j
public class ReceiveMessageServiceImpl implements ReceiveMessageService {

    private final ReceiveMessageRepository receiveMessageRepository;
    public boolean isSettingCompleted;

    public ReceiveMessageServiceImpl(ReceiveMessageRepository receiveMessageRepository) {
        this.receiveMessageRepository = receiveMessageRepository;
        isSettingCompleted = false;
    }

    @Override
    public boolean insertReceiveMessage(SendAck sendAck) {
        try {
            SendAckEntity sendAckEntity = sendAck.toEntity();
            return receiveMessageRepository.insert(sendAckEntity);
        } catch (InsertFailedException e) {
            log.error(e.getReason());
        }
        return false;
    }

    @Override
    public boolean deleteReceiveMessage(long id) {
        try {
            return receiveMessageRepository.deleteById(id);
        } catch (DeleteFailedException e) {
            log.error(e.getReason());
        }
        return false;
    }


    @Override
    public boolean deleteAllReceiveMessage() {
        try {
            return receiveMessageRepository.deleteAll();
        } catch (DeleteFailedException e) {
            log.error(e.getReason());
        }
        return false;
    }


    @Override
    public boolean isAlive() {
        return receiveMessageRepository.isAlive();
    }

}