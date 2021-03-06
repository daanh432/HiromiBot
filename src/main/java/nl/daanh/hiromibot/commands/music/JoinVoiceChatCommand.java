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

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.objects.SelfPermission;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.LavalinkUtils;
import nl.daanh.hiromibot.utils.music.PlayerManager;

import java.util.List;

@SelfPermission(Permission.VOICE_CONNECT)
@SelfPermission(Permission.VOICE_SPEAK)
@SelfPermission(Permission.VOICE_USE_VAD)
public class JoinVoiceChatCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        TextChannel textChannel = ctx.getChannel();
        Guild guild = ctx.getGuild();
        Member member = ctx.getMember();
        Member selfMember = ctx.getSelfMember();

        if (LavalinkUtils.isConnected(guild)) {
            if (LavalinkUtils.getConnectedChannel(guild) != null) {
                EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed(String.format("I'm already connected to `%s`.", LavalinkUtils.getConnectedChannel(guild).getName()), false);
                textChannel.sendMessage(embedBuilder.build()).queue();
                return;
            }

            textChannel.sendMessage(EmbedUtils.defaultMusicEmbed("I'm already connected to a voice channel.", false).build()).queue();
            return;
        }

        GuildVoiceState memberVoiceState = member.getVoiceState();
        if (memberVoiceState != null) {
            if (!memberVoiceState.inVoiceChannel() || memberVoiceState.getChannel() == null) {
                textChannel.sendMessage(EmbedUtils.defaultMusicEmbed("You have to be connected to a voice channel first.", false).build()).queue();
                return;
            }

            VoiceChannel voiceChannel = memberVoiceState.getChannel();

            if (!selfMember.hasPermission(voiceChannel, Permission.VOICE_CONNECT)) {
                EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed(String.format("I'm missing the permission voice connect to join `%s`.", voiceChannel.getName()), false);
                textChannel.sendMessage(embedBuilder.build()).queue();
                return;
            }

            LavalinkUtils.openConnection(voiceChannel);
            PlayerManager.getInstance().setLastChannel(guild, textChannel);
            EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed(String.format("Joining the voice channel `%s`.", voiceChannel.getName()), true);
            textChannel.sendMessage(embedBuilder.build()).queue();
            return;
        }

        textChannel.sendMessage("An error occurred trying to execute that command. Please try again later.").queue();
    }

    @Override
    public String getHelp() {
        return "Makes the bot join a voice channel\n" +
                "Usage: ``join``";
    }

    @Override
    public CATEGORY getCategory() {
        return CATEGORY.MUSIC;
    }

    @Override
    public String getInvoke() {
        return "join";
    }

    @Override
    public List<String> getAliases() {
        return List.of("connect");
    }
}
