package nanoit.kr.module;

import nanoit.kr.queue.InternalQueueImpl;

public abstract class ModuleProcess implements Runnable {

    public static ModuleProcessManager moduleProcessManager;

    static {
        moduleProcessManager = new ModuleProcessManager();
    }

    protected final String uuid;
    protected final InternalQueueImpl queue;
    protected boolean flag;

    abstract public void shoutDown();

    abstract public void sleep() throws InterruptedException;

    abstract public String getUuid();

    protected Status status;
    protected final long lastRunningTime;

    public ModuleProcess(InternalQueueImpl queue, String uuid) {
        this.queue = queue;
        this.uuid = uuid;
        moduleProcessManager.register(this);
        this.status = Status.INIT;
        this.lastRunningTime = System.currentTimeMillis();
    }


    public enum Status {
        INIT, //
        RUN,  // flag, interrupt 멈추는 경우
        STOP  // STOP 으로 상태가 안바뀌는 경우
    }
}