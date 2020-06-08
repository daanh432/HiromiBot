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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.RandomUtils;
import nl.daanh.hiromibot.utils.music.PlayerManager;

import java.util.List;

public class PauseCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        Guild guild = ctx.getGuild();
        TextChannel channel = ctx.getChannel();
        PlayerManager playerManager = PlayerManager.getInstance();
        Member selfMember = ctx.getSelfMember();
        Member member = ctx.getMember();

        if (selfMember.getVoiceState() != null && !selfMember.getVoiceState().inVoiceChannel()) {
            channel.sendMessage(EmbedUtils.defaultMusicEmbed("Well. There's nothing for me to pause I'm afraid.", false).build()).queue();
            return;
        }

        if (!RandomUtils.inSameVoiceChannel(member, selfMember)
                && selfMember.getVoiceState() != null
                && selfMember.getVoiceState().inVoiceChannel()) {
            channel.sendMessage(EmbedUtils.defaultMusicEmbed("You have to be in the voice channel to pause.", false).build()).queue();
            return;
        }

        if (playerManager.isPaused(guild)) {
            EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed("Hey! I'm already paused no need to tell me a bedtime story.", false);
            channel.sendMessage(embedBuilder.build()).queue();
            return;
        }


        EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed("*yawns* I'm going to take a quick nap now. I'm pausing playback for you.", true);
        channel.sendMessage(embedBuilder.build()).queue();
        playerManager.setPaused(guild, true);
        playerManager.setLastChannel(guild, channel);
    }

    @Override
    public String getHelp() {
        return "Pauses music playback after if it's playing\n" +
                "Usage: ``pause``";
    }

    @Override
    public String getInvoke() {
        return "pause";
    }

    @Override
    public CATEGORY getCategory() {
        return CATEGORY.MUSIC;
    }

    @Override
    public List<String> getAliases() {
        return List.of("ps", "pa", "silent", "mute");
    }
}
