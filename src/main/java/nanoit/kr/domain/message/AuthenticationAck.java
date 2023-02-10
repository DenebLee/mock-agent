package nanoit.kr.domain.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
    public class AuthenticationAck {
        private long agentId;
        private MessageResult result;
    }