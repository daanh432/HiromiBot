package nl.daanh.hiromibot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
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
            final Listener listener = new Listener();

            // Set utils
            setEmbedTemplate();
            setUnirestSettings(config);

            // Build JDA
            DefaultShardManagerBuilder shardManagerBuilder = new DefaultShardManagerBuilder();
            shardManagerBuilder.setToken(config.getString("token"));
            shardManagerBuilder.setActivity(Activity.listening(config.getString("activity")));
            shardManagerBuilder.addEventListeners(listener);

            ShardManager shardManager = shardManagerBuilder.build();

            LOGGER.info("Bot has started with " + shardManager.getShards().size() + " shards on " + shardManager.getGuilds().size() + " guilds.");
        } catch (LoginException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Bot();
    }

    private void setUnirestSettings(Config config) {
        WebUtils.setUserAgent("HiromiBot / 1.0.0 / Open Source Discord Bot");
        WebUtils.setHiromiApiToken(config.getString("apitoken"));
    }

    private void setEmbedTemplate() {
        EmbedUtils.setEmbedBuilder(() -> new EmbedBuilder()
                .setColor(RandomUtils.getRandomColor())
                .setFooter("Hiromi Bot", null)
                .setTimestamp(Instant.now()));
    }
}
