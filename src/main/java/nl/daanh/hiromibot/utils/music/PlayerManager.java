package nl.daanh.hiromibot.utils.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromibot.utils.EmbedUtils;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

    private static PlayerManager INSTANCE;
    private final AudioPlayerManager audioPlayerManager;
    private final Map<Long, GuildMusicManager> musicManagers;


    private PlayerManager() {
        this.musicManagers = new HashMap<>();

        this.audioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
    }

    public static synchronized PlayerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }

    public synchronized GuildMusicManager getGuildMusicManager(Guild guild) {
        long guildId = guild.getIdLong();
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(audioPlayerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    private void play(GuildMusicManager musicManager, AudioTrack audioTrack) {
        musicManager.scheduler.queue(audioTrack);
    }

    public void loadAndPlay(TextChannel textChannel, String trackUrl) {
        GuildMusicManager musicManager = getGuildMusicManager(textChannel.getGuild());

        audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed(String.format("Adding the song %s to the queue.", track.getInfo().title), true);
                textChannel.sendMessage(embedBuilder.build()).queue();
                play(musicManager, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().remove(0);
                }

                EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed(String.format("Adding the first song %s of the playlist %s to the queue.", firstTrack.getInfo().title, playlist.getName()), true);
                textChannel.sendMessage(embedBuilder.build()).queue();

                play(musicManager, firstTrack);

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
