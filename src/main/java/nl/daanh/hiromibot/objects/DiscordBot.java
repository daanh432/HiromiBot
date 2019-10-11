package nl.daanh.hiromibot.objects;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import nl.daanh.hiromibot.Bot;
import nl.daanh.hiromibot.CommandHandler;
import nl.daanh.hiromibot.Listener;
import nl.daanh.hiromibot.utils.EmbedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.time.Instant;
import java.util.Random;

public class DiscordBot {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);
    private static final Random randomGenerator = new Random();
    private static String TOKEN = null;
    private final CommandHandler commandHandler = new CommandHandler(randomGenerator, this);
    private final Listener listener = new Listener(commandHandler);

    public DiscordBot(String token) {
        TOKEN = token;
        setEmbedTemplate();
    }

    public void startBot() {
        if (TOKEN != null) {
            try {
                LOGGER.info("Bot is starting...");
                JDA bot = new JDABuilder()
                        .setToken(TOKEN)
                        .setActivity(Activity.listening("hi!help"))
                        .addEventListeners(listener)
                        .build().awaitReady();
                LOGGER.info("Bot has started on " + bot.getGuilds().size() + " guilds.");
            } catch (LoginException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void linkDatabase(String username, String password, String ipAddress, String database) {

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
