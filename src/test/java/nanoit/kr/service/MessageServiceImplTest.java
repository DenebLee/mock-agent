package nanoit.kr.service;
import nanoit.kr.domain.entity.MessageEntity;
import nanoit.kr.domain.entity.SendAckEntity;
import nanoit.kr.domain.message.MessageResult;
import nanoit.kr.domain.message.Send;
import nanoit.kr.exception.SelectFailedException;
import nanoit.kr.setup.TestSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Testcontainers
class MessageServiceImplTest extends TestSetup {

    public MessageServiceImplTest() throws IOException {
        messageRepository.createTable();
    }

    @AfterEach
    void tearDown() {
        messageRepository.commonDeleteTable();
    }

    @DisplayName("생성된 테이블에 ping 을 날렸을 때 존재하는 경우 정상적으로 true 값이 return 되어야 한다")
    @Test
    void t1() {
        // given , when , then
        assertThat(messageService.isAlive()).isTrue();
    }

    @DisplayName("테이블에 ping 을 날렸을 때 테이블이 정상적으로 생성이 안되어 있으면 false 값을 반환하여야 한다")
    @Test
    void t2(){
        // given
        when(messageRepository.commonPing()).thenThrow(new SelectFailedException("failed"));

        // when
        boolean result = messageService.isAlive();

        // then
        verify(messageRepository).commonPing();
        verify(messageRepository).createTable();
    }

    @DisplayName("테이블에 레코드들이 존재하는 경우 count 메소드를 실행 하였을 때 존재하는 레코들의 갯수가 return 되어야 한다")
    @Test
    void t3(){
        // given
        int count = 4;
        insertMessages(count);

        // when
        long actualCount = messageService.count();

        // then
        assertThat(actualCount).isEqualTo(count);
    }

    @DisplayName("테이블에 레코드가 없을 경우 count 를 실행 하였을 때 failed 가 되어야 하며 0이 return 되어야 한다")
    @Test
    void t4(){
        // given
        when(messageRepository.commonCount()).thenThrow(new SelectFailedException("failed"));

        // when
        long result = messageService.count();

        // then
        assertEquals(0L, result);
        verify(messageRepository).commonCount();
    }

    @DisplayName("테이블에 있는 레코드들을 모두 가져오는 selectAll 메소드를 실행 하면 가져온 레코드들을 send 로 변환하고 selected 를 업데이트 하여야 한다")
    @Test
    void t5() throws InterruptedException {
        // given
        int count = 10;
        List<MessageEntity> list = insertMessages(count);

        // when
        List<Send> expected = messageService.selectAll();

        // then
        for (Send send : expected) {
            int messageCount = 1;
            assertThat(send.getMessageId()).isEqualTo(messageCount);
            assertThat(send.getPhoneNumber()).isEqualTo(list.get(messageCount).getPhoneNumber());
            assertThat(send.getCallbackNumber()).isEqualTo(list.get(messageCount).getCallbackNumber());
            assertThat(send.getSenderName()).isEqualTo(list.get(messageCount).getSenderName());
            assertThat(send.getPhoneNumber()).isEqualTo(list.get(messageCount).getContent());
            messageCount++;
        }

        List<MessageEntity> actualList = messageRepository.sendSelectAll();
        for (MessageEntity data : actualList) {
            assertThat(data.getSelected()).isEqualTo(MessageResult.SUCCESS.getProperty());
            assertThat(data.getSendTime()).isNotNull();
            assertThat(data.getCreatedAt()).isNotNull();
            assertThat(data.getCreatedAt()).isNotNull();
        }
    }

    @DisplayName("테이블에 레코드가 없을 경우 selectAll 메소드를 실행 하였을 때 정상적으로 실패 처리가 되어야 하며 list 가 null 이여야 한다")
    @Test
    void t6() throws InterruptedException {
        // given
        when(messageRepository.sendSelectAll()).thenReturn(null);

        // when
        List<Send> result = messageService.selectAll();

        // then
        assertThat(result).isNull();
        verify(messageRepository).sendSelectAll();
    }

    @DisplayName("테이블에 레코드가 존재하지만 selectAll 메소드를 실행 하였을 때 selected 상태 업데이트에 실패하였을 때 정상적으로 에러 처리가 되어야 한다")
    @Test
    void t7(){
        // given
        int count = 10;
        List<MessageEntity> list = insertMessages(count);

        when(messageRepository.sendSelectAll()).thenReturn(list);

        // when
        boolean result = messageRepository.selectedUpdate(list);

        // then
        verify(messageRepository).sendSelectAll();
        verify(messageRepository).selectedUpdate(list);

        assertThat(result).isFalse();
    }

    @DisplayName("Queue 에 정상적으로 publish 하게 되면 id 들이 담긴 list 를 넘겨줬을 때 정상적으로 해당 id 에 해당하는 레코드들의 sendResult 가 수정되어야 한다")
    @Test
    void t8() throws InterruptedException {
        // given
        int count = 10;
        insertMessages(count);
        List<Long> ids = new ArrayList<>();

        List<Send> list = messageService.selectAll();

        for (Send send: list) {
            ids.add(send.getMessageId());
        }

        // when
        boolean result = messageService.updateSendResults(ids);

        // then
        assertThat(result).isTrue();
        for (int i = 1; i < count+1; i++) {
            MessageEntity data = messageRepository.selectById(i);
            assertThat(data.getSelected()).isEqualTo(MessageResult.SUCCESS.getProperty());
        }
    }

    @DisplayName("Queue 에 publish 하기 전 ids 가 빈 리스트일 경우 updateSendResult 가 실패해야 한다")
    @Test
    void t9(){
        // given
        List<Long> emptyIds = new ArrayList<>();

        // when
        boolean result = messageService.updateSendResults(emptyIds);

        // then
        assertThat(result).isFalse();
        for (int i = 1; i < 11; i++) {
            MessageEntity data = messageRepository.selectById(i);
            assertThat(data.getSelected()).isNull();
        }
    }

    @DisplayName("updateReceiveResults 메소드를 실행 하였을 때 정상적으로 전달한 id 값에 해당하는 레코드들이 업데이트 되어야 한다")
    @Test
    void t10() throws InterruptedException {
        // given
        int count = 5;
        List<MessageEntity> list = insertMessages(count);
        List<Send> expected = messageService.selectAll();
        List<Long> ids = new ArrayList<>();

        for (Send send : expected) {
            int messageCount = 1;
            assertThat(send.getMessageId()).isEqualTo(messageCount);
            assertThat(send.getPhoneNumber()).isEqualTo(list.get(messageCount).getPhoneNumber());
            assertThat(send.getCallbackNumber()).isEqualTo(list.get(messageCount).getCallbackNumber());
            assertThat(send.getSenderName()).isEqualTo(list.get(messageCount).getSenderName());
            assertThat(send.getPhoneNumber()).isEqualTo(list.get(messageCount).getContent());
            messageCount++;
            ids.add(send.getMessageId());
        }
        boolean updateSendResult = messageService.updateSendResults(ids);

        // when ,then
        for (int i = 1; i < ids.size() + 1; i++) {
            SendAckEntity send = new SendAckEntity();
            send
                    .setMessageId(i)
                    .setResult(MessageResult.SUCCESS.getProperty());
            boolean updateReceiveResult = messageService.updateReceiveResult(send);
            assertThat(updateReceiveResult).isTrue();
        }

    }

    @DisplayName("updateSendResult 메소드를 실행 하였을 때 정상적으로 전달한 id 값에 해당하는 레코드들이 업데이트 되어야 한다")
    @Test
    void t11() throws InterruptedException {
        // given
        MessageEntity expected = new MessageEntity();
        expected
                .setAgentId(1)
                .setPhoneNumber("010-4444-5555")
                .setCallbackNumber("053-111-2222")
                .setSenderName("이정섭")
                .setContent("안녕하세요");
        boolean insertResult = messageRepository.insert(expected);
        List<Send> list = messageService.selectAll();

        // when
        boolean actual = messageService.updateSendResult(list.get(0).getMessageId());

        // then
        assertThat(actual).isTrue();
    }


    private List<MessageEntity> insertMessages(int count) {
        List<MessageEntity> list = new ArrayList<>();
        for (int i = 1; i < count + 1; i++) {
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
        return list;
    }
}