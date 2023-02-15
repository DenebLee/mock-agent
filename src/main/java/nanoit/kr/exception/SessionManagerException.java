package nanoit.kr.exception;

public class SessionManagerException extends RuntimeException {
    private final String reason;

    public SessionManagerException(String reason) {
        super(reason);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

}
