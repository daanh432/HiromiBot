package nl.daanh.hiromibot.utils;

import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.util.Random;

public class RandomUtils {
    public static final Random randomGenerator = new Random();

    public static Color getRandomColor() {
        float r = randomGenerator.nextFloat();
        float g = randomGenerator.nextFloat();
        float b = randomGenerator.nextFloat();

        return new Color(r, g, b);
    }

    public static Boolean inSameVoiceChannel(Member member, Member self) {
        if (member != null && self != null && member.getVoiceState() != null && self.getVoiceState() != null) {
            return member.getVoiceState().getChannel() != self.getVoiceState().getChannel();
        }
        return false;
    }
}