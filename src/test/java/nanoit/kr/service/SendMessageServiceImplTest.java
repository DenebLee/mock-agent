package nanoit.kr.service;

import nanoit.kr.domain.entity.SendEntity;
import nanoit.kr.domain.message.MessageStatus;
import nanoit.kr.domain.message.Send;
import nanoit.kr.util.TestSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
class SendMessageServiceImplTest extends TestSetup {

    public SendMessageServiceImplTest() throws IOException {
        super("SEND");
    }

    @AfterEach
    void tearDown() {
        sendMessageRepository.deleteAll();
    }

    @DisplayName("selectSendMessages 를 호출 하였을 때 테이블에 있는 모든 칼럼들이 정상적으로 select 되어야 한다")
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
        List<SendEntity> resultList = sendMessageService.selectSendMessages();

        // then
        assertThat(resultList.size()).isEqualTo(count);
        assertThat(resultList.get(4).getContent()).isEqualTo("안녕하세요4");
        assertThat(resultList.get(resultList.size() - 1).getId()).isEqualTo(count);
    }

    @DisplayName("updateSendMessageStatus 를 호출 하였을 때 id 값에 해당하는 status 가 정상적으로 업데이트 되어야 한다")
    @Test
    void t2() {
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

        //when
        boolean result = sendMessageService.updateSendMessageStatus(1, MessageStatus.SENT);

        // then
        assertThat(insertResult).isTrue();
        SendEntity actual = sendMessageRepository.selectById(1);
        assertThat(result).isTrue();
        assertThat(actual.getStatus()).isEqualTo(expected.getStatus());
    }

    @DisplayName("deleteAllSendMessage 를 호출 하였을 때 receive 테이블에 있는 모든 데이터가 정상적으로 삭제 되어야 한다")
    @Test
    void t3() {
        // given
        SendEntity expected = new SendEntity();
        for (int i = 0; i < 10; i++) {
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
        boolean result = sendMessageService.deleteAllSendMessage();

        // then
        long count = sendMessageService.count();
        assertThat(count).isEqualTo(0);
        assertThat(result).isTrue();

    }

    @DisplayName("count 를 호출 하였을 때 receive 테이블에 있는 칼럼 갯수가 정상적으로 return 되어야 한다")
    @Test
    void t4() {
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
        long countResult = sendMessageService.count();

        // then
        assertThat(countResult).isEqualTo(count);
        List<SendEntity> list = sendMessageService.selectSendMessages();
        assertThat(list.size()).isEqualTo(countResult);

    }

}