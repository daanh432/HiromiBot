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

import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromibot.CommandManager;
import nl.daanh.hiromibot.database.DatabaseManager;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;

import java.util.List;

public class HelpCommand implements CommandInterface {
    private final CommandManager commandManager;

    public HelpCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void handle(CommandContext ctx) {
        List<String> args = ctx.getArgs();
        long guildId = ctx.getGuild().getIdLong();
        TextChannel channel = ctx.getChannel();

        if (args.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append("List of commands:\n");
            commandManager.getCommands().stream().map(CommandInterface::getInvoke).forEach(
                    (it) -> builder.append("`")
                            .append(DatabaseManager.instance.getPrefix(guildId))
                            .append(it).append("`\n")
            );
            channel.sendMessage(builder.toString()).queue();
            return;
        }

        String invoke = args.get(0);

        CommandInterface command = commandManager.getCommand(invoke);

        if (command == null) {
            channel.sendMessage("Oh oh.. It looks like the command " + invoke + " doesn't exist!").queue();
            return;
        }

        channel.sendMessage(command.getHelp()).queue();
    }

    @Override
    public CATEGORY getCategory() {
        return CATEGORY.OTHER;
    }

    @Override
    public String getInvoke() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "Shows the list of available commands\n" +
                "Usage: ``help <command>``";
    }

    @Override
    public List<String> getAliases() {
        return List.of("commands", "commandlist");
    }
}
