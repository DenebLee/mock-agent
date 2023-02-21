package nanoit.kr.module;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import nanoit.kr.domain.message.PayloadType;
import nanoit.kr.domain.message.Send;
import nanoit.kr.queue.InternalDataType;
import nanoit.kr.queue.InternalQueueImpl;
import nanoit.kr.domain.message.Payload;
import nanoit.kr.extension.Jackson;

import java.util.UUID;

@Slf4j
public class SendMapper extends ModuleProcess {

    private final InternalQueueImpl queue;

    public SendMapper(String uuid, InternalQueueImpl queue) {
        super(queue, uuid);
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object obj = queue.subscribe(InternalDataType.SEND_MAPPER);
                if (obj instanceof Send) {
                    Send send = (Send) obj;
                    if (isDataValid(send)) {
                        sendQueue(makeSendData(send));
                    } else {
                        log.warn("[SEND-MAPPER] Missing or invalid information in the message to be sent: {}", send);
                    }
                } else {
                    log.error("[SEND-MAPPER] Critical Error");
                }
            }
        } catch (InterruptedException e) {
            log.error("[SEND-MAPPER] Interrupted while waiting for messages: {}", e.getMessage());
            Thread.currentThread().interrupt();
        } catch (JsonProcessingException e) {
            log.error("[SEND-MAPPER] Failed to process JSON: {}", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Payload형태로 String 으로 캐스팅 되어야 함
    private String makeSendData(Send send) throws JsonProcessingException {
        Payload payload = new Payload();
        payload
                .setType(PayloadType.SEND)
                .setMessageUuid(UUID.randomUUID().toString())
                .setData(send);

        return Jackson.getInstance().getObjectMapper().writeValueAsString(payload);
    }

    private boolean isDataValid(Send send) {
        if (send.getMessageId() == 0) {
            return false;
        }
        if (send.getSenderName() == null || send.getSenderName().contains(" ")) {
            return false;
        }
        if (send.getPhoneNumber() == null || send.getPhoneNumber().contains(" ")) {
            return false;
        }
        if (send.getCallbackNumber() == null || send.getCallbackNumber().contains(" ")) {
            return false;
        }
        return send.getContent() != null;
    }

    private boolean sendQueue(String data) {
        if (queue.publish(InternalDataType.SENDER, data)) {
            log.debug("[SEND-MAPPER] Success to send Data to Queue");
            return true;
        } else {
            log.error("[SEND-MAPPER] Failed to send Data to Queue");
            return false;
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
