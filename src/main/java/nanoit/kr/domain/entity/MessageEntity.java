package nanoit.kr.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import nanoit.kr.domain.message.MessageDto;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)

public class MessageEntity {
    private long id;
    private int agentId;

    private String selected;
    private String receiveResult;
    private String sendResult;


    private String phoneNumber;
    private String callbackNumber;
    private String senderName;
    private String content;


    private Timestamp sendTime;
    private Timestamp receiveTime;
    private Timestamp createdAt;
    private Timestamp lastModifiedAt;

    private MessageDto toDto() {
        return new MessageDto(id, agentId,selected, receiveResult, sendResult, phoneNumber, callbackNumber, senderName, content, sendTime, receiveTime, createdAt, lastModifiedAt);
    }

}
