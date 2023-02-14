package nanoit.kr.domain.before;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)

public class SendBefore {
    private String phoneNum;
    private String callback;
    private String name;
    private String content;
}
