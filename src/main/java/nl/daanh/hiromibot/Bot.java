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
import java.time.Instant;

public class Bot {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);
    private static final CommandHandler commandHandler = new CommandHandler(RandomUtils.randomGenerator);
    private static final Listener listener = new Listener(commandHandler);

    private Bot() {
        try {
            setEmbedTemplate();
            setUnirestSettings();

            JDA discordBot = new JDABuilder()
                    .setToken(Constants.TOKEN)
                    .setActivity(Activity.listening("hi!help"))
                    .addEventListeners(listener)
                    .build().awaitReady();

            LOGGER.info("Bot has started on " + discordBot.getGuilds().size() + " guilds.");
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
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
