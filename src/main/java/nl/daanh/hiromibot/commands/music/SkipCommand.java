package nl.daanh.hiromibot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.RandomUtils;
import nl.daanh.hiromibot.utils.music.PlayerManager;

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
        AudioTrack playingTrack = playerManager.getPlayingTrack(guild);

        if (playingTrack == null || selfMember.getVoiceState() == null || !selfMember.getVoiceState().inVoiceChannel()) {
            textChannel.sendMessage(EmbedUtils.defaultMusicEmbed("It doesn't look like I'm playing any music.", false).build()).queue();
            return;
        }

        try {
            AudioTrackInfo previousTrack = playingTrack.getInfo();
            playerManager.skipTrack(guild);

            EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed(String.format("Skipping ``%s``. Now playing ``%s``",
                    previousTrack.title,
                    playingTrack.getInfo().title), true);
            textChannel.sendMessage(embedBuilder.build()).queue();
        } catch (Exception exception) {
            textChannel.sendMessage(EmbedUtils.defaultMusicEmbed(String.format("Something went wrong trying to skip ``%s``", playingTrack.getInfo().title), false).build()).queue();
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
