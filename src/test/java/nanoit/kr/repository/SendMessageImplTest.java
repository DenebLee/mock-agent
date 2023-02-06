package nanoit.kr.repository;


import nanoit.kr.util.TestSetup;
import nanoit.kr.domain.entity.SendEntity;
import nanoit.kr.domain.message.MessageStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
class SendMessageImplTest extends TestSetup {
    public SendMessageImplTest() throws IOException {
        super("SEND");
    }

    @AfterEach
    void tearDown() {
        sendMessageRepository.deleteAll();
    }

    @DisplayName("send 테이블에 존재하는 칼럼의 갯수를 정상적으로 가져올 수 있어야 한다")
    @Test
    void t1() {
        // given
        SendEntity expected = new SendEntity();
        int count = 10;
        for (int i = 0; i < count; i++) {
            expected
                    .setId(0)
                    .setPhoneNum("010-4444-5555")
                    .setCallback("053-444-5555")
                    .setName("이정섭")
                    .setContent("안녕하세요" + i)
                    .setCreatedAt(new Timestamp(System.currentTimeMillis()))
                    .setLastModifiedAt(new Timestamp(System.currentTimeMillis()));
            boolean insertResult = sendMessageRepository.insert(expected);
            assertThat(insertResult).isTrue();
        }

        // when
        long actual = sendMessageRepository.count();

        // then
        assertThat(actual).isEqualTo(count);
    }

    @DisplayName("send 테이블에 deleteAll 을 했을 경우 모든 칼럼이 정상적으로 삭제 되어야 한다")
    @Test
    void t2() {
        // given
        SendEntity expected = new SendEntity();
        int count = 10;
        for (int i = 0; i < count; i++) {
            expected
                    .setId(0)
                    .setPhoneNum("010-4444-5555")
                    .setCallback("053-444-5555")
                    .setName("이정섭")
                    .setContent("안녕하세요" + i)
                    .setCreatedAt(new Timestamp(System.currentTimeMillis()))
                    .setLastModifiedAt(new Timestamp(System.currentTimeMillis()));
            boolean insertResult = sendMessageRepository.insert(expected);
            assertThat(insertResult).isTrue();
        }
        long insertCount = sendMessageRepository.count();

        // when
        boolean actual = sendMessageRepository.deleteAll();

        // then
        assertThat(insertCount).isEqualTo(count);
        assertThat(sendMessageRepository.count()).isEqualTo(0);
        assertThat(actual).isTrue();
    }


    @DisplayName("send 테이블에 updateMessageStatus 를 했을 경우 요청한 id 에 해당하는 칼럼이 정상적으로 MessageStatus를 업데이트 하여야 한다")
    @Test
    void t3() {
        // given
        SendEntity expected = new SendEntity();
        expected
                .setId(0)
                .setPhoneNum("010-4444-5555")
                .setCallback("053-444-5555")
                .setName("이정섭")
                .setContent("안녕하세요")
                .setCreatedAt(new Timestamp(System.currentTimeMillis()))
                .setLastModifiedAt(new Timestamp(System.currentTimeMillis()));
        boolean insertResult = sendMessageRepository.insert(expected);
        SendEntity inputUpdateValue = new SendEntity();
        inputUpdateValue
                .setId(1)
                .setStatus(MessageStatus.SENT);

        // when
        boolean updateResult = sendMessageRepository.updateMessageStatus(inputUpdateValue);

        // then
        assertThat(insertResult).isTrue();
        assertThat(updateResult).isTrue();
        SendEntity updateResultEntity = sendMessageRepository.selectById(1);
        assertThat(updateResultEntity.getStatus()).isEqualTo(MessageStatus.SENT);
    }


    @DisplayName("send 테이블에 selectById 를 했을 경우 요청한 id 에 해당하는 칼럼이 정상적으로 select 되어야 한다")
    @Test
    void t4() {
        // given
        SendEntity expected = new SendEntity();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        expected
                .setId(0)
                .setPhoneNum("010-4444-5555")
                .setCallback("053-444-5555")
                .setName("이정섭")
                .setContent("안녕하세요")
                .setCreatedAt(currentTime)
                .setLastModifiedAt(currentTime);
        boolean insertResult = sendMessageRepository.insert(expected);

        // when
        SendEntity actual = sendMessageRepository.selectById(1);

        // then
        assertThat(actual.getId()).isEqualTo(1);
        assertThat(actual.getPhoneNum()).isEqualTo(expected.getPhoneNum());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getCallback()).isEqualTo(expected.getCallback());
        assertThat(actual.getCreatedAt()).isEqualTo(expected.getCreatedAt());
        assertThat(actual.getLastModifiedAt()).isEqualTo(expected.getLastModifiedAt());
        assertThat(actual.getContent()).isEqualTo(expected.getContent());

    }

    @DisplayName("send 테이블에 selectAll 을 했을 때 모든 칼럼이 정상적으로 select 되어야 한다")
    @Test
    void t5() {
        // given
        SendEntity expected = new SendEntity();
        int count = 10;
        for (int i = 0; i < count; i++) {
            expected
                    .setId(0)
                    .setPhoneNum("010-4444-5555")
                    .setCallback("053-444-5555")
                    .setName("이정섭")
                    .setContent("안녕하세요" + i)
                    .setCreatedAt(new Timestamp(System.currentTimeMillis()))
                    .setLastModifiedAt(new Timestamp(System.currentTimeMillis()));
            assertThat(sendMessageRepository.insert(expected)).isTrue();
        }

        // when
        List<SendEntity> actual = sendMessageRepository.selectAll();

        // then
        assertThat(actual.size()).isEqualTo(count);
        assertThat(actual.get(3).getContent()).isEqualTo("안녕하세요3");
        assertThat(actual.get(actual.size() - 1).getId()).isEqualTo(count);
    }


    @DisplayName("send 테이블에 isAlive 를 했을 때 테이블이 정상적으로 존재하면 True 를 return 하여야 한다")
    @Test
    void t6() {
        // given , when
        boolean actual = sendMessageRepository.isAlive();

        // then
        assertThat(actual).isTrue();
    }

    @DisplayName("send 테이블에 insert 를 했을 때 정상적으로 데이터가 insert 되어야 한다")
    @Test
    void t7() {
        // given
        SendEntity expected = new SendEntity();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        expected
                .setId(0)
                .setPhoneNum("010-4444-5555")
                .setCallback("053-444-5555")
                .setName("이정섭")
                .setContent("안녕하세요")
                .setCreatedAt(currentTime)
                .setLastModifiedAt(currentTime);


        // when
        boolean insertResult = sendMessageRepository.insert(expected);

        // then
        assertThat(insertResult).isTrue();
        SendEntity actual = sendMessageRepository.selectById(1);
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getCallback()).isEqualTo(expected.getCallback());
        assertThat(actual.getPhoneNum()).isEqualTo(expected.getPhoneNum());
        assertThat(actual.getContent()).isEqualTo(expected.getContent());
        assertThat(actual.getCreatedAt()).isEqualTo(expected.getCreatedAt());
        assertThat(actual.getLastModifiedAt()).isEqualTo(expected.getLastModifiedAt());


    }

    @DisplayName("send 테이블에 deleteById 를 했을 때 정상적으로 요청한 id를 가진 칼럼이 delete 되어야 한다")
    @Test
    void t8() {
        // given
        SendEntity expected = new SendEntity();
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        expected
                .setId(0)
                .setPhoneNum("010-4444-5555")
                .setCallback("053-444-5555")
                .setName("이정섭")
                .setContent("안녕하세요")
                .setCreatedAt(currentTime)
                .setLastModifiedAt(currentTime);
        boolean insertResult = sendMessageRepository.insert(expected);

        // when
        boolean actual = sendMessageRepository.deleteById(1);

        // then
        assertThat(insertResult).isTrue();
        assertThat(actual).isTrue();
        assertThat(sendMessageRepository.selectById(1)).isNull();
    }

}