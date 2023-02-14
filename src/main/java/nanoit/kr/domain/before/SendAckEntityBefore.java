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

public class SendAckEntityBefore {
    private long id;
    private MessageResult result;
    private Timestamp createdAt;
    private Timestamp lastModifiedAt;

    public SendAckBefore toDto() {
        return new SendAckBefore(result, createdAt, lastModifiedAt);
    }
}
