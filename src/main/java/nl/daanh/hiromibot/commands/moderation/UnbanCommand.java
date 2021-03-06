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

package nl.daanh.hiromibot.commands.moderation;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.objects.SelfPermission;
import nl.daanh.hiromibot.objects.UserPermission;

import java.util.List;
import java.util.stream.Collectors;

@UserPermission(Permission.BAN_MEMBERS)
@SelfPermission(value = Permission.BAN_MEMBERS, errorMessage = "Oh no! I can't unban anyone! I don't have the ban permission. *I know it's weird*")
public class UnbanCommand implements CommandInterface {
    private boolean isCorrectUser(Guild.Ban ban, String arg) {
        User bannedUser = ban.getUser();
        return bannedUser.getName().equalsIgnoreCase(arg) || bannedUser.getId().equalsIgnoreCase(arg) || String.format("%#s", bannedUser).equalsIgnoreCase(arg);
    }

    @Override
    public void handle(CommandContext ctx) {
        List<String> args = ctx.getArgs();
        String joinedArgs = String.join("", args);
        TextChannel channel = ctx.getChannel();
        Member member = ctx.getMember();
        Guild guild = ctx.getGuild();

        if (args.isEmpty()) {
            channel.sendMessage(this.getHelp()).queue();
            return;
        }

        guild.retrieveBanList().queue(bans -> {
            List<User> goodUsers = bans.stream().filter(ban -> this.isCorrectUser(ban, joinedArgs)).map(Guild.Ban::getUser).collect(Collectors.toList());

            if (goodUsers.isEmpty()) {
                channel.sendMessage(String.format("The user %s is not found or banned.", joinedArgs)).queue();
                return;
            }

            User targetUser = goodUsers.get(0);

            guild.unban(targetUser).reason(String.format("Unbanned by: %#s", member)).queue();
            channel.sendMessage(String.format("User %#s has been unbanned.", targetUser)).queue();
        });
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
