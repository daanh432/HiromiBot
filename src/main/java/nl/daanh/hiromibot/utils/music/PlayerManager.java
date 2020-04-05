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
import net.dv8tion.jda.api.entities.VoiceChannel;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.LavalinkUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class PlayerManager {
    private static PlayerManager INSTANCE;
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    private PlayerManager() {
        this.musicManagers = new HashMap<>();

        this.playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        playerManager.registerSourceManager(new BandcampAudioSourceManager());
        playerManager.registerSourceManager(new VimeoAudioSourceManager());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        playerManager.registerSourceManager(new BeamAudioSourceManager());
        playerManager.registerSourceManager(new HttpAudioSourceManager());
        playerManager.registerSourceManager(new LocalAudioSourceManager());
    }

    public static synchronized PlayerManager getInstance() {
        if (INSTANCE == null) INSTANCE = new PlayerManager();
        return INSTANCE;
    }

    private static void connectToFirstVoiceChannel(Guild guild) {
        for (VoiceChannel voiceChannel : guild.getVoiceChannels()) {
            LavalinkUtils.openConnection(voiceChannel);
            break;
        }
    }

    public BlockingQueue<AudioTrack> getQueue(final Guild guild) {
        GuildMusicManager guildMusicManager = this.getGuildAudioPlayer(guild);
        return guildMusicManager.scheduler.getQueue();
    }

    public AudioTrack getPlayingTrack(final Guild guild) {
        GuildMusicManager guildMusicManager = this.getGuildAudioPlayer(guild);
        return guildMusicManager.player.getPlayingTrack();
    }

    public boolean isPaused(final Guild guild) {
        GuildMusicManager guildMusicManager = this.getGuildAudioPlayer(guild);
        return guildMusicManager.player.isPaused();
    }

    public void purge(final Guild guild) {
        GuildMusicManager guildMusicManager = this.getGuildAudioPlayer(guild);
        guildMusicManager.scheduler.getQueue().clear();
        guildMusicManager.player.stopTrack();
        guildMusicManager.player.setPaused(false);
    }

    public void skipTrack(final Guild guild) {
        GuildMusicManager musicManager = getGuildAudioPlayer(guild);
        musicManager.scheduler.nextTrack();
    }

    private synchronized GuildMusicManager getGuildAudioPlayer(final Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(guild);
            musicManagers.put(guildId, musicManager);
        }

        return musicManager;
    }

    private void play(final Guild guild, final GuildMusicManager musicManager, final AudioTrack track) {
        connectToFirstVoiceChannel(guild);
        musicManager.scheduler.queue(track);
    }

    private void skipTrack(final TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.nextTrack();

        EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed("Skipping to the next track.", true);
        channel.sendMessage(embedBuilder.build()).queue();
    }

    public void loadAndPlay(final TextChannel textChannel, final String trackUrl) {
        GuildMusicManager musicManager = getGuildAudioPlayer(textChannel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed(String.format("Adding the song %s to the queue.", track.getInfo().title), true);
                textChannel.sendMessage(embedBuilder.build()).queue();
                play(textChannel.getGuild(), musicManager, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().remove(0);
                }

                EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed(String.format("Adding the first song %s of the playlist %s to the queue.", firstTrack.getInfo().title, playlist.getName()), true);
                textChannel.sendMessage(embedBuilder.build()).queue();

                play(textChannel.getGuild(), musicManager, firstTrack);

                playlist.getTracks().forEach(musicManager.scheduler::queue);
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
