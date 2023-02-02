package nanoit.kr.exception;

public class DataNullException extends RuntimeException {
    private final String reason;

    public DataNullException(String reason) {
        super(reason);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

}
