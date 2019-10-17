package nl.daanh.hiromibot.utils;

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
}