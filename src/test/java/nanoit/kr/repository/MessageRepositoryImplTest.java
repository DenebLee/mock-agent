package nanoit.kr.repository;


import nanoit.kr.domain.entity.MessageEntity;
import nanoit.kr.domain.entity.SendAckEntity;
import nanoit.kr.domain.message.MessageResult;
import nanoit.kr.domain.message.SendAck;
import nanoit.kr.setup.TestSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Testcontainers
class MessageRepositoryImplTest extends TestSetup {

    public MessageRepositoryImplTest() throws IOException {
        super();
        messageRepository.createTable();
    }

    @AfterEach
    void tearDown() {
        messageRepository.commonDeleteTable();
    }

    @DisplayName("정상적으로 Agent테이블이 생성되어야 한다 ")
    @Test
    void t1() {
        // given & when & then
        assertThat(messageRepository.commonPing()).isTrue();
    }

    @DisplayName("테이블에 있는 레코드 갯수 만큼 정상적으로 Count 되어야 한다  ")
    @Test
    void t2() {
        // given
        int count = 1000;
        insertMessages(count);

        // when
        long actual = messageRepository.commonCount();

        // then
        assertThat(actual).isEqualTo(count);

    }

    @DisplayName("생성된 테이블에 핑을 날렸을때 정상적으로 응답 하여야 한다")
    @Test
    void t3() {
        // given & when & then
        assertThat(messageRepository.commonPing()).isTrue();

    }

    @DisplayName("commonDeleteTable 메소드를 실행 하였을 때 정상적으로 테이블에 있는 레코드들이 삭제되어야 한다")
    @Test
    void t4() {
        // given
        int count = 1000;
        insertMessages(count);
        long beforeCountInTable = messageRepository.commonCount();

        // when
        boolean actual = messageRepository.commonDeleteTable();
        long afterCountInTable = messageRepository.commonCount();

        // then
        assertThat(beforeCountInTable).isEqualTo(count);
        assertThat(actual).isTrue();
        assertThat(afterCountInTable).isEqualTo(0);
    }

    @DisplayName("commonDelteById 메소드를 실행 하였을 때 정상적으로 id에 해당하는 레코드가 삭제 되어야한다")
    @Test
    void t5() {
        int count = 1000;
        insertMessages(count);
        long beforeCountInTable = messageRepository.commonCount();

        // when
        long id = 1;
        boolean actual = messageRepository.commonDeleteById(id);
        long afterCountInTable = messageRepository.commonCount();
        MessageEntity isExist = messageRepository.selectById(id);

        // then
        assertThat(beforeCountInTable).isEqualTo(count);
        assertThat(afterCountInTable).isEqualTo(count - 1);
        assertThat(isExist).isNull();
        assertThat(actual).isTrue();
    }

    @DisplayName("selectById 메소드를 실행 하였을 떄 정상적으로 id에 해당하는 레코드가 select 되어야 한다")
    @Test
    void t6() {
        // given
        int count = 100;
        insertMessages(count);
        long beforeCountInTable = messageRepository.commonCount();


        // when
        MessageEntity actual = messageRepository.selectById(88);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getSenderName()).isEqualTo("이정섭87");
        assertThat(actual.getContent()).isEqualTo("안녕하세요87");
    }

    @DisplayName("insert 메소드를 실행 하였을 떄 정상적으로 테이블에 insert 되어야 한다")
    @Test
    void t7() {
        // given
        MessageEntity expected = new MessageEntity();
        expected
                .setId(0)
                .setAgentId(1)
                .setPhoneNumber("010-4444-5555")
                .setCallbackNumber("053-111-2222")
                .setSenderName("이정섭")
                .setContent("안녕하세요");

        // when
        boolean insertResult = messageRepository.insert(expected);
        MessageEntity actual = messageRepository.selectById(1);
        // then
        assertThat(insertResult).isTrue();
        assertThat(actual).isNotNull();
        assertThat(actual.getAgentId()).isEqualTo(expected.getAgentId());
        assertThat(actual.getCallbackNumber()).isEqualTo(expected.getCallbackNumber());
        assertThat(actual.getPhoneNumber()).isEqualTo(expected.getPhoneNumber());
        assertThat(actual.getContent()).isEqualTo(expected.getContent());
        assertThat(actual.getSelected()).isEqualTo("0");
    }

    @DisplayName("insertAll 메소드를 실행 하였을때 정상적으로 요청한 다중 레코드 데이터들이 insert 되어야 한다")
    @Test
    void t8() {
        // given , when
        int count = 100;
        insertMessages(count);

        // then
        assertThat(messageRepository.commonCount()).isEqualTo(100);
    }

    @DisplayName("receiveCount 메소드를 실행 하였을때 정상적으로 receive에 필요한 레코드들의 갯수를 가져와야 한다")
    @Test
    void t9() {
        // given
        int count = 10;
        insertMessages(count);

        for (int i = 0; i < count; i++) {
            SendAckEntity sendAck = new SendAckEntity(i, MessageResult.SUCCESS.getProperty());
            boolean updateResult = messageRepository.receiveUpdate(sendAck);
            assertThat(updateResult).isTrue();
        }

        // when
        assertThat(messageRepository.receiveCount()).isEqualTo(10);
        List<MessageEntity> resultReceive = messageRepository.receiveSelectAll();
        assertThat(resultReceive.size()).isEqualTo(10);

        for (MessageEntity data : resultReceive) {
            assertThat(data.getReceiveResult()).isEqualTo(MessageResult.SUCCESS.getProperty());
            assertThat(data.getId()).isNotNull();
        }

    }

    @DisplayName("")
    @Test
    void t10() {

    }

    @DisplayName("")
    @Test
    void t11() {

    }

    @DisplayName("")
    @Test
    void t12() {

    }

    private void insertMessages(int count) {
        List<MessageEntity> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            MessageEntity message = new MessageEntity();
            message
                    .setAgentId(1)
                    .setPhoneNumber("010-4444-5555")
                    .setCallbackNumber("053-111-2222")
                    .setSenderName("이정섭" + i)
                    .setContent("안녕하세요" + i);
            list.add(message);
        }
        messageRepository.insertAll(list);
    }

}