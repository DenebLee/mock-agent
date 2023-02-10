package nanoit.kr.domain.message;

import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)

public class Authentication {
    private long agentId;
    private String username;
    private String password;
    private String email;
}