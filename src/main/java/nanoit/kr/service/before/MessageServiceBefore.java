package nanoit.kr.service.before;

import nanoit.kr.domain.entity.SendAckEntity;
import nanoit.kr.domain.message.Send;

import java.util.List;

public interface MessageServiceBefore {

    // 1. 테이블 생성 되었는지에 대한 ping 채크
    // 2. 테이블 갯수 정하는 메소드 지원
    // 3. 테이블에 생성된 레코드들 가져오는 selectAll을 하며 동시에 update selected 를 진행
    // 4. 정상적으로 스케쥴러가 select 하고 update 가 되었으면 queue에 넣고 sendResult update

    // 5. receive 메시지 테이블에 update 한다

    boolean isAlive();

    long count();

    List<Send> selectAll() throws InterruptedException;

    boolean updateSendResults(List<Long> ids);

    boolean updateReceiveResult(SendAckEntity sendAck);

    boolean updateSendResult(long id);

}
