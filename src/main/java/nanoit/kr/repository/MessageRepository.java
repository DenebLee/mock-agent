package nanoit.kr.repository;

import nanoit.kr.domain.PropertyDto;
import nanoit.kr.domain.entity.MessageEntity;
import nanoit.kr.domain.before.SendAckEntityBefore;

import java.io.IOException;
import java.util.List;

/*
    count -  사용 가능한 엔티티 수를 반환 ✔
    delete (entity) - 지정된 엔티티를 삭제 ✔
    deleteAll () - 리포지토리에서 관리하는 모든 엔티티를 삭제
    deleteAllByCondition(Iterable<? extends ID> ids) - 지정된 ID를 가진 T 유형의 모든 인스턴스를 삭제
    deleteById(ID id) - 지정된 ID의 엔티티를 삭제
    update(entity) - 지정된 id의 엔티티 수정
    existsById(ID id) - 지정된 ID를 가진 엔티티가 있는지 여부를 반환
    selectAll() - 형식의 모든 인스턴스를 반환 ✔
    selectById(Iterable<ID> ids) - 지정된 ID를 가진 T 유형의 모든 인스턴스를 반환
    insert(S entity) - 지정된 엔티티 저장 ✔
    insertAll(Iterable<S> entities) - 지정된 모든 엔티티 저장 ✔
 */

public interface MessageRepository {
    static MessageRepository createMessageRepository(PropertyDto dto) throws IOException {
        return new MessageRepositoryImpl(dto) {
        };
    }

    void settingPreferences();

    boolean insertAgentId(long agentId);


    // Common Method
    long commonCount();

    boolean commonDeleteTable();

    boolean commonPing();

    boolean commonDeleteById(long id);

    MessageEntity selectById(long id);

    boolean insert(MessageEntity message);

    boolean insertAll(List<MessageEntity> list);


    // Receive Method
    long receiveCount();

    SendAckEntityBefore receiveSelectById(long id);

    List<MessageEntity> receiveSelectAll();

    boolean receiveUpdate(long id);


    // Send Method
    List<MessageEntity> sendSelectAll();

    boolean selectedUpdate(List<MessageEntity> list);

    boolean sendResultUpdate(long id);

}

