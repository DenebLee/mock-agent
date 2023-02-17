package nanoit.kr.domain.message;

public enum MessageResult {
    SUCCESS("1"),
    FAILED("0");

    private final String property;

    MessageResult(String property){this.property = property;}

    public String getProperty(){return property;}


}
