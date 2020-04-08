/*
 * HiromiBot, a multipurpose open source Discord bot
 * Copyright (c) 2019 - 2020 daanh432
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package nl.daanh.hiromibot;

import lavalink.client.io.jda.JdaLavalink;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import nl.daanh.hiromibot.listeners.CommandListener;
import nl.daanh.hiromibot.listeners.MusicListener;
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
    public static final int SHARD_COUNT = 1;
    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);
    private static Bot instance;
    private ShardManager shardManager;

    private Bot() {
        try {
            // Load config file
            final Config config = Config.getInstance();

            // Set utils
            this.setEmbedTemplate();
            this.setUnirestSettings(config);

            final DefaultShardManagerBuilder shardManagerBuilder = DefaultShardManagerBuilder.create(
                    GatewayIntent.GUILD_BANS,
                    GatewayIntent.GUILD_VOICE_STATES,
                    GatewayIntent.GUILD_MESSAGES,
                    GatewayIntent.GUILD_MESSAGE_REACTIONS
            );
            shardManagerBuilder.setChunkingFilter(ChunkingFilter.NONE);


            // Create lavalink instances
            final LavalinkUtils lavalinkUtils = new LavalinkUtils(config.getString("token"));
            final JdaLavalink lavalink = LavalinkUtils.getLavalink();
            shardManagerBuilder.addEventListeners(lavalink);
            shardManagerBuilder.setVoiceDispatchInterceptor(lavalink.getVoiceInterceptor());

            // Build JDA
            shardManagerBuilder.setToken(config.getString("token"))
                    // Disable parts of the cache
                    .disableCache(EnumSet.of(CacheFlag.ACTIVITY, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS))
                    .setMemberCachePolicy(MemberCachePolicy.DEFAULT)
                    // Enable the bulk delete event
                    .setBulkDeleteSplittingEnabled(false)
                    // Set activity of Discord user
                    .setActivity(Activity.listening(config.getString("activity")))
                    // Add listeners
                    .addEventListeners(new CommandListener())
                    .addEventListeners(new MusicListener())
                    // Set shard count
                    .setShardsTotal(SHARD_COUNT);

            shardManager = shardManagerBuilder.build();

            LOGGER.info("Bot has started with " + shardManager.getShards().size() + " shards on " + shardManager.getGuilds().size() + " guilds.");
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public static Bot getInstance() {
        return instance;
    }

    public static void main(String[] args) throws IOException {
        new Config(new File("settings.json"));
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
