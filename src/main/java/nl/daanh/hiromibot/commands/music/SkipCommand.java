package nl.daanh.hiromibot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromibot.Constants;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.RandomUtils;
import nl.daanh.hiromibot.utils.music.GuildMusicManager;
import nl.daanh.hiromibot.utils.music.PlayerManager;
import nl.daanh.hiromibot.utils.music.TrackScheduler;

import java.util.List;

public class SkipCommand implements CommandInterface {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        TextChannel textChannel = event.getChannel();

        if (!RandomUtils.inSameVoiceChannel(event.getMember(), event.getGuild().getSelfMember())) {
            textChannel.sendMessage(EmbedUtils.defaultMusicEmbed("You have to be in the voice channel to skip songs.", false).build()).queue();
            return;
        }

        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
        TrackScheduler scheduler = musicManager.scheduler;
        AudioPlayer player = musicManager.player;

        if (player.getPlayingTrack() == null || event.getGuild().getSelfMember().getVoiceState() == null || !event.getGuild().getSelfMember().getVoiceState().inVoiceChannel()) {
            textChannel.sendMessage(EmbedUtils.defaultMusicEmbed("It doesn't look like I'm playing any music.", false).build()).queue();
            return;
        }

        try {
            AudioTrackInfo previousTrack = player.getPlayingTrack().getInfo();
            scheduler.nextTrack();

            EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed(String.format("Skipping ``%s``. Now playing ``%s``",
                    previousTrack.title,
                    player.getPlayingTrack().getInfo().title), true);
            textChannel.sendMessage(embedBuilder.build()).queue();
        } catch (Exception exception) {
            textChannel.sendMessage(EmbedUtils.defaultMusicEmbed(String.format("Something went wrong trying to skip ``%s``", player.getPlayingTrack().getInfo().title), false).build()).queue();
        }
    }

    @Override
    public String getHelp() {
        return "Skips the current playing song.";
    }

    @Override
    public String getUsage() {
        return "Usage: `" + Constants.PREFIX + getInvoke() + "`";
    }

    @Override
    public String getInvoke() {
        return "skip";
    }
}
