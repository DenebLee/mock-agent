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
    private int agentId;


    private String selected;
    private String sendResult;
    private String receiveResult;


    private String phoneNumber;
    private String callbackNumber;
    private String senderName;
    private String content;


    private Timestamp sendTime;
    private Timestamp receiveTime;
    private Timestamp createdAt;
    private Timestamp lastModifiedAt;

    private MessageEntity toEntity() {
        return new MessageEntity(id, agentId, selected, sendResult, receiveResult, phoneNumber, callbackNumber, senderName, content, sendTime, receiveTime, createdAt, lastModifiedAt);
    }
}
