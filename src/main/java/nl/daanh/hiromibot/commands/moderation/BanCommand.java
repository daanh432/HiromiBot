package nl.daanh.hiromibot.commands.moderation;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromibot.Secrets;
import nl.daanh.hiromibot.objects.CommandInterface;

import java.util.List;

public class BanCommand implements CommandInterface {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        TextChannel channel = event.getChannel();
        Member member = event.getMember();
        Member selfMember = event.getGuild().getSelfMember();

        if (args.isEmpty()) {
            channel.sendMessage("Missing arguments. " + getUsage()).queue();
            return;
        }

        List<Member> foundMembers = FinderUtil.findMembers(args.get(0), event.getGuild());

        if (foundMembers.isEmpty()) {
            channel.sendMessage("No users found for `" + args.get(0) + "`").queue();
            return;
        }

        Member targetMember = foundMembers.get(0);
        String kickReason = String.join(" ", args.subList(1, args.size()));

        if (member == null || !member.hasPermission(Permission.BAN_MEMBERS) || !member.canInteract(targetMember)) {
            channel.sendMessage("You don't have permissions to use this command").queue();
            return;
        }

        if (!selfMember.hasPermission(Permission.BAN_MEMBERS) || !selfMember.canInteract(targetMember)) {
            channel.sendMessage("I can't ban that user or I don't have the ban members permission.").queue();
            return;
        }

        event.getGuild().ban(targetMember, 0).reason(String.format("Banned by: %#s, with reason: %s", event.getAuthor(), kickReason)).queue();
        channel.sendMessage(String.format("User %#s has been banned.", targetMember)).queue();
    }

    @Override
    public String getHelp() {
        return "Bans specified user off the server\n" +
                getUsage();
    }

    @Override
    public String getUsage() {
        return "Usage: `" + Secrets.PREFIX + getInvoke() + "` [user name/@user mention/user id] <reason>";
    }

    @Override
    public String getInvoke() {
        return "ban";
    }
}
