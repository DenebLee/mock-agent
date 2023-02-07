package nanoit.kr.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import nanoit.kr.domain.message.MessageStatus;
import nanoit.kr.domain.message.Send;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)

public class SendEntity {
    private long id;
    private MessageStatus status;
    private String phoneNum;
    private String callback;
    private String name;
    private String content;
    private Timestamp createdAt;
    private Timestamp lastModifiedAt;

    public Send toDto() {
        return new Send(phoneNum, callback, name, content);
    }
}
