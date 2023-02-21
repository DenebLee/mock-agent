package nanoit.kr;

import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestScheduler {
    public static void main(String[] args) {
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(3);
        scheduledExecutorService.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);

    }


    static Runnable task = () -> {
        System.out.println("안녕" + new Date());
    };
}
