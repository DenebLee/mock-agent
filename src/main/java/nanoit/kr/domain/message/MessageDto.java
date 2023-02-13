package nanoit.kr.domain.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nanoit.kr.domain.entity.MessageEntity;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {

    private long id;
    private String selected;
    private String phoneNumber;
    private String sendResult;
    private String callbackNumber;
    private String senderName;
    private String content;
    private Timestamp sendTime;
    private String receiveResult;
    private Timestamp receiveTime;
    private Timestamp createdAt;
    private Timestamp lastModifiedAt;

    private MessageEntity toEntity() {
        return new MessageEntity(id, selected, phoneNumber, sendResult, callbackNumber, senderName, content, sendTime, receiveResult, receiveTime, createdAt, lastModifiedAt);
    }
}
