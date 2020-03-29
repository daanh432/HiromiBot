package nl.daanh.hiromibot;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
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

    public static boolean AllowedToRun(TextChannel channel, Member member) {
        return RateLimiter.AllowedToRun(channel, member.getUser());
    }

    public static boolean AllowedToRun(MessageChannel channel, User user) {
        long currentTime = RateLimiter.CurrentTime();
        RateLimitObject time = buffer.getOrDefault(user.getIdLong(), new RateLimitObject(0L, 0));

        if (currentTime > time.getTime() + RateLimiter.timeBetweenMessages) {
            buffer.remove(user.getIdLong());
            RateLimit(channel, user);
            return true;
        } else {
            RateLimit(channel, user);
            return false;
        }
    }

    private static void RateLimit(MessageChannel channel, User user) {
        if (buffer.containsKey(user.getIdLong())) {
            RateLimitObject rateLimitObject = buffer.get(user.getIdLong());
            rateLimitObject.times++;
            if (rateLimitObject.times == 2) {
                channel.sendMessage("OwO calm down! I'm cooling down from your last request!").queue();
            }
        } else {
            RateLimitObject rateLimitObject = new RateLimitObject(RateLimiter.CurrentTime(), 1);
            buffer.put(user.getIdLong(), rateLimitObject);
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