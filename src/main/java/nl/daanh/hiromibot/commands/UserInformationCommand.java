/*
 * HiromiBot, a multipurpose open source Discord bot
 * Copyright (c) 2019 - 2020 daanh432
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package nl.daanh.hiromibot.commands;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import nl.daanh.hiromibot.objects.CommandContext;
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
    public void handle(CommandContext ctx) {
        List<String> args = ctx.getArgs();
        TextChannel channel = ctx.getChannel();
        Guild guild = ctx.getGuild();
        Member member = ctx.getMember();
        String joinedArgs = String.join("", args);

        if (args.isEmpty()) {
            channel.sendMessage(generateEmbed(member.getUser(), member)).queue();
            return;
        }

        List<Member> foundMembers = FinderUtil.findMembers(joinedArgs, guild);

        if (foundMembers.isEmpty()) {
            channel.sendMessage("No users found for `" + joinedArgs + "`").queue();
            return;
        }

        channel.sendMessage(generateEmbed(foundMembers.get(0).getUser(), foundMembers.get(0))).queue();
    }

    @Override
    public CATEGORY getCategory() {
        return CATEGORY.OTHER;
    }

    @Override
    public String getInvoke() {
        return "userinfo";
    }

    @Override
    public String getHelp() {
        return "Displays information about yourself or a different user.\n" +
                "Usage: ``userinfo <mention>``";
    }

    @Override
    public List<String> getAliases() {
        return List.of("whois");
    }
}
