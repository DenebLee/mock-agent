package nanoit.kr.domain.before;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import nanoit.kr.domain.message.MessageResult;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)

public class SendAckBefore {
    private MessageResult result;
    private Timestamp createdAt;
    private Timestamp lastModifiedAt;

    public SendAckEntityBefore toEntity() {
        return new SendAckEntityBefore(0, result, createdAt, lastModifiedAt);
    }
}