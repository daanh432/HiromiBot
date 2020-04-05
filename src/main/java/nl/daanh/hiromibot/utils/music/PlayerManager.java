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
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import nl.daanh.hiromibot.utils.EmbedUtils;

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
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public static synchronized PlayerManager getInstance() {
        if (INSTANCE == null) INSTANCE = new PlayerManager();
        return INSTANCE;
    }

    private static void connectToFirstVoiceChannel(AudioManager audioManager) {
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
            for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
                audioManager.openAudioConnection(voiceChannel);
                break;
            }
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
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    private void play(final Guild guild, final GuildMusicManager musicManager, final AudioTrack track) {
        connectToFirstVoiceChannel(guild.getAudioManager());


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
