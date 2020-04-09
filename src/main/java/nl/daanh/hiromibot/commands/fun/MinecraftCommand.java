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

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.WebUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class MinecraftCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        String errorMessage = "Dang it. Couldn't find a history of this user.";
        TextChannel textChannel = ctx.getChannel();
        List<String> args = ctx.getArgs();

        if (args.isEmpty()) {
            textChannel.sendMessage("Arguments missing.\n" + this.getHelp()).queue();
            return;
        }

        try {
            JSONObject jsonResponse = WebUtils.fetchJsonFromUrl(String.format("https://some-random-api.ml/mc?username=%s", args.get(0)));
            if (!jsonResponse.has("username") || !jsonResponse.has("name_history")) {
                textChannel.sendMessage(errorMessage).queue();
                return;
            }

            EmbedBuilder embedBuilder = EmbedUtils.defaultEmbed();
            JSONArray nameHistory = jsonResponse.getJSONArray("name_history");

            for (int i = 0; i < nameHistory.length(); i++) {
                JSONObject name = nameHistory.getJSONObject(i);
                if (name.has("name") && name.has("changedToAt")) {
                    if (name.getString("changedToAt").contains("Orig")) {
                        embedBuilder.addField(name.getString("name"), "Users original name", false);
                    } else {
                        embedBuilder.addField(name.getString("name"), String.format("Changed to on %s", name.getString("changedToAt")), false);
                    }
                }
            }

            textChannel.sendMessage(embedBuilder.build()).queue();
        } catch (Exception e) {
            textChannel.sendMessage(errorMessage).queue();
        }
    }

    @Override
    public String getHelp() {
        return "Tells you the history about a minecraft user\n" +
                "Usage: ``minecraft <minecraft username>``";
    }

    @Override
    public String getInvoke() {
        return "minecraft";
    }

    @Override
    public CATEGORY getCategory() {
        return CATEGORY.FUN;
    }

    @Override
    public List<String> getAliases() {
        return List.of("mc");
    }
}