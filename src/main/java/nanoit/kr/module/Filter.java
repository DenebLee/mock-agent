package nanoit.kr.module;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.domain.message.*;
import nanoit.kr.manager.SessionManager;
import nanoit.kr.queue.InternalDataType;
import nanoit.kr.queue.InternalQueueImpl;
import nanoit.kr.extension.Jackson;

@Slf4j
public class Filter extends ModuleProcess {

    private final InternalQueueImpl queue;
    private final SessionManager manager;

    public Filter(String uuid, InternalQueueImpl queue, SessionManager manager) {
        super(queue, uuid);
        this.queue = queue;
        this.manager = manager;
    }

    @Override
    public void run() {
        try {
            while (this.flag) {
                Object object = queue.receiveSubscribe(InternalDataType.FILTER);
                if (object != null && object instanceof Payload) {
                    Payload payload = (Payload) object;
                    if (validatePayload(payload)) {
                        switch (payload.getType()) {
                            case SEND_ACK:
                                // Ack 일 경우 성공 실패 여부에 따라 sendAck값 다르게
                                if (payload.getData() instanceof ErrorPayload) {
                                    ErrorPayload errorPayload = (ErrorPayload) payload.getData();
                                    log.warn("[FILTER] Error Message Catch reason : [{}]", errorPayload.getReason());
                                    queue.receivePublish(InternalDataType.INSERT, new SendAck(errorPayload.getMessageNum(), MessageResult.FAILED));
                                }
                                SendAck sendAck = Jackson.getInstance().getObjectMapper().convertValue(payload.getData(), SendAck.class);
                                if (sendAck.getResult().equals(MessageResult.SUCCESS)) {
                                    if (queue.receivePublish(InternalDataType.INSERT, new InsertMessage(payload.getMessageUuid(), sendAck))) {
                                        log.debug("[FILTER] Message Send To Insert Success message :[{}]", sendAck);
                                    }
                                }
                                break;
                            case AUTHENTICATION_ACK:
                                if (payload.getData() instanceof ErrorPayload) {
                                    ErrorPayload errorPayload = (ErrorPayload) payload.getData();
                                    log.warn("[FILTER] Error Authentication Message Catch reason : [{}]", errorPayload.getReason());
                                    manager.cleanUpResourcesWhenAuthenticationFailure(payload.getMessageUuid());
                                }
                                AuthenticationAck authenticationAck = Jackson.getInstance().getObjectMapper().convertValue(payload.getData(), AuthenticationAck.class);
                                if (authenticationAck.getResult().equals(MessageResult.SUCCESS)) {
                                    manager.getResource(payload.getMessageUuid()).setAuthenticationStatus();
                                }
                                break;
                            case REPORT:
                                log.info("[FILTER] Report Receive Success report-data : [{}]", payload.getData());
                                break;
                            case ALIVE_ACK:
                                // some code
                                break;
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("[INSERT] Error occurred while taking message from queue: {}", e.getMessage());
            shoutDown();
        } catch (NullPointerException e) {
            log.debug("[INSERT] Null Pointer Exception occurred while processing message: {}", e.getMessage());
        } catch (Exception e) {
            log.error("[INSERT] Error occurred while processing message: {}", e.getMessage());
        }
    }

    private boolean validateSendAck(SendAck ack) {
        return ack.getResult() != null && ack.getMessageId() == 0;
    }

    private boolean validatePayload(Payload payload) {
        if (payload.getType() == null) {
            return false;
        }
        if (payload.getMessageUuid() == null || payload.getMessageUuid().trim().equals("")) {
            return false;
        }
        return payload.getData() != null;
    }

    @Override
    public void shoutDown() {
      Thread.currentThread().interrupt();
      this.flag = false;
    }

    @Override
    public void sleep() throws InterruptedException {
        log.debug("[INSERT] Putting module to sleep...");
        Thread.sleep(1000);
    }

    @Override
    public String getUuid() {
        return this.uuid;
    }
}
