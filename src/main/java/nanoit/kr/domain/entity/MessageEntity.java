package nanoit.kr.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nanoit.kr.domain.message.MessageDto;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity {
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

    private MessageDto toDto() {
        return new MessageDto(id, selected, phoneNumber, sendResult, callbackNumber, senderName, content, sendTime, receiveResult, receiveTime, createdAt, lastModifiedAt);
    }

}
