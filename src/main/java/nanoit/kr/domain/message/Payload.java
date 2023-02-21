package nanoit.kr.domain.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)

public class Payload {
    private PayloadType type;
    private String messageUuid;
    private Object data;
}