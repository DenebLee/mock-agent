package nanoit.kr.domain.internaldata;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nanoit.kr.domain.message.Payload;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class InternalDataFilter {
    // 기타 필요한 필드 값은 추후 수정 할 예정
    private Payload payload;
}
