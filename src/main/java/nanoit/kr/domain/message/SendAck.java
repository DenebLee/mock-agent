package nanoit.kr.domain.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import nanoit.kr.domain.entity.SendAckEntity;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)

public class SendAck {
    private MessageResult result;

    public SendAckEntity toEntity() {
        return new SendAckEntity(0, result, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
    }
}