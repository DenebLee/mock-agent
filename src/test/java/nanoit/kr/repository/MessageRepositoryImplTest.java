package nanoit.kr.repository;


import nanoit.kr.domain.entity.MessageEntity;
import nanoit.kr.domain.entity.SendAckEntity;
import nanoit.kr.domain.message.MessageResult;
import nanoit.kr.setup.TestSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.swing.*;
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

    @DisplayName("t1 - 정상적으로 Agent 테이블이 생성되어야 한다 ")
    @Test
    void t1() {
        // given & when & then
        assertThat(messageRepository.commonPing()).isTrue();
    }

    @DisplayName("t2 - 테이블에 있는 레코드 갯수 만큼 정상적으로 Count 되어야 한다  ")
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

    @DisplayName("t3 - 생성된 테이블에 핑을 날렸을때 정상적으로 응답 하여야 한다")
    @Test
    void t3() {
        // given & when & then
        assertThat(messageRepository.commonPing()).isTrue();

    }

    @DisplayName("t4 - commonDeleteTable 메소드를 실행 하였을 때 정상적으로 테이블에 있는 레코드들이 삭제되어야 한다")
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

    @DisplayName("t5 - commonDelteById 메소드를 실행 하였을 때 정상적으로 id에 해당하는 레코드가 삭제 되어야한다")
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

    @DisplayName("t6 - selectById 메소드를 실행 하였을 떄 정상적으로 id에 해당하는 레코드가 select 되어야 한다")
    @Test
    void t6() {
        // given
        int count = 100;
        insertMessages(count);
        long beforeCountInTable = messageRepository.commonCount();


        // when
        MessageEntity actual = messageRepository.selectById(count - 12);

        // then
        assertThat(actual).isNotNull();
        assertThat(actual.getSenderName()).isEqualTo("이정섭" + (count - 12));
        assertThat(actual.getContent()).isEqualTo("안녕하세요" + (count - 12));
    }

    @DisplayName("t7 - insert 메소드를 실행 하였을 떄 정상적으로 테이블에 insert 되어야 한다")
    @Test
    void t7() {
        // given
        MessageEntity expected = new MessageEntity();
        expected
                .setId(0)
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
        assertThat(actual.getCallbackNumber()).isEqualTo(expected.getCallbackNumber());
        assertThat(actual.getPhoneNumber()).isEqualTo(expected.getPhoneNumber());
        assertThat(actual.getContent()).isEqualTo(expected.getContent());
        assertThat(actual.getSelected()).isEqualTo("0");
    }

    @DisplayName("t8 - insertAll 메소드를 실행 하였을때 정상적으로 요청한 다중 레코드 데이터들이 insert 되어야 한다")
    @Test
    void t8() {
        // given , when
        int count = 100;
        insertMessages(count);

        // then
        assertThat(messageRepository.commonCount()).isEqualTo(100);
    }

    @DisplayName("t9 - receiveCount 메소드를 실행 하였을때 정상적으로 receive에 필요한 레코드들의 갯수를 가져와야 한다")
    @Test
    void t9() {
        // given
        int count = 1;
        List<MessageEntity> list = insertMessages(count);
        boolean selectedUpdate = messageRepository.selectedUpdate(list);

        SendAckEntity sendAck = new SendAckEntity(1, MessageResult.SUCCESS.getProperty());
        messageRepository.sendResultUpdate(1L);
        boolean updateResult = messageRepository.receiveUpdate(sendAck);

        // when
        assertThat(selectedUpdate).isTrue();
        assertThat(updateResult).isTrue();
        assertThat(messageRepository.receiveCount()).isEqualTo(1);
    }

    @DisplayName("t10 - receiveSelectById 메소드를 실행 하였을 때 정상적으로 요청한 Id 값을 가진 레코드를 반환 하여야 한다")
    @Test
    void t10() {
        // given
        int count = 1;
        List<MessageEntity> list = insertMessages(count);
        long recordCount = messageRepository.commonCount();
        boolean selectedUpdate = messageRepository.selectedUpdate(list);
        messageRepository.sendResultUpdate(1);
        System.out.println(messageRepository.selectById(1));
        SendAckEntity sendAck = new SendAckEntity(1, MessageResult.SUCCESS.getProperty());
        boolean updateResult = messageRepository.receiveUpdate(sendAck);


        // when
        MessageEntity actual = messageRepository.receiveSelectById(sendAck.getMessageId());

        // then
        assertThat(recordCount).isEqualTo(1);
        assertThat(actual.getReceiveTime()).isNotNull();
        assertThat(actual.getReceiveResult()).isEqualTo(MessageResult.SUCCESS.getProperty());
    }

    @DisplayName("t11 - receiveSelectAll 메소드를 실행 하였을 때 정상적으로 List 가 가져와야 한다")
    @Test
    void t11() {
        int count = 3;
        List<MessageEntity> list = insertMessages(count);
        long recordCount = messageRepository.commonCount();
        boolean selectedUpdate = messageRepository.selectedUpdate(list);
        for (int i = 1; i < count + 1; i++) {
            messageRepository.sendResultUpdate(i);
            SendAckEntity sendAck = new SendAckEntity(i, MessageResult.SUCCESS.getProperty());
            boolean updateResult = messageRepository.receiveUpdate(sendAck);
        }
        // when
        List<MessageEntity> actual = messageRepository.receiveSelectAll();
        System.out.println(actual);

        // then
        int a = 0;
        for (MessageEntity data : actual) {
            a++;
            assertThat(data.getId()).isEqualTo(a);
            assertThat(data.getReceiveResult()).isEqualTo(MessageResult.SUCCESS.getProperty());
            assertThat(data.getReceiveTime()).isNotNull();
        }
    }

    @DisplayName("t12 - receiveUpdate 메소드를 실행 하였을 때 정상적으로 update 가 진행 되어야 한다")
    @Test
    void t12() {
        // given
        int count = 1;
        List<MessageEntity> list = insertMessages(count);
        boolean selectedUpdate = messageRepository.selectedUpdate(list);
        messageRepository.sendResultUpdate(1);
        SendAckEntity expected = new SendAckEntity(1, MessageResult.SUCCESS.getProperty());

        // when
        boolean updateResult = messageRepository.receiveUpdate(expected);

        // then
        assertThat(selectedUpdate).isTrue();
        assertThat(updateResult).isTrue();
    }

    @DisplayName("t13 - sendSelectAll 메소드를 실행 하였을 때 정상적으로 send 관련 레코드들이 가져와야 한다")
    @Test
    void t13() {
        // given
        int count = 5;
        insertMessages(count);

        // when
        List<MessageEntity> actual = messageRepository.sendSelectAll();

        // then
        assertThat(actual.size()).isEqualTo(count);
        int messageCount = 1;
        for (MessageEntity data : actual) {
            assertThat(data.getId()).isEqualTo(messageCount);
            assertThat(data.getPhoneNumber()).isEqualTo("010-4444-5555");
            assertThat(data.getCallbackNumber()).isEqualTo("053-111-2222");
            assertThat(data.getSenderName()).isEqualTo("이정섭" + messageCount);
            assertThat(data.getContent()).isEqualTo("안녕하세요" + messageCount);
            messageCount++;
        }
    }


    @DisplayName("t14 - selectedUpdate 메소드를 실행하면 selected 상태가 0인 레코드들을 전체 selected = 1로 정상적으로 update 하여야 한다")
    @Test
    void t14() {
        // given
        int count = 10;
        List<MessageEntity> list = insertMessages(count);

        // when
        boolean selectedUpdate = messageRepository.selectedUpdate(list);

        // then
        assertThat(selectedUpdate).isTrue();
    }

    @DisplayName("t15 - sendResultUpdate 메소드를 실행 했을때 List 에 담긴 int 값들을 가진 레코드들이 sendResult 값이 업데이트 되어야 한다")
    @Test
    void t15() {
        // given
        int count = 3;
        List<MessageEntity> list = insertMessages(count);
        boolean selectedUpdate = messageRepository.selectedUpdate(list);
        for (int i = 1; i < count + 1; i++) {
            assertThat(messageRepository.sendResultUpdate(i)).isTrue();
        }
    }

    private List<MessageEntity> insertMessages(int count) {
        List<MessageEntity> list = new ArrayList<>();
        for (int i = 1; i < count + 1; i++) {
            MessageEntity message = new MessageEntity();
            message
                    .setId(i)
                    .setPhoneNumber("010-4444-5555")
                    .setCallbackNumber("053-111-2222")
                    .setSenderName("이정섭" + i)
                    .setContent("안녕하세요" + i);
            list.add(message);
        }
        messageRepository.insertAll(list);
        return list;
    }

}