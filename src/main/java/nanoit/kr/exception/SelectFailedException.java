package nanoit.kr.exception;

public class SelectFailedException extends RuntimeException {
    private final String reason;

    public SelectFailedException(String reason) {
        super(reason);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
