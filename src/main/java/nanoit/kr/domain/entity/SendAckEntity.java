package nanoit.kr.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nanoit.kr.domain.message.MessageResult;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class SendAckEntity {
    private long messageId;
    private String result;
}
