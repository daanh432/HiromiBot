package nl.daanh.hiromibot;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.sharding.ShardManager;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.LavalinkUtils;
import nl.daanh.hiromibot.utils.SettingsUtil;
import nl.daanh.hiromibot.utils.WebUtils;
import nl.daanh.hiromibot.utils.music.PlayerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Timer;

class Listener extends ListenerAdapter {
    private static Timer garbageCollectionTimer = new Timer();
    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private final CommandManager commandManager;
    private final EventWaiter eventWaiter;

    Listener() {
        garbageCollectionTimer.schedule(new RateLimitGarbageCollection(), 5000, 300000);
        this.eventWaiter = new EventWaiter();
        this.commandManager = new CommandManager(this.eventWaiter);
    }

    /**
     * Checks if music playback should be paused
     *
     * @param channelLeft the channel that was left
     * @param guild       the guild where the event happened
     */
    private void pauseMusicHandler(@Nonnull VoiceChannel channelLeft, @Nonnull Guild guild) {
        if (channelLeft.getMembers().size() == 1) {
            PlayerManager playerManager = PlayerManager.getInstance();
            VoiceChannel musicChannel = LavalinkUtils.getConnectedChannel(guild);
            if (musicChannel != null && musicChannel.equals(channelLeft)) {
                this.pauseMusic(playerManager, guild);
            }
        }
    }

    /**
     * Pauses music playback
     *
     * @param playerManager a playerManager instance
     * @param guild         the guild where the event happened
     */
    private void pauseMusic(@Nonnull PlayerManager playerManager, @Nonnull Guild guild) {
        TextChannel announceChannel = playerManager.getLastChannel(guild);
        if (announceChannel != null && announceChannel.canTalk() && !playerManager.isPaused(guild)) {
            EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed(String.format("Pausing music playback because everyone left the channel.\nResume by typing: ``%sresume``", Config.getInstance().getString("prefix")), true);
            announceChannel.sendMessage(embedBuilder.build()).queue();
        }
        playerManager.setPaused(guild, true);
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        LOGGER.info("{} is ready", event.getJDA().getSelfUser().getAsTag());
    }

    @Override
    public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
        this.pauseMusicHandler(event.getChannelLeft(), event.getGuild());
    }

    @Override
    public void onGuildVoiceMove(@Nonnull GuildVoiceMoveEvent event) {
        this.pauseMusicHandler(event.getChannelLeft(), event.getGuild());
        if (event.getMember().equals(event.getGuild().getSelfMember())) {
            this.pauseMusic(PlayerManager.getInstance(), event.getGuild());
        }
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
        String prefix = rawMessage.startsWith(Config.getInstance().getString("prefix")) ? Config.getInstance().getString("prefix") : SettingsUtil.getPrefix(event.getGuild().getIdLong());
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
