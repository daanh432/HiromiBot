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

import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;

import java.util.List;

public class BanCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        Guild guild = ctx.getGuild();
        Member member = ctx.getMember();
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

        if (member == null || !member.hasPermission(Permission.BAN_MEMBERS) || !member.canInteract(targetMember)) {
            channel.sendMessage("You don't have permissions to use this command").queue();
            return;
        }

        if (!selfMember.hasPermission(Permission.BAN_MEMBERS) || !selfMember.canInteract(targetMember)) {
            channel.sendMessage("I can't ban that user or I don't have the ban members permission.").queue();
            return;
        }

        guild.ban(targetMember, 0).reason(String.format("Banned by: %#s, with reason: %s", member, kickReason)).queue();
        channel.sendMessage(String.format("User %#s has been banned.", targetMember)).queue();
    }

    @Override
    public String getHelp() {
        return "Bans specified user off the server\n" +
                "Usage: ``ban <mention>``";
    }

    @Override
    public CATEGORY getCategory() {
        return CATEGORY.MODERATION;
    }

    @Override
    public String getInvoke() {
        return "ban";
    }
}
