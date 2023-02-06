package nanoit.kr.service;

import nanoit.kr.domain.entity.SendAckEntity;
import nanoit.kr.domain.message.MessageResult;
import nanoit.kr.domain.message.SendAck;
import nanoit.kr.util.TestSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
class ReceiveMessageServiceImplTest extends TestSetup {

    public ReceiveMessageServiceImplTest() throws IOException {
        super("RECEIVE");
    }

    @AfterEach
    void tearDown() {
        receiveMessageService.deleteAllReceiveMessage();
    }

    @DisplayName("send 테이블에 sendAck 의 값이 정상적으로 insert 되어야 한다")
    @Test
    void t1() {
        // given
        SendAck expected = new SendAck();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        expected
                .setResult(MessageResult.SUCCESS)
                .setCreatedAt(currentTime)
                .setLastModifiedAt(currentTime);

        // when
        boolean insertResult = receiveMessageService.insertReceiveMessage(expected);

        // then
        assertThat(insertResult).isTrue();
        SendAckEntity actual = receiveMessageRepository.selectById(1);
        assertThat(actual.getId()).isEqualTo(1);
        assertThat(actual.getResult()).isEqualTo(expected.getResult());
        assertThat(actual.getCreatedAt()).isEqualTo(expected.getCreatedAt());
        assertThat(actual.getLastModifiedAt()).isEqualTo(expected.getLastModifiedAt());

    }

    @DisplayName("deleteAllReceiveMessage 호출 시 receive 테이블에 있는 모든 데이터가 정상적으로 삭제 되어야 한다")
    @Test
    void t2() {
        // given
        SendAck expected = new SendAck();
        for (int i = 0; i < 10; i++) {
            expected
                    .setResult(MessageResult.SUCCESS)
                    .setCreatedAt(new Timestamp(System.currentTimeMillis()))
                    .setLastModifiedAt(new Timestamp(System.currentTimeMillis()));
            boolean insertResult = receiveMessageService.insertReceiveMessage(expected);
            assertThat(insertResult).isTrue();
        }

        // when
        boolean actual = receiveMessageService.deleteAllReceiveMessage();

        // then
        assertThat(actual).isTrue();
    }

    @DisplayName("deleteReceiveMessage 를 호출 시 receive 테이블에 있는 id 값에 해당하는 칼럼이 정상적으로 삭제 되어야 한다")
    @Test
    void t3() {
        // given
        SendAck expected = new SendAck();
        expected
                .setResult(MessageResult.SUCCESS)
                .setCreatedAt(new Timestamp(System.currentTimeMillis()))
                .setLastModifiedAt(new Timestamp(System.currentTimeMillis()));
        boolean insertResult = receiveMessageService.insertReceiveMessage(expected);

        // when
        boolean actual = receiveMessageService.deleteReceiveMessage(1);

        // then
        assertThat(insertResult).isTrue();
        assertThat(actual).isTrue();
    }

    @DisplayName("isAlive 를 호출 시 테이블이 정상적으로 생성 되어있다면 true 를 return 해야 한다")
    @Test
    void t4() {
        // given , when
        boolean result = receiveMessageService.isAlive();

        // then
        assertThat(result).isTrue();
    }
}