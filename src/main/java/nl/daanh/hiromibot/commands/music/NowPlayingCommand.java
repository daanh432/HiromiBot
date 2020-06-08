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

package nl.daanh.hiromibot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.music.PlayerManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class NowPlayingCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        TextChannel textChannel = ctx.getChannel();
        Guild guild = ctx.getGuild();
        Member selfMember = ctx.getSelfMember();
        PlayerManager playerManager = PlayerManager.getInstance();
        AudioTrack playingTrack = playerManager.getPlayingTrack(guild);

        if (playingTrack == null || selfMember.getVoiceState() == null || !selfMember.getVoiceState().inVoiceChannel()) {
            textChannel.sendMessage(EmbedUtils.defaultMusicEmbed("It doesn't look like I'm playing any music.", false).build()).queue();
            return;
        }

        AudioTrackInfo trackInfo = playingTrack.getInfo();
        textChannel.sendMessage(EmbedUtils.defaultMusicEmbed(String.format(
                "**Playing** [%s](%s)\n%s %s - %s",
                trackInfo.title,
                trackInfo.uri,
                playerManager.isPaused(guild) ? "\u23F8" : "▶",
                this.formatTime(playingTrack.getPosition()),
                this.formatTime(playingTrack.getDuration())
        ), true).build()).queue();
    }

    @Override
    public String getHelp() {
        return "Shows you the current song that is playing and time in song.\n" +
                "Usage: ``np``";
    }

    @Override
    public CATEGORY getCategory() {
        return CATEGORY.MUSIC;
    }

    @Override
    public String getInvoke() {
        return "np";
    }

    @Override
    public List<String> getAliases() {
        return List.of("nowplaying", "currentsong", "whatisplaying", "nowplay", "nplaying", "playing", "current", "now");
    }

    private String formatTime(long timeInMillis) {
        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
        final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
