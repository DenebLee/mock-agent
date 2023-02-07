//package nanoit.kr.thread;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public abstract class ModuleProcess implements Runnable {
//
//    public static ModuleProcessManagerImpl moduleProcessManagerImpl;
//
//    static {
//        moduleProcessManagerImpl = new ModuleProcessManagerImpl();
//    }
//
//    protected final String uuid;
//    protected boolean flag;
//
//    abstract public void shoutDown();
//
//    abstract public void sleep() throws InterruptedException;
//
//    abstract public String getUuid();
//
//    protected Status status;
//    protected final long lastRunningTime;
//
//    public ModuleProcess(String uuid) {
//        this.uuid = uuid;
//        if (!moduleProcessManagerImpl.register(this)) {
//            log.error("[MODULE-PROCESS] Runnable instance REGISTER FAILED");
//        }
//        this.status = Status.INIT;
//        this.lastRunningTime = System.currentTimeMillis();
//    }
//
//
//    public enum Status {
//        INIT, //
//        RUN,  // flag, interrupt 멈추는 경우
//        STOP  // STOP 으로 상태가 안바뀌는 경우
//    }
//}
