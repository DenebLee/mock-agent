package nanoit.kr.module;

import nanoit.kr.InternalQueue;

public abstract class ModuleProcess implements Runnable {

    public static ModuleProcessManagerImpl moduleProcessManagerImpl;

    static {
        moduleProcessManagerImpl = new ModuleProcessManagerImpl();
    }

    protected final String uuid;
    protected final InternalQueue queue;
    protected boolean flag;

    abstract public void shoutDown();

    abstract public void sleep() throws InterruptedException;

    abstract public String getUuid();

    protected Status status;
    protected final long lastRunningTime;

    public ModuleProcess(InternalQueue queue, String uuid) {
        this.queue = queue;
        this.uuid = uuid;
        moduleProcessManagerImpl.register(this);
        this.status = Status.INIT;
        this.lastRunningTime = System.currentTimeMillis();
    }


    public enum Status {
        INIT, //
        RUN,  // flag, interrupt 멈추는 경우
        STOP  // STOP 으로 상태가 안바뀌는 경우
    }
}