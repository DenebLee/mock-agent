package nanoit.kr.domain.before;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import nanoit.kr.domain.message.MessageStatus;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)

public class SendEntityBefore {
    private long id;
    private MessageStatus status;
    private String phoneNum;
    private String callback;
    private String name;
    private String content;
    private Timestamp createdAt;
    private Timestamp lastModifiedAt;

    public SendBefore toDto() {
        return new SendBefore(phoneNum, callback, name, content);
    }
}
