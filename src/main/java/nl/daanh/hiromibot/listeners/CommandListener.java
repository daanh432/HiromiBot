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

package nl.daanh.hiromibot.listeners;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;
import nl.daanh.hiromibot.CommandManager;
import nl.daanh.hiromibot.Config;
import nl.daanh.hiromibot.database.DatabaseManager;
import nl.daanh.hiromibot.ratelimiting.RateLimiter;
import nl.daanh.hiromibot.ratelimiting.RateLimiterGarbageCollection;
import nl.daanh.hiromibot.utils.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Timer;

public class CommandListener extends ListenerAdapter {
    private static Timer garbageCollectionTimer = new Timer();
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandListener.class);
    private final CommandManager commandManager;
    private final EventWaiter eventWaiter;

    public CommandListener() {
        garbageCollectionTimer.schedule(new RateLimiterGarbageCollection(), 5000, 300000);
        this.eventWaiter = new EventWaiter();
        this.commandManager = new CommandManager(this.eventWaiter);
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        LOGGER.info("{} is ready", event.getJDA().getSelfUser().getAsTag());
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        this.eventWaiter.onEvent(event);
    }

    @Override
    public void onMessageReactionRemove(@Nonnull MessageReactionRemoveEvent event) {
        this.eventWaiter.onEvent(event);
    }

    @Override
    public void onMessageReactionRemoveAll(@Nonnull MessageReactionRemoveAllEvent event) {
        this.eventWaiter.onEvent(event);
    }

    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getAuthor().isFake()) return;

        if (RateLimiter.AllowedToRun(event.getChannel(), event.getAuthor())) {
            event.getChannel().sendMessage("I don't operate in private channels.").queue();
        }
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        String rawMessage = event.getMessage().getContentRaw().toLowerCase();

        if (event.getAuthor().getIdLong() == Config.getInstance().getLong("owner")
                && event.getJDA().getShardManager() != null
                && rawMessage.equalsIgnoreCase(Config.getInstance().getString("prefix") + "shutdown")) {
            shutdown(event.getJDA().getShardManager());
            return;
        }

        // Check sender
        if (event.getAuthor().isBot() || event.getMessage().isWebhookMessage() || event.getMember() == null)
            return;

        // Check if bot has required permissions to be able to execute in this channel
        if (!event.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_WRITE))
            return;

        // Check prefix
        String prefix = rawMessage.startsWith(Config.getInstance().getString("prefix")) ? Config.getInstance().getString("prefix") : DatabaseManager.instance.getPrefix(event.getGuild().getIdLong());
        if (!rawMessage.startsWith(prefix))
            return;

        if (RateLimiter.AllowedToRun(event.getChannel(), event.getMember())) {
            commandManager.handle(event, prefix);
        }
    }

    /**
     * Shuts down the bot gracefully
     *
     * @param shardManager the shard manager you know
     */
    private void shutdown(ShardManager shardManager) {
        LOGGER.warn("Shutting down on request.");
        for (JDA jda : shardManager.getShards()) {
            jda.shutdown();
        }
        shardManager.shutdown();
        WebUtils.client.dispatcher().executorService().shutdown();
        WebUtils.client.connectionPool().evictAll();
        System.exit(0);
    }
}
