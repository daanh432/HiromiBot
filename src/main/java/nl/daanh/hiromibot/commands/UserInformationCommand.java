package nl.daanh.hiromibot.commands;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromibot.Secrets;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.EmbedUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class UserInformationCommand implements CommandInterface {
    private MessageEmbed generateEmbed(User user, Member member) {
        List<String> memberPermissions = member.getPermissions().stream().map(Permission::getName).collect(Collectors.toList());
        return EmbedUtils.defaultEmbed()
                .setColor(member.getColor())
                .setThumbnail(user.getEffectiveAvatarUrl())
                .addField("Display Name", member.getEffectiveName(), false)
                .addField("User ID (Click to see) + Mention", String.format("||%s|| (%s)", user.getId(), member.getAsMention()), false)
                .addField("Account Creation Date", user.getTimeCreated().format(DateTimeFormatter.RFC_1123_DATE_TIME), false)
                .addField("Guild Joined Date", member.getTimeJoined().format(DateTimeFormatter.RFC_1123_DATE_TIME), false)
                .addField("Has Permissions", String.format("%s", String.join(", ", memberPermissions)), false)
                .build();
    }

    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        String joinedArgs = String.join("", args);
        if (args.isEmpty() && event.getMember() != null) {
            event.getChannel().sendMessage(generateEmbed(event.getMember().getUser(), event.getMember())).queue();
        } else {
            List<Member> foundMembers = FinderUtil.findMembers(joinedArgs, event.getGuild());
            if (foundMembers.isEmpty()) {
                event.getChannel().sendMessage("No users found for `" + joinedArgs + "`").queue();
                return;
            }
            event.getChannel().sendMessage(generateEmbed(foundMembers.get(0).getUser(), foundMembers.get(0))).queue();
        }
    }

    @Override
    public String getHelp() {
        return "Displays information about yourself or a different user.\n" +
                getUsage();
    }

    @Override
    public String getUsage() {
        return "Usage: `" + Secrets.PREFIX + getInvoke() + " [user name/@user mention/user id]`";
    }

    @Override
    public String getInvoke() {
        return "userinfo";
    }
}
