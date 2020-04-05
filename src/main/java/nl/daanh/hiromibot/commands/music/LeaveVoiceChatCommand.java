package nl.daanh.hiromibot.commands.music;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.LavalinkUtils;
import nl.daanh.hiromibot.utils.RandomUtils;

import java.util.List;

public class LeaveVoiceChatCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        TextChannel textChannel = ctx.getChannel();
        Guild guild = ctx.getGuild();
        Member selfMember = ctx.getSelfMember();
        Member member = ctx.getMember();

        if (!LavalinkUtils.isConnected(guild)) {
            textChannel.sendMessage("I'm not connected to a voice channel.").queue();
            return;
        }

        VoiceChannel voiceChannel = LavalinkUtils.getConnectedChannel(guild);

        if (voiceChannel == null || !RandomUtils.inSameVoiceChannel(member, selfMember)) {
            textChannel.sendMessage(EmbedUtils.defaultMusicEmbed("You have to be in the voice channel to use this command.", false).build()).queue();
            return;
        }

        LavalinkUtils.closeConnection(ctx.getGuild());
        textChannel.sendMessage(EmbedUtils.defaultMusicEmbed(String.format("Leaving the voice channel ``%s``.", voiceChannel.getName()), true).build()).queue();
    }

    @Override
    public String getHelp() {
        return "Makes the bot leave the voice channel\n" +
                "Usage: ``leave``";
    }

    @Override
    public CATEGORY getCategory() {
        return CATEGORY.MUSIC;
    }

    @Override
    public String getInvoke() {
        return "leave";
    }

    @Override
    public List<String> getAliases() {
        return List.of("disconnect");
    }
}
