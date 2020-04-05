package nl.daanh.hiromibot;

import lavalink.client.io.jda.JdaLavalink;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.LavalinkUtils;
import nl.daanh.hiromibot.utils.RandomUtils;
import nl.daanh.hiromibot.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.EnumSet;

public class Bot {
    public static final int SHARD_COUNT = 2;
    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);
    private static Bot instance;
    private ShardManager shardManager;

    private Bot() {
        try {
            // Load config file
            final DefaultShardManagerBuilder shardManagerBuilder = new DefaultShardManagerBuilder();
            final Config config = new Config(new File("settings.json"));
            final Listener listener = new Listener();

            // Set utils
            setEmbedTemplate();
            setUnirestSettings(config);

            LavalinkUtils lavalinkUtils = new LavalinkUtils(config.getString("token"));
            JdaLavalink lavalink = LavalinkUtils.getLavalink();

            // Build JDA
            shardManagerBuilder.setToken(config.getString("token"))
                    // Disable parts of the cache
                    .setDisabledCacheFlags(EnumSet.of(CacheFlag.ACTIVITY))
                    // Enable the bulk delete event
                    .setBulkDeleteSplittingEnabled(false)
                    // Set activity of Discord user
                    .setActivity(Activity.listening(config.getString("activity")))
                    // Add listeners
                    .addEventListeners(listener)
                    // Lavalink setup
                    .addEventListeners(lavalink)
                    .setVoiceDispatchInterceptor(lavalink.getVoiceInterceptor())
                    // Set shard count
                    .setShardsTotal(SHARD_COUNT);

            shardManager = shardManagerBuilder.build();

            LOGGER.info("Bot has started with " + shardManager.getShards().size() + " shards on " + shardManager.getGuilds().size() + " guilds.");
        } catch (LoginException | IOException e) {
            e.printStackTrace();
        }
    }

    public static Bot getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        instance = new Bot();
    }

    public ShardManager getShardManager() {
        return this.shardManager;
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
