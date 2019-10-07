package nl.daanh.hiromibot.commands.settings;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromibot.Constants;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.GuildSettingsUtils;

import java.util.List;

public class SetPrefixCommand implements CommandInterface {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        Member member = event.getMember();
        TextChannel textChannel = event.getChannel();
        Guild guild = event.getGuild();
        GuildSettingsUtils guildSettingsUtils = new GuildSettingsUtils(guild);

        if (member != null && !member.hasPermission(Permission.MANAGE_SERVER)) {
            textChannel.sendMessage("You need the Manage Server permission to use this command").queue();
            return;
        }

        if (args.isEmpty()) {
            textChannel.sendMessage("Arguments missing. " + getUsage()).queue();
            return;
        }

        guildSettingsUtils.setPrefix(args.get(0));

        textChannel.sendMessage("The prefix has been changed to `" + args.get(0) + "`.").queue();

    }

    @Override
    public String getHelp() {
        return "Sets the prefix I'll listen to on this server.\n" + getUsage();
    }

    @Override
    public String getUsage() {
        return "Usage: `" + Constants.PREFIX + getInvoke() + " <prefix>`";
    }

    @Override
    public String getInvoke() {
        return "setprefix";
    }
}