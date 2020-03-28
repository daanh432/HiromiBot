package nl.daanh.hiromibot;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimerTask;

class RateLimiter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimiter.class);
    private static final Integer timeBetweenMessages = 2; // 2 seconds
    private static final HashMap<Long, RateLimitObject> buffer = new HashMap<>();

    private static long CurrentTime() {
        return Instant.now().getEpochSecond();
    }

    static void GarbageCollection() {
        Iterator<Long> iter = buffer.keySet().iterator();
        while (iter.hasNext()) {
            Long memberId = iter.next();
            RateLimitObject rateLimitObject = buffer.get(memberId);

            if (RateLimiter.CurrentTime() > rateLimitObject.getTime() + RateLimiter.timeBetweenMessages)
                iter.remove();
        }
    }

    static boolean AllowedToRun(TextChannel channel, Member member) {
        long currentTime = RateLimiter.CurrentTime();
        RateLimitObject time = buffer.getOrDefault(member.getIdLong(), new RateLimitObject(0L, 0));

        if (currentTime > time.getTime() + RateLimiter.timeBetweenMessages) {
            buffer.remove(member.getIdLong());
            RateLimit(channel, member);
            return true;
        } else {
            RateLimit(channel, member);
            return false;
        }
    }

    private static void RateLimit(TextChannel channel, Member member) {
        if (buffer.containsKey(member.getIdLong())) {
            RateLimitObject rateLimitObject = buffer.get(member.getIdLong());
            rateLimitObject.times++;
            if (rateLimitObject.times == 2) {
                channel.sendMessage("OwO calm down! I'm cooling down from your last request!").queue();
            }
        } else {
            RateLimitObject rateLimitObject = new RateLimitObject(RateLimiter.CurrentTime(), 1);
            buffer.put(member.getIdLong(), rateLimitObject);
        }
    }
}

class RateLimitObject {
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

class RateLimitGarbageCollection extends TimerTask {
    public void run() {
        RateLimiter.GarbageCollection();
    }
}