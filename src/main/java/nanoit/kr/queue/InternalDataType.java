package nanoit.kr.queue;

import nanoit.kr.domain.entity.SendAckEntity;
import nanoit.kr.domain.message.Send;
import nanoit.kr.domain.message.SendAck;

public enum InternalDataType {
    RECEIVE_MAPPER(String.class),
    SEND_MAPPER(Send.class),
    SENDER(String.class),
    FILTER(SendAck.class),
    INSERT(SendAckEntity.class);

    private final Class<?> dataType;

    InternalDataType(Class<?> dataType) {
        this.dataType = dataType;
    }

    public Class<?> getDataType() {
        return dataType;
    }
}