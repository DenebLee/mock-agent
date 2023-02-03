package nanoit.kr.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import nanoit.kr.domain.message.MessageResult;
import nanoit.kr.domain.message.SendAck;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)

public class SendAckEntity {
    private long id;
    private MessageResult result;
    private Timestamp createdAt;
    private Timestamp lastModifiedAt;

    public SendAck toDto() {
        return new SendAck(result);
    }
}
