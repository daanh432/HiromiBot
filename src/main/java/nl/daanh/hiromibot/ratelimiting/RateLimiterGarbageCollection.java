package nl.daanh.hiromibot.ratelimiting;

import java.util.TimerTask;

public class RateLimiterGarbageCollection extends TimerTask {
    public void run() {
        RateLimiter.GarbageCollection();
    }
}