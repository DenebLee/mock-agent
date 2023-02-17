//package nanoit.kr.scheduler;
//
//import lombok.extern.slf4j.Slf4j;
//
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//@Slf4j
//public class DataBaseSchedulerForInsertData {
//    private final ScheduledExecutorService scheduledExecutorService;
//
//    public DataBaseSchedulerForInsertData() {
//        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
//        scheduledExecutorService.scheduleAtFixedRate(task, 5, 10, TimeUnit.SECONDS);
//    }
//
//    public Runnable task = new Runnable() {
//        @Override
//        public void run() {
//            try {
//                int limit = 1;
//                int count = 0;
//                do {
//                    count++;
//                    sendEntityBefore
//                            .setId(0)
//                            .setPhoneNum("010-4444-5555")
//                            .setCallback("053-444-5555")
//                            .setName("이정섭")
//                            .setContent("안녕하세요" + count)
//                            .setCreatedAt(new Timestamp(System.currentTimeMillis()))
//                            .setLastModifiedAt(new Timestamp(System.currentTimeMillis()));
//                    list.add(sendEntityBefore);
//                } while (count < limit);
//            } catch (Exception e) {
//                e.printStackTrace();
//                scheduledExecutorService.shutdown();
//                log.error("[SCHEDULER-INSERT-DATA] SHUTDOWN -> {}", e.getMessage());
//            }
//        }
//    };
//}
//
