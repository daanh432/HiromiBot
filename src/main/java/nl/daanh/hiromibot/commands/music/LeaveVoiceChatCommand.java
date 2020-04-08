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

package nl.daanh.hiromibot.commands.music;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.LavalinkUtils;
import nl.daanh.hiromibot.utils.RandomUtils;
import nl.daanh.hiromibot.utils.music.PlayerManager;

import java.util.List;

public class LeaveVoiceChatCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        TextChannel textChannel = ctx.getChannel();
        Guild guild = ctx.getGuild();
        Member selfMember = ctx.getSelfMember();
        Member member = ctx.getMember();

        if (!LavalinkUtils.isConnected(guild)) {
            textChannel.sendMessage("I'm not connected to a voice channel.").queue();
            return;
        }

        VoiceChannel voiceChannel = LavalinkUtils.getConnectedChannel(guild);

        if (voiceChannel == null || !RandomUtils.inSameVoiceChannel(member, selfMember)) {
            textChannel.sendMessage(EmbedUtils.defaultMusicEmbed("You have to be in the voice channel to use this command.", false).build()).queue();
            return;
        }

        LavalinkUtils.closeConnection(ctx.getGuild());
        PlayerManager.getInstance().setLastChannel(ctx.getGuild(), null);
        textChannel.sendMessage(EmbedUtils.defaultMusicEmbed(String.format("Leaving the voice channel ``%s``.", voiceChannel.getName()), true).build()).queue();
    }

    @Override
    public String getHelp() {
        return "Makes the bot leave the voice channel\n" +
                "Usage: ``leave``";
    }

    @Override
    public CATEGORY getCategory() {
        return CATEGORY.MUSIC;
    }

    @Override
    public String getInvoke() {
        return "leave";
    }

    @Override
    public List<String> getAliases() {
        return List.of("disconnect");
    }
}
