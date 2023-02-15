package nanoit.kr.scheduler;

import lombok.extern.slf4j.Slf4j;
import nanoit.kr.InternalQueue;
import nanoit.kr.domain.PropertyDto;
import nanoit.kr.domain.message.Send;
import nanoit.kr.service.MessageService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DataBaseScheduler {
    private final ScheduledExecutorService scheduledExecutorService;
    private final MessageService messageService;
    private final InternalQueue queue;
    private List<Send> selectList;
    private final PropertyDto dto;

    public DataBaseScheduler(MessageService messageService, InternalQueue queue, PropertyDto dto) throws IOException {
        this.messageService = messageService;
        this.queue = queue;
        this.selectList = new ArrayList<>();
        this.dto = dto;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }


    public Runnable task = new Runnable() {
        @Override
        public void run() {
            try {
                selectList = messageService.selectAll();
                for (Send send : selectList) {
                    if (queue.publish(send)) {
                        log.debug("[SCHEDULER - @AGENTID{}] SELECT DATA FROM TABLE SUCCESS !!! number Imported : [{}]", dto.getUserAgent(), selectList.size());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                shutdownScheduler();
                log.error("[SCHEDULER] SHUTDOWN -> {}", e.getMessage());
            }
        }
    };

    public void start() {
        scheduledExecutorService.scheduleWithFixedDelay(task, 1, 3, TimeUnit.MICROSECONDS);
    }

    public boolean isSchedulerShutdown() {
        return scheduledExecutorService.isShutdown();
    }

    public void shutdownScheduler() {
        this.scheduledExecutorService.shutdownNow();
    }


}
