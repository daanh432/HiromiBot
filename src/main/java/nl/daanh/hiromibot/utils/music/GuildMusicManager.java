package nl.daanh.hiromibot.utils.music;

import lavalink.client.player.IPlayer;
import net.dv8tion.jda.api.entities.Guild;
import nl.daanh.hiromibot.utils.LavalinkUtils;

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
}