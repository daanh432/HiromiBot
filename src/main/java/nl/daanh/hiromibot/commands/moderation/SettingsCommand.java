package nl.daanh.hiromibot.commands.moderation;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.objects.DiscordBot;

import java.util.List;

public class SettingsCommand implements CommandInterface {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event, DiscordBot discordBot) {
        TextChannel textChannel = event.getChannel();
        Member member = event.getMember();
        Guild guild = event.getGuild();
        Member selfMember = guild.getSelfMember();
        if (args.isEmpty()) {
            textChannel.sendMessage("Arguments missing. " + getUsage()).queue();
            return;
        }

        if (args.size() == 1) {
            switch (args.get(0)) {
                case "prefix":
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public String getHelp() {
        return "Change settings for this guild.";
    }

    @Override
    public String getUsage() {
        return "Usage: ``";
    }

    @Override
    public String getInvoke() {
        return "settings";
    }
}
