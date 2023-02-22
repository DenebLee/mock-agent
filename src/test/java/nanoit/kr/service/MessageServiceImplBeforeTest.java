//package nanoit.kr.service;
//
//import nanoit.kr.domain.entity.MessageEntity;
//import nanoit.kr.domain.entity.SendAckEntity;
//import nanoit.kr.domain.message.MessageResult;
//import nanoit.kr.domain.message.Send;
//import nanoit.kr.exception.SelectFailedException;
//import nanoit.kr.exception.UpdateFailedException;
//import nanoit.kr.repository.before.MessageRepositoryBefore;
//import nanoit.kr.setup.TestSetup;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
//import static org.mockito.Mockito.*;
//
//@Testcontainers
//class MessageServiceImplBeforeTest extends TestSetup {
//
//    public MessageServiceImplBeforeTest() throws IOException {
//        messageRepositoryBefore.createTable();
//    }
//
//    @AfterEach
//    void tearDown() {
//        messageRepositoryBefore.commonDeleteTable();
//    }
//
//    @DisplayName("t1 - 생성된 테이블에 ping 을 날렸을 때 존재하는 경우 정상적으로 true 값이 return 되어야 한다")
//    @Test
//    void t1() {
//        // given , when , then
//        assertThat(messageService.isAlive()).isTrue();
//    }
//
//    @DisplayName("t2 - 테이블에 레코드들이 존재하는 경우 count 메소드를 실행 하였을 때 존재하는 레코들의 갯수가 return 되어야 한다")
//    @Test
//    void t2() {
//        // given
//        int count = 4;
//        insertMessages(count);
//
//        // when
//        long actualCount = messageService.count();
//
//        // then
//        assertThat(actualCount).isEqualTo(count);
//    }
//
//    @DisplayName("t3 - 테이블에 레코드가 없을 경우 count 를 실행 하였을 때 failed 가 되어야 하며 0이 return 되어야 한다")
//    @Test
//    void t3() {
//        // given
//        MessageRepositoryBefore messageRepositoryBefore = mock(MessageRepositoryBefore.class);
//        when(messageRepositoryBefore.commonCount()).thenThrow(new SelectFailedException("failed"));
//        MessageService messageService = new MessageServiceImpl(messageRepositoryBefore);
//
//        // when
//        long result = messageService.count();
//
//        // then
//        verify(messageRepositoryBefore).commonCount();
//        assertThat(result).isEqualTo(0L);
//    }
//
//
//    @DisplayName("t4 - 테이블에 있는 레코드들을 모두 가져오는 selectAll 메소드를 실행 하면 가져온 레코드들을 send 로 변환하고 selected 를 업데이트 하여야 한다")
//    @Test
//    void t4() throws InterruptedException {
//        // given
//        int count = 10;
//        List<MessageEntity> list = insertMessages(count);
//
//        // when
//        List<Send> expected = messageService.selectAll();
//
//        // then
//        int messageCount = 1;
//        for (Send send : expected) {
//            assertThat(send.getMessageId()).isEqualTo(messageCount);
//            assertThat(send.getPhoneNumber()).isEqualTo("010-4444-5555");
//            assertThat(send.getCallbackNumber()).isEqualTo("053-111-2222");
//            assertThat(send.getSenderName()).isEqualTo("이정섭" + messageCount);
//            messageCount++;
//        }
//
//        List<MessageEntity> actualList = messageRepositoryBefore.sendSelectAll();
//        for (MessageEntity data : actualList) {
//            assertThat(data.getSelected()).isEqualTo(MessageResult.SUCCESS.getProperty());
//            assertThat(data.getSendTime()).isNotNull();
//            assertThat(data.getCreatedAt()).isNotNull();
//            assertThat(data.getCreatedAt()).isNotNull();
//        }
//    }
//
//    @DisplayName("t5 - 테이블에 레코드가 없을 경우 selectAll 메소드를 실행 하였을 때 정상적으로 실패 처리가 되어야 하며 list 가 null 이여야 한다")
//    @Test
//    void t5() {
//        MessageRepositoryBefore messageRepositoryBefore = mock(MessageRepositoryBefore.class);
//        MessageService messageService = mock(MessageServiceImpl.class);
//
//        assertThatThrownBy(() -> {
//            messageService.selectAll();
//            when(messageRepositoryBefore.sendSelectAll()).thenReturn(Collections.emptyList());
//        }).isInstanceOf(SelectFailedException.class)
//                .hasMessageContaining("[MSG-SERVICE] No messages found");
//    }
//
//    @DisplayName("t6 - 테이블에 레코드가 존재하지만 selectAll 메소드를 실행 하였을 때 selected 상태 업데이트에 실패하였을 때 정상적으로 에러 처리가 되어야 한다")
//    @Test
//    void t6() {
//        List<MessageEntity> messageEntities = mock(ArrayList.class);
//        assertThatThrownBy(() -> {
//            when(messageRepositoryBefore.sendSelectAll()).thenReturn(messageEntities);
//            when(messageRepositoryBefore.selectedUpdate(messageEntities)).thenReturn(false);
//        }).isInstanceOf(UpdateFailedException.class)
//                .hasMessageContaining("[MSG-SERVICE] Error in updating messages");
//    }
//
//    @DisplayName("t7 - Queue 에 정상적으로 publish 하게 되면 id 들이 담긴 list 를 넘겨줬을 때 정상적으로 해당 id 에 해당하는 레코드들의 sendResult 가 수정되어야 한다")
//    @Test
//    void t7() throws InterruptedException {
//        // given
//        int count = 1;
//        List<Long> ids = new ArrayList<>();
//        List<Send> sendList = messageService.selectAll();
//
//        for (Send send : sendList) {
//            ids.add(send.getMessageId());
//        }
//        // when
//        boolean result = messageService.updateSendResults(ids);
//
//        // then
//        assertThat(result).isTrue();
//        for (int i = 1; i < count + 1; i++) {
//            MessageEntity data = messageRepositoryBefore.selectById(i);
//            assertThat(data.getSelected()).isEqualTo(MessageResult.SUCCESS.getProperty());
//        }
//    }
//
//    @DisplayName("t8 - Queue 에 publish 하기 전 ids 가 빈 리스트일 경우 updateSendResult 가 실패해야 한다")
//    @Test
//    void t8() {
//        // given
//        List<Long> emptyIds = new ArrayList<>();
//
//        // when
//        boolean result = messageService.updateSendResults(emptyIds);
//
//        // then
//        assertThat(result).isFalse();
//        for (int i = 1; i < 11; i++) {
//            MessageEntity data = messageRepositoryBefore.selectById(i);
//            assertThat(data.getSelected()).isNull();
//        }
//    }
//
//    @DisplayName("t9 - updateReceiveResults 메소드를 실행 하였을 때 정상적으로 전달한 id 값에 해당하는 레코드들이 업데이트 되어야 한다")
//    @Test
//    void t9() throws InterruptedException {
//        // given
//        int count = 5;
//        List<MessageEntity> list = insertMessages(count);
//        List<Send> expected = messageService.selectAll();
//        List<Long> ids = new ArrayList<>();
//        int messageCount = 1;
//        for (Send send : expected) {
//            assertThat(send.getMessageId()).isEqualTo(messageCount);
//            assertThat(send.getPhoneNumber()).isEqualTo("010-4444-5555");
//            assertThat(send.getCallbackNumber()).isEqualTo("053-111-2222");
//            System.out.println(send.getSenderName());
//            assertThat(send.getSenderName()).isEqualTo("이정섭" + messageCount);
//            ids.add(send.getMessageId());
//            messageCount++;
//
//        }
//        boolean updateSendResult = messageService.updateSendResults(ids);
//
//        // when ,then
//        for (int i = 1; i < ids.size() + 1; i++) {
//            SendAckEntity send = new SendAckEntity();
//            send
//                    .setMessageId(i)
//                    .setResult(MessageResult.SUCCESS.getProperty());
//            boolean updateReceiveResult = messageService.updateReceiveResult(send);
//            assertThat(updateReceiveResult).isTrue();
//        }
//
//    }
//
//    @DisplayName("t10 - updateSendResult 메소드를 실행 하였을 때 정상적으로 전달한 id 값에 해당하는 레코드들이 업데이트 되어야 한다")
//    @Test
//    void t10() throws InterruptedException {
//        // given
//        MessageEntity expected = new MessageEntity();
//        expected
//                .setPhoneNumber("010-4444-5555")
//                .setCallbackNumber("053-111-2222")
//                .setSenderName("이정섭")
//                .setContent("안녕하세요");
//        boolean insertResult = messageRepositoryBefore.insert(expected);
//        List<Send> list = messageService.selectAll();
//
//        // when
//        boolean actual = messageService.updateSendResult(list.get(0).getMessageId());
//
//        // then
//        assertThat(actual).isTrue();
//    }
//
//
//    private List<MessageEntity> insertMessages(int count) {
//        List<MessageEntity> list = new ArrayList<>();
//        for (int i = 1; i < count + 1; i++) {
//            MessageEntity message = new MessageEntity();
//            message
//                    .setPhoneNumber("010-4444-5555")
//                    .setCallbackNumber("053-111-2222")
//                    .setSenderName("이정섭" + i)
//                    .setContent("안녕하세요" + i);
//            list.add(message);
//        }
//        messageRepositoryBefore.insertAll(list);
//        return list;
//    }
//}