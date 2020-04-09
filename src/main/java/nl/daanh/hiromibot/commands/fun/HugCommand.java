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

package nl.daanh.hiromibot.commands.fun;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.WebUtils;
import org.json.JSONObject;

import java.util.List;

public class HugCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        String errorMessage = "Oh no. It looks like something went wrong when I tried to hug that person for you. O.O";
        TextChannel textChannel = ctx.getChannel();
        Member member = ctx.getMember();
        Guild guild = ctx.getGuild();
        List<String> args = ctx.getArgs();

        if (args.isEmpty()) {
            textChannel.sendMessage(this.getHelp()).queue();
            return;
        }

        try {
            JSONObject jsonResponse = WebUtils.fetchJsonFromUrl("https://some-random-api.ml/animu/hug");
            if (!jsonResponse.has("link")) {
                textChannel.sendMessage(errorMessage).queue();
                return;
            }

            List<Member> foundMembers = FinderUtil.findMembers(String.join(" ", args), guild);
            String hugTitle = String.format("UwU! %s hugged %s", member.getAsMention(), foundMembers.get(0).getAsMention());
            EmbedBuilder embedBuilder = EmbedUtils.embedImage(jsonResponse.getString("link"));
            embedBuilder.setDescription(hugTitle);
            textChannel.sendMessage(embedBuilder.build()).queue();
        } catch (Exception e) {
            textChannel.sendMessage(errorMessage).queue();
        }
    }

    @Override
    public String getHelp() {
        return "Oh oh oh yes! Hugs for everyone! (Sends a hug gif)\n" +
                "Usage: ``hug [username|mention|id]``";
    }

    @Override
    public CATEGORY getCategory() {
        return CATEGORY.FUN;
    }

    @Override
    public String getInvoke() {
        return "hug";
    }
}