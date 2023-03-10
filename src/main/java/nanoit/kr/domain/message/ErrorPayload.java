package nanoit.kr.domain.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorPayload {
    private long messageNum;
    private String reason;
}
