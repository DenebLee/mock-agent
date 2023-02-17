package nanoit.kr.service;

import nanoit.kr.domain.entity.SendAckEntity;
import nanoit.kr.domain.message.Send;

import java.util.List;

public interface MessageService {


    // 테이블 전체 칼럼 갯수 구하기
    // 전송할 메시지 전체 select 한 후 update
    // 전송이 완료되었으면 sendResult 업데이트
    // 응답 메시지 받으면 값 update

    boolean isAlive();

    long count();

    List<Send> selectAll() throws InterruptedException;

    boolean updateSendResult(long id);

    boolean updateReceiveResult(SendAckEntity sendAck);
}
