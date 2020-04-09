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

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;

import java.util.List;

public class PingCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        JDA jda = ctx.getJDA();
        TextChannel channel = ctx.getChannel();
        long startTime = System.currentTimeMillis();
        channel.sendMessage("Pong!").queue(message -> {
            long finishTime = System.currentTimeMillis();
            jda.getRestPing().queue((ping) -> message.editMessage("Gateway ping is " + jda.getGatewayPing() + "ms, Rest API Ping is " + ping + "ms, message took " + (finishTime - startTime) + "ms to send.").queue());
        });
    }

    @Override
    public String getHelp() {
        return "Returns amount of time bot takes to contact the Discord API";
    }

    @Override
    public CATEGORY getCategory() {
        return CATEGORY.OTHER;
    }

    @Override
    public String getInvoke() {
        return "ping";
    }

    @Override
    public List<String> getAliases() {
        return List.of("latency", "pong", "lag");
    }
}
