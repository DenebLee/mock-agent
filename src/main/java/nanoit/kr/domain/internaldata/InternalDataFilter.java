package nanoit.kr.domain.internaldata;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import nanoit.kr.domain.message.Payload;
import nanoit.kr.queue.InternalDataType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)

public class InternalDataFilter {
    private InternalDataType type;
    private Payload payload;
}
