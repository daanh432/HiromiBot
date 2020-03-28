package nl.daanh.hiromibot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.music.GuildMusicManager;
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
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(guild);
        AudioPlayer player = musicManager.player;

        if (player.getPlayingTrack() == null || selfMember.getVoiceState() == null || !selfMember.getVoiceState().inVoiceChannel()) {
            textChannel.sendMessage(EmbedUtils.defaultMusicEmbed("It doesn't look like I'm playing any music.", false).build()).queue();
            return;
        }

        AudioTrackInfo trackInfo = player.getPlayingTrack().getInfo();
        textChannel.sendMessage(EmbedUtils.defaultMusicEmbed(String.format(
                "**Playing** [%s](%s)\n%s %s - %s",
                trackInfo.title,
                trackInfo.uri,
                player.isPaused() ? "\u23F8" : "▶",
                formatTime(player.getPlayingTrack().getPosition()),
                formatTime(player.getPlayingTrack().getDuration())
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
