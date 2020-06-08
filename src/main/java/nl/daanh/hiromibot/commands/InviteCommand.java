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

import net.dv8tion.jda.api.Permission;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;

import java.util.List;

public class InviteCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        String url = ctx.getJDA().getInviteUrl(List.of(Permission.MESSAGE_MANAGE, Permission.BAN_MEMBERS, Permission.NICKNAME_CHANGE, Permission.NICKNAME_MANAGE, Permission.KICK_MEMBERS, Permission.MANAGE_CHANNEL, Permission.MANAGE_EMOTES, Permission.MANAGE_PERMISSIONS, Permission.MANAGE_ROLES, Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_HISTORY, Permission.MESSAGE_MANAGE, Permission.MESSAGE_READ, Permission.MESSAGE_WRITE, Permission.VIEW_AUDIT_LOGS, Permission.VIEW_CHANNEL, Permission.VIEW_GUILD_INSIGHTS, Permission.VOICE_CONNECT, Permission.VOICE_DEAF_OTHERS, Permission.VOICE_MOVE_OTHERS, Permission.VOICE_MUTE_OTHERS, Permission.VOICE_SPEAK, Permission.VOICE_STREAM, Permission.VOICE_USE_VAD));

        ctx.getChannel().sendMessage(String.format("Feel free to send anyone the invite link: %s", url)).queue();
    }

    @Override
    public String getHelp() {
        return "Generates a invite url with the required permissions for the bot.";
    }

    @Override
    public String getInvoke() {
        return "invite";
    }

    @Override
    public CATEGORY getCategory() {
        return CATEGORY.OTHER;
    }

    @Override
    public List<String> getAliases() {
        return List.of("invitelink", "invites", "url", "install");
    }
}
