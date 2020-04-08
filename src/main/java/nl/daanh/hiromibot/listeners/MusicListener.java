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

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import nl.daanh.hiromibot.Config;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.LavalinkUtils;
import nl.daanh.hiromibot.utils.music.PlayerManager;

import javax.annotation.Nonnull;

public class MusicListener extends ListenerAdapter {
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
}
