package nl.daanh.hiromibot;

import kong.unirest.Unirest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.RandomUtils;
import nl.daanh.hiromibot.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.time.Instant;

public class Bot {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);

    private Bot() {
        try {
            // Load config file
            final Config config = new Config(new File("settings.json"));

            final CommandHandler commandHandler = new CommandHandler(RandomUtils.randomGenerator);
            final Listener listener = new Listener(commandHandler);

            // Set utils
            setEmbedTemplate();
            setUnirestSettings();

            // Start JDA
            JDA discordBot = new JDABuilder()
                    .setToken(config.getString("token"))
                    .setActivity(Activity.listening(config.getString("activity")))
                    .addEventListeners(listener)
                    .build().awaitReady();

            LOGGER.info("Bot has started on " + discordBot.getGuilds().size() + " guilds.");
        } catch (LoginException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new Bot();
    }

    private void setUnirestSettings() {
        Unirest.config().enableCookieManagement(false);
        WebUtils.setUserAgent("HiromiBot / 1.0.0 / Open Source Discord Bot");
    }

    private void setEmbedTemplate() {
        EmbedUtils.setEmbedBuilder(() -> new EmbedBuilder()
                .setColor(RandomUtils.getRandomColor())
                .setFooter("Hiromi Bot", null)
                .setTimestamp(Instant.now()));
    }
}
