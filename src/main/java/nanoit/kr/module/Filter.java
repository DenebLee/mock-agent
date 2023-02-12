package nanoit.kr.module;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.domain.internaldata.InternalDataType;
import nanoit.kr.TemporaryQueue;
import nanoit.kr.domain.internaldata.InternalDataFilter;
import nanoit.kr.domain.internaldata.InternalDataInsert;
import nanoit.kr.domain.message.ErrorPayload;
import nanoit.kr.domain.message.Payload;
import nanoit.kr.domain.message.Report;
import nanoit.kr.domain.message.SendAck;
import nanoit.kr.extension.Jackson;
import nanoit.kr.thread.ThreadResource;

@Slf4j
public class Filter extends ModuleProcess {

    private final TemporaryQueue queue;
    private final ThreadResource threadResource;

    public Filter(String uuid, TemporaryQueue queue, ThreadResource threadResource) {
        super(queue, uuid);
        this.threadResource = threadResource;
        this.queue = queue;
    }


    @Override
    public void run() {
        try {
            while (true) {
                Object object = queue.subscribe(InternalDataType.Filter);

                if (object instanceof InternalDataFilter) {
                    InternalDataFilter internalDataFilter = (InternalDataFilter) object;
                    Payload payload = internalDataFilter.getPayload();

                    if (payload.getData() instanceof ErrorPayload && payload.getData() != null) {
                        log.error("[FILTER] RECEIVED ERROR MESSAGE error content : [{}]", ((ErrorPayload) payload.getData()).getReason());
                        throw new Exception();
                    }

                    switch (payload.getType()) {
                        case SEND_ACK:
                            SendAck sendAck = Jackson.getInstance().getObjectMapper().convertValue(payload.getData(), SendAck.class);
                            if (queue.publish(new InternalDataInsert(sendAck))) {
                                log.debug("[FILTER] SEND_ACK SEND TO INSERT SUCCESS data :[{}]", sendAck);
                            } else {
                                log.warn("[MAPPER] SEND_ACK SEND TO INSERT FAILED");
                            }
                            break;

                        case AUTHENTICATION_ACK:
                            threadResource.setAuthenticationStatus();
                            log.info("[FILTER] AUTHENTICATION SUCCESS !! ");
                            break;

                        case REPORT:
                            // 현재는 그냥 log 로 출력하지만 추후 테이블 만들어서 삽입
                            Report report = Jackson.getInstance().getObjectMapper().convertValue(payload.getData(), Report.class);
                            if (queue.publish(new InternalDataInsert(report))) {
                                log.debug("[FILTER] REPORT SEND TO INSERT SUCCESS data :[{}]", report);
                            } else {
                                log.warn("[MAPPER] REPORT SEND TO INSERT FAILED");
                            }
                            break;

                        case ALIVE_ACK:
                            // G/W 단 구현안되서 추후 구현 예정
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shoutDown() {

    }

    @Override
    public void sleep() throws InterruptedException {

    }

    @Override
    public String getUuid() {
        return this.uuid;
    }
}