package nanoit.kr.repository;


import nanoit.kr.domain.entity.MessageEntity;
import nanoit.kr.setup.TestSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.when;

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
        insertMessage(count);

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
        insertMessage(count);
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
        insertMessage(count);
        long beforeCountInTable = messageRepository.commonCount();

        // when
        long id = 199;
        boolean actual = messageRepository.commonDeleteById(id);
        long afterCountInTable = messageRepository.commonCount();
        when(messageRepository.selectById(id)).thenReturn(isNull(MessageEntity.class));

        // then
        assertThat(beforeCountInTable).isEqualTo(count);
        assertThat(actual).isTrue();
        assertThat(afterCountInTable).isEqualTo(count - 1);
    }

    @DisplayName("")
    @Test
    void t6() {

    }


    private void insertMessage(int count) {
        List<MessageEntity> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            MessageEntity message = new MessageEntity();
            message
                    .setId(0)
                    .setAgentId(1)
                    .setPhoneNumber("010-4444-5555")
                    .setCallbackNumber("053-111-2222")
                    .setSenderName("이정섭")
                    .setContent("안녕하세요");
            list.add(message);
        }
        messageRepository.insertAll(list);
    }

}