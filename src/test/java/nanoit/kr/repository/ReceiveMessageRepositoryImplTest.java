package nanoit.kr.repository;

import nanoit.kr.domain.entity.SendAckEntity;
import nanoit.kr.domain.message.MessageResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
class ReceiveMessageRepositoryImplTest extends RepositoryTestSetUp {
    public ReceiveMessageRepositoryImplTest() throws IOException {
        super("RECEIVE");
    }

    @AfterEach
    void tearDown() {
        receiveMessageRepository.deleteAll();
    }

    @DisplayName("Receive 테이블에 SendAckEntity 를 insert 했을 때 정상적으로 insert 되어야 한다")
    @Test
    void t1() {
        // given
        SendAckEntity expected = new SendAckEntity();
        expected.setResult(MessageResult.SUCCESS)
                .setCreatedAt(new Timestamp(System.currentTimeMillis()))
                .setLastModifiedAt(new Timestamp(System.currentTimeMillis()));

        // when
        boolean actual = receiveMessageRepository.insert(expected);

        // then
        assertThat(actual).isTrue();
    }

    @DisplayName("Receive 테이블에 ping 을 보냈을때 응답 하여야 한다")
    @Test
    void t2() {
        // given , when
        boolean actual = receiveMessageRepository.isAlive();

        // then
        assertThat(actual).isTrue();

    }

    @DisplayName("Receive 테이블에 selectAll 을 하였을 때 테이블에 있는 칼럼들이 모두 정상적으로 select 되어야 한다")
    @Test
    void t3() {
        // given
        SendAckEntity expected = new SendAckEntity();
        boolean insertResult = false;
        int count = 10;
        for (int i = 0; i < count; i++) {
            expected.setResult(MessageResult.SUCCESS)
                    .setCreatedAt(new Timestamp(System.currentTimeMillis()))
                    .setLastModifiedAt(new Timestamp(System.currentTimeMillis()));
            insertResult = receiveMessageRepository.insert(expected);
        }

        // when
        List<SendAckEntity> actual = receiveMessageRepository.selectAll();

        // then
        assertThat(insertResult).isTrue();
        assertThat(actual.size()).isEqualTo(count);
        for (SendAckEntity send : actual) {
            assertThat(send.getResult()).isEqualTo(expected.getResult());
        }

    }

    @DisplayName("Receive 테이블에 deleteAll 을 하였을 때 테이블에 있는 모든 칼럼들이 정상적으로 삭제 되어야 한다")
    @Test
    void t4() {
        // given
        SendAckEntity sendAckEntity = new SendAckEntity();
        sendAckEntity.setResult(MessageResult.SUCCESS)
                .setCreatedAt(new Timestamp(System.currentTimeMillis()))
                .setLastModifiedAt(new Timestamp(System.currentTimeMillis()));
        boolean insertResult = receiveMessageRepository.insert(sendAckEntity);

        // when
        boolean actual = receiveMessageRepository.deleteAll();

        // then
        assertThat(insertResult).isTrue();
        assertThat(actual).isTrue();


    }

    @DisplayName("Receive 테이블에 deleteById 을 하였을 때 테이블에 요청한 id 값에 해당하는 칼럼이 정상적으로 삭제 되어야 한다")
    @Test
    void t5() {
        // given
        SendAckEntity sendAckEntity = new SendAckEntity();
        sendAckEntity
                .setResult(MessageResult.SUCCESS)
                .setCreatedAt(new Timestamp(System.currentTimeMillis()))
                .setLastModifiedAt(new Timestamp(System.currentTimeMillis()));
        boolean insertResult = receiveMessageRepository.insert(sendAckEntity);

        // when
        boolean actual = receiveMessageRepository.deleteById(1);

        // then
        assertThat(insertResult).isTrue();
        assertThat(actual).isTrue();
        assertThat(receiveMessageRepository.selectById(1)).isNull();
    }

    @DisplayName("Receive 테이블에 selectById 를 하였을 때 요청한 id 값에 해당하는 칼럼이 정상적으로 select 되어야 한다")
    @Test
    void t6() {
        // given
        SendAckEntity expected = new SendAckEntity();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        expected
                .setResult(MessageResult.SUCCESS)
                .setCreatedAt(currentTime)
                .setLastModifiedAt(currentTime);
        boolean insertResult = receiveMessageRepository.insert(expected);

        // when
        SendAckEntity actual = receiveMessageRepository.selectById(1);

        // then
        assertThat(insertResult).isTrue();
        assertThat(actual.getId()).isEqualTo(1);
        assertThat(actual.getResult()).isEqualTo(expected.getResult());
        assertThat(actual.getCreatedAt()).isEqualTo(expected.getCreatedAt());
        assertThat(actual.getLastModifiedAt()).isEqualTo(expected.getLastModifiedAt());

    }

}
