package board.myboard.global.log;

public class TraceStatus {

    private TraceId tracdId;
    private Long stratTimeMs;
    private String message;

    public TraceStatus(TraceId tracdId, Long stratTimeMs, String message) {
        this.tracdId = tracdId;
        this.stratTimeMs = stratTimeMs;
        this.message = message;
    }

    public TraceId getTraceId() {
        return tracdId;
    }

    public Long getStartTimeMs() {
        return stratTimeMs;
    }

    public String getMessage() {
        return message;
    }
}
