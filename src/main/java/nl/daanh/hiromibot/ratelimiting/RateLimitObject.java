package nl.daanh.hiromibot.ratelimiting;

public class RateLimitObject {
    Integer times;
    private Long time;

    RateLimitObject(Long time, Integer times) {
        this.time = time;
        this.times = times;
    }

    Long getTime() {
        return time;
    }
}