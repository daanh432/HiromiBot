package nl.daanh.hiromibot.commands.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.RandomUtils;
import nl.daanh.hiromibot.utils.music.PlayerManager;

import java.util.List;

public class ResumeCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        Guild guild = ctx.getGuild();
        TextChannel channel = ctx.getChannel();
        PlayerManager playerManager = PlayerManager.getInstance();
        Member selfMember = ctx.getSelfMember();
        Member member = ctx.getMember();

        if (selfMember.getVoiceState() != null && !selfMember.getVoiceState().inVoiceChannel()) {
            channel.sendMessage(EmbedUtils.defaultMusicEmbed("Well. There's nothing for me to resume I'm afraid.", false).build()).queue();
            return;
        }

        if (!RandomUtils.inSameVoiceChannel(member, selfMember)
                && selfMember.getVoiceState() != null
                && selfMember.getVoiceState().inVoiceChannel()) {
            channel.sendMessage(EmbedUtils.defaultMusicEmbed("You have to be in the voice channel to resume.", false).build()).queue();
            return;
        }

        if (!playerManager.isPaused(guild)) {
            EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed("Hey! I'm already playing music no need to poke me.", false);
            channel.sendMessage(embedBuilder.build()).queue();
            return;
        }


        EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed("Who poked me? I'm resuming playback for you.", true);
        channel.sendMessage(embedBuilder.build()).queue();
        playerManager.setPaused(guild, false);
        playerManager.setLastChannel(guild, channel);
    }

    @Override
    public String getHelp() {
        return "Resumes music playback after it has been paused\n" +
                "Usage: ``resume``";
    }

    @Override
    public String getInvoke() {
        return "resume";
    }

    @Override
    public CATEGORY getCategory() {
        return CATEGORY.MUSIC;
    }

    @Override
    public List<String> getAliases() {
        return List.of("continue", "unmute", "res");
    }
}
