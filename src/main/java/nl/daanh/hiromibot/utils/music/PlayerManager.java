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

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromibot.utils.EmbedUtils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class PlayerManager {
    private static PlayerManager INSTANCE;
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    private PlayerManager() {
        this.musicManagers = new HashMap<>();

        this.playerManager = new DefaultAudioPlayerManager();
        this.playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        this.playerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        this.playerManager.registerSourceManager(new BandcampAudioSourceManager());
        this.playerManager.registerSourceManager(new VimeoAudioSourceManager());
        this.playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        this.playerManager.registerSourceManager(new BeamAudioSourceManager());
        this.playerManager.registerSourceManager(new HttpAudioSourceManager());
        this.playerManager.registerSourceManager(new LocalAudioSourceManager());
    }

    public static synchronized PlayerManager getInstance() {
        if (INSTANCE == null) INSTANCE = new PlayerManager();
        return INSTANCE;
    }

    public BlockingQueue<AudioTrack> getQueue(final Guild guild) {
        GuildMusicManager guildMusicManager = this.getGuildAudioPlayer(guild);
        return guildMusicManager.scheduler.getQueue();
    }

    @Nullable
    public AudioTrack getPlayingTrack(final Guild guild) {
        GuildMusicManager guildMusicManager = this.getGuildAudioPlayer(guild);
        return guildMusicManager.player.getPlayingTrack();
    }

    @Nullable
    public TextChannel getLastChannel(final Guild guild) {
        GuildMusicManager guildMusicManager = this.getGuildAudioPlayer(guild);
        return guildMusicManager.getLastChannel();
    }

    public void setLastChannel(final Guild guild, TextChannel channel) {
        GuildMusicManager guildMusicManager = this.getGuildAudioPlayer(guild);
        guildMusicManager.setLastChannel(channel);
    }

    public boolean isPaused(final Guild guild) {
        GuildMusicManager guildMusicManager = this.getGuildAudioPlayer(guild);
        return guildMusicManager.player.isPaused();
    }

    public void setPaused(final Guild guild, final boolean paused) {
        GuildMusicManager guildMusicManager = this.getGuildAudioPlayer(guild);
        guildMusicManager.player.setPaused(paused);
    }

    public void purge(final Guild guild) {
        GuildMusicManager guildMusicManager = this.getGuildAudioPlayer(guild);
        guildMusicManager.scheduler.getQueue().clear();
        guildMusicManager.player.stopTrack();
        guildMusicManager.player.setPaused(false);
    }

    public void skipTrack(final Guild guild) {
        GuildMusicManager musicManager = this.getGuildAudioPlayer(guild);
        musicManager.scheduler.nextTrack();
    }

    private synchronized GuildMusicManager getGuildAudioPlayer(final Guild guild) {
        long guildId = guild.getIdLong();
        GuildMusicManager musicManager = this.musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(guild);
            this.musicManagers.put(guildId, musicManager);
        }

        return musicManager;
    }

    public void loadAndPlay(final TextChannel textChannel, final String trackUrl) {
        GuildMusicManager musicManager = this.getGuildAudioPlayer(textChannel.getGuild());
        this.setLastChannel(textChannel.getGuild(), textChannel);

        this.playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                try {
                    musicManager.scheduler.queue(track);
                    EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed(String.format("Adding the song %s to the queue.", track.getInfo().title), true);
                    textChannel.sendMessage(embedBuilder.build()).queue();
                } catch (QueueToBigException e) {
                    EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed("The queue is full!\nPlease wait with adding songs to the queue till there's some more space.", false);
                    textChannel.sendMessage(embedBuilder.build()).queue();
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().remove(0);
                } else {
                    playlist.getTracks().remove(firstTrack);
                }

                try {

                    int queueSize = musicManager.scheduler.getQueueSize();
                    musicManager.scheduler.queue(firstTrack);

                    List<AudioTrack> tracks = playlist.getTracks();

                    for (AudioTrack track : tracks) {
                        try {
                            musicManager.scheduler.queue(track);
                        } catch (QueueToBigException e) {
                            EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed(String.format("Playlist is to big!\nAdding %s songs from the playlist %s to the queue.", musicManager.scheduler.getMaxQueueSize() - queueSize, playlist.getName()), false);
                            textChannel.sendMessage(embedBuilder.build()).queue();
                            return;
                        }
                    }

                    EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed(String.format("Adding %s songs from the playlist ``%s`` to the queue.", playlist.getTracks().size() + 1, playlist.getName()), true);
                    textChannel.sendMessage(embedBuilder.build()).queue();
                } catch (QueueToBigException e) {
                    EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed("The queue is full!\nPlease wait with adding songs to the queue till there's some more space.", false);
                    textChannel.sendMessage(embedBuilder.build()).queue();
                }
            }

            @Override
            public void noMatches() {
                EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed(String.format("Couldn't find by %s", trackUrl), false);
                textChannel.sendMessage(embedBuilder.build()).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed(String.format("Couldn't not play %s \n```%s```", trackUrl, exception.getMessage()), false);
                textChannel.sendMessage(embedBuilder.build()).queue();
            }
        });
    }
}
