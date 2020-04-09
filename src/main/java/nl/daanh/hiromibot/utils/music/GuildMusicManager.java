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

package nl.daanh.hiromibot.utils.music;

import lavalink.client.player.IPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromibot.utils.LavalinkUtils;

import javax.annotation.Nonnull;
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

    private final Guild guild;

    private TextChannel lastChannel;

    /**
     * Creates a player and a track scheduler.
     *
     * @param guild the guild for which the guild music manager should be created
     */
    public GuildMusicManager(@Nonnull Guild guild) {
        this.guild = guild;
        this.player = LavalinkUtils.getLavalink().getLink(guild).getPlayer();
        scheduler = new TrackScheduler(this.player, this);
        player.addListener(scheduler);
    }

    @Nullable
    public TextChannel getLastChannel() {
        return this.lastChannel;
    }

    @Nonnull
    public Guild getGuild() {
        return this.guild;
    }

    public void setLastChannel(TextChannel channel) {
        this.lastChannel = channel;
    }
}