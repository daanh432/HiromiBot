package nl.daanh.hiromibot.commands.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;

import java.util.List;
import java.util.stream.Collectors;

public class UnbanCommand implements CommandInterface {
    private boolean isCorrectUser(Guild.Ban ban, String arg) {
        User bannedUser = ban.getUser();
        return bannedUser.getName().equalsIgnoreCase(arg) || bannedUser.getId().equalsIgnoreCase(arg)
                || String.format("%#s", bannedUser).equalsIgnoreCase(arg);
    }

    @Override
    public void handle(CommandContext ctx) {
        List<String> args = ctx.getArgs();
        String joinedArgs = String.join("", args);
        TextChannel channel = ctx.getChannel();
        Member member = ctx.getMember();
        Member selfMember = ctx.getSelfMember();
        Guild guild = ctx.getGuild();

        if (args.isEmpty()) {
            channel.sendMessage(this.getHelp()).queue();
            return;
        }

        if (member == null || !member.hasPermission(Permission.BAN_MEMBERS)) {
            channel.sendMessage("You don't have permissions to use this command").queue();
            return;
        }

        if (!selfMember.hasPermission(Permission.BAN_MEMBERS)) {
            channel.sendMessage("I need the ban members permission to unban users. *I know it's weird*").queue();
            return;
        }

        guild.retrieveBanList().queue(bans -> {
            List<User> goodUsers = bans.stream().filter(ban -> isCorrectUser(ban, joinedArgs)).map(Guild.Ban::getUser).collect(Collectors.toList());

            if (goodUsers.isEmpty()) {
                channel.sendMessage(String.format("The user %s is not found or banned.", joinedArgs)).queue();
                return;
            }

            User targetUser = goodUsers.get(0);

            guild.unban(targetUser).reason(String.format("Unbanned by: %#s", member)).queue();
            channel.sendMessage(String.format("User %#s has been unbanned.", targetUser)).queue();
        });

//        event.getGuild().kick(targetMember, String.format("Kicked by: %#s, with reason: %s", event.getAuthor(), kickReason)).queue();
//        channel.sendMessage(String.format("User %#s has been unbanned.", targetMember)).queue();
    }

    @Override
    public String getHelp() {
        return "Unbans the specified user from the guild.\n" +
                "Usage: ``unban <mention>``";
    }

    @Override
    public CATEGORY getCategory() {
        return CATEGORY.MODERATION;
    }

    @Override
    public String getInvoke() {
        return "unban";
    }
}
