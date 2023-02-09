package nanoit.kr.scheduler;

import nanoit.kr.InternalDataType;
import nanoit.kr.TemporaryQueue;
import nanoit.kr.domain.entity.SendEntity;
import nanoit.kr.domain.message.Send;
import nanoit.kr.extension.Jackson;
import nanoit.kr.util.TestSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.sql.Timestamp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;


@Testcontainers
class DataBaseSchedulerTest extends TestSetup {
    private static TemporaryQueue queue;
    private static DataBaseScheduler scheduler;

    public DataBaseSchedulerTest() throws IOException {
        super("SEND");
        queue = spy(new TemporaryQueue());
    }


    @BeforeEach
    void setUp() {
        scheduler = spy(new DataBaseScheduler(sendMessageService, queue));
    }

    @AfterEach
    void tearDown() {
        sendMessageService.deleteAllSendMessage();
    }

    @DisplayName("스케쥴러가 DB에 있는 데이터를 정상적으로 select 해와 queue 에 성공적으로 넣을 수 있어야 한다")
    @Test
    void t1() throws InterruptedException {
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
        Thread.sleep(3000);

        // then
        assertThat(queue.getQueueSize(InternalDataType.SENDER)).isEqualTo(count);
    }

    @DisplayName("스케쥴러가 select 한 데이터와 queue 에 담은 데이터가 일치 하여야 한다")
    @Test
    void t2() throws InterruptedException {
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
        assertThat(insertResult).isTrue();

        // when
        Thread.sleep(2000);

        // then
        Object object = queue.subscribe(InternalDataType.SENDER);
        assertThat(object).isInstanceOf(Send.class);
        Send send = Jackson.getInstance().getObjectMapper().convertValue(object, Send.class);
        assertThat(send.getContent()).isEqualTo(expected.getContent());
        assertThat(send.getName()).isEqualTo(expected.getName());
        assertThat(send.getPhoneNum()).isEqualTo(expected.getPhoneNum());
        assertThat(send.getCallback()).isEqualTo(expected.getCallback());
    }
}