package nanoit.kr.scheduler;

import nanoit.kr.domain.internaldata.InternalDataType;
import nanoit.kr.TemporaryQueue;
import nanoit.kr.domain.before.SendEntityBefore;
import nanoit.kr.domain.before.SendBefore;
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
class DataBaseSchedulerBeforeTest extends TestSetup {
    private static TemporaryQueue queue;
    private static DataBaseSchedulerBefore scheduler;

    public DataBaseSchedulerBeforeTest() throws IOException {
        super("SEND");
        queue = spy(new TemporaryQueue());
    }


    @BeforeEach
    void setUp() {
        scheduler = spy(new DataBaseSchedulerBefore(sendMessageService, queue));
    }

    @AfterEach
    void tearDown() {
        sendMessageService.deleteAllSendMessage();
    }

    @DisplayName("스케쥴러가 DB에 있는 데이터를 정상적으로 select 해와 queue 에 성공적으로 넣을 수 있어야 한다")
    @Test
    void t1() throws InterruptedException {
        // given
        SendEntityBefore expected = new SendEntityBefore();
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
        SendEntityBefore expected = new SendEntityBefore();
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
        assertThat(object).isInstanceOf(SendBefore.class);
        SendBefore sendBefore = Jackson.getInstance().getObjectMapper().convertValue(object, SendBefore.class);
        assertThat(sendBefore.getContent()).isEqualTo(expected.getContent());
        assertThat(sendBefore.getName()).isEqualTo(expected.getName());
        assertThat(sendBefore.getPhoneNum()).isEqualTo(expected.getPhoneNum());
        assertThat(sendBefore.getCallback()).isEqualTo(expected.getCallback());
    }
}