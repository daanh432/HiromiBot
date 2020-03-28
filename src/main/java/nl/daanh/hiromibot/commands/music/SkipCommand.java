package nl.daanh.hiromibot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.RandomUtils;
import nl.daanh.hiromibot.utils.music.GuildMusicManager;
import nl.daanh.hiromibot.utils.music.PlayerManager;
import nl.daanh.hiromibot.utils.music.TrackScheduler;

public class SkipCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        TextChannel textChannel = ctx.getChannel();
        Guild guild = ctx.getGuild();
        Member member = ctx.getMember();
        Member selfMember = ctx.getSelfMember();

        if (!RandomUtils.inSameVoiceChannel(member, selfMember)) {
            textChannel.sendMessage(EmbedUtils.defaultMusicEmbed("You have to be in the voice channel to skip songs.", false).build()).queue();
            return;
        }

        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(guild);
        TrackScheduler scheduler = musicManager.scheduler;
        AudioPlayer player = musicManager.player;

        if (player.getPlayingTrack() == null || selfMember.getVoiceState() == null || !selfMember.getVoiceState().inVoiceChannel()) {
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
        return "Skips the current playing song.\n" +
                "Usage: ``skip``";
    }

    @Override
    public CATEGORY getCategory() {
        return CATEGORY.MUSIC;
    }

    @Override
    public String getInvoke() {
        return "skip";
    }
}
