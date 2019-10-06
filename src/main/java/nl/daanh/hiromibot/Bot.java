package nl.daanh.hiromibot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import nl.daanh.hiromibot.utils.EmbedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.time.Instant;
import java.util.Random;

public class Bot {
    private final Random randomGenerator = new Random();

    private Bot() {
        CommandHandler commandHandler = new CommandHandler(randomGenerator);
        Listener listener = new Listener(commandHandler);

        Logger LOGGER = LoggerFactory.getLogger(Bot.class);

        setEmbedTemplate();

        try {
            LOGGER.info("Bot is starting...");

            JDA bot = new JDABuilder()
                    .setToken(Constants.TOKEN)
                    .addEventListeners(listener)
                    .build().awaitReady();

            LOGGER.info("Bot has started on " + bot.getGuilds().size() + " guilds.");
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Bot();
    }

    private void setEmbedTemplate() {
        EmbedUtils.setEmbedBuilder(() -> new EmbedBuilder()
                .setColor(getRandomColor())
                .setFooter("Hiromi Bot", null)
                .setTimestamp(Instant.now()));
    }

    private Color getRandomColor() {
        float r = randomGenerator.nextFloat();
        float g = randomGenerator.nextFloat();
        float b = randomGenerator.nextFloat();

        return new Color(r, g, b);
    }
}
