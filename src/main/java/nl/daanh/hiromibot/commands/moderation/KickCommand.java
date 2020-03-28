package nl.daanh.hiromibot.commands.moderation;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;

import java.util.List;

public class KickCommand implements CommandInterface {

    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        Member member = ctx.getMember();
        Guild guild = ctx.getGuild();
        Member selfMember = ctx.getSelfMember();
        List<String> args = ctx.getArgs();

        if (args.isEmpty()) {
            channel.sendMessage(this.getHelp()).queue();
            return;
        }

        List<Member> foundMembers = FinderUtil.findMembers(args.get(0), guild);

        if (foundMembers.isEmpty()) {
            channel.sendMessage("No users found for `" + args.get(0) + "`").queue();
            return;
        }

        Member targetMember = foundMembers.get(0);
        String kickReason = String.join(" ", args.subList(1, args.size()));

        if (member == null || !member.hasPermission(Permission.KICK_MEMBERS) || !member.canInteract(targetMember)) {
            channel.sendMessage("You don't have permissions to use this command").queue();
            return;
        }

        if (!selfMember.hasPermission(Permission.KICK_MEMBERS) || !selfMember.canInteract(targetMember)) {
            channel.sendMessage("I can't kick that user or I don't have the kick members permission.").queue();
            return;
        }

        guild.kick(targetMember, String.format("Kicked by: %#s, with reason: %s", member, kickReason)).queue();
        channel.sendMessage(String.format("User %#s has been kicked.", targetMember)).queue();
    }

    @Override
    public String getHelp() {
        return "Kicks specified user off the server\n" +
                "Usage: ``kick <mention>``";
    }

    @Override
    public CATEGORY getCategory() {
        return CATEGORY.MODERATION;
    }

    @Override
    public String getInvoke() {
        return "kick";
    }
}
