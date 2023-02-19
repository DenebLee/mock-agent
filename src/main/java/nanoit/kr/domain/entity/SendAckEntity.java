package nanoit.kr.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import nanoit.kr.domain.message.MessageResult;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)

public class SendAckEntity {
    private long messageId;
    private String result;
}
