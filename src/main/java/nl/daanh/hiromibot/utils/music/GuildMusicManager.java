package nl.daanh.hiromibot.utils.music;

import lavalink.client.player.IPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromibot.utils.LavalinkUtils;

import javax.annotation.Nullable;

/**
 * Holder for both the player and a track scheduler for one guild.
 */
public class GuildMusicManager {
    /**
     * Audio player for the guild.
     */
    public final IPlayer player;
    /**
     * Track scheduler for the player.
     */
    public final TrackScheduler scheduler;

    private TextChannel lastChannel;

    /**
     * Creates a player and a track scheduler.
     *
     * @param guild the guild for which the guild music manager should be created
     */
    public GuildMusicManager(Guild guild) {
        this.player = LavalinkUtils.getLavalink().getLink(guild).getPlayer();
        scheduler = new TrackScheduler(this.player);
        player.addListener(scheduler);
    }

    @Nullable
    public TextChannel getLastChannel() {
        return this.lastChannel;
    }

    public void setLastChannel(TextChannel channel) {
        this.lastChannel = channel;
    }
}