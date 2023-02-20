package nanoit.kr.domain.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)

public class Send {
    private long messageId;
    private String phoneNumber;
    private String callbackNumber;
    private String senderName;
    private String content;
}
