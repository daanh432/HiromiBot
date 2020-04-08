package nl.daanh.hiromibot.commands.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.LavalinkUtils;
import nl.daanh.hiromibot.utils.RandomUtils;

import java.util.List;

public class StopCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        TextChannel textChannel = ctx.getChannel();
        Guild guild = ctx.getGuild();
        Member member = ctx.getMember();
        Member selfMember = ctx.getSelfMember();


        if (!RandomUtils.inSameVoiceChannel(member, selfMember)) {
            textChannel.sendMessage(EmbedUtils.defaultMusicEmbed("You have to be in the voice channel to stop the player and clear the queue.", false).build()).queue();
            return;
        }

        LavalinkUtils.closeConnection(guild);

        EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed("Stopping and clearing the queue", true);
        textChannel.sendMessage(embedBuilder.build()).queue();
    }

    @Override
    public String getHelp() {
        return "Stops the music player\n" +
                "Usage: ``stop``";
    }

    @Override
    public CATEGORY getCategory() {
        return CATEGORY.MUSIC;
    }

    @Override
    public String getInvoke() {
        return "stop";
    }

    @Override
    public List<String> getAliases() {
        return List.of("purge", "clear", "leave", "disconnect");
    }
}
