package nanoit.kr.repository;

import nanoit.kr.domain.before.SendAckEntityBefore;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

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

public interface ReceiveMessageRepository {
    static ReceiveMessageRepository createReceiveRepository(Properties properties) throws IOException {
        return new ReceiveMessageRepositoryImpl(properties) {
        };
    }

    long count();

    SendAckEntityBefore selectById(long id);

    boolean deleteById(long id);

    boolean deleteAll();

    List<SendAckEntityBefore> selectAll();

    boolean insert(SendAckEntityBefore sendAck);

    boolean isAlive();


}

