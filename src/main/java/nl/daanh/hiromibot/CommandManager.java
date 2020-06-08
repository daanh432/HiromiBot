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

package nl.daanh.hiromibot;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromibot.commands.*;
import nl.daanh.hiromibot.commands.fun.HugCommand;
import nl.daanh.hiromibot.commands.fun.MemeCommand;
import nl.daanh.hiromibot.commands.fun.PatCommand;
import nl.daanh.hiromibot.commands.moderation.BanCommand;
import nl.daanh.hiromibot.commands.moderation.KickCommand;
import nl.daanh.hiromibot.commands.moderation.SettingsCommand;
import nl.daanh.hiromibot.commands.moderation.UnbanCommand;
import nl.daanh.hiromibot.commands.music.*;
import nl.daanh.hiromibot.database.DatabaseManager;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.objects.SelfPermission;
import nl.daanh.hiromibot.objects.UserPermission;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {
    private final List<CommandInterface> commands = new ArrayList<>();

    public CommandManager(EventWaiter eventWaiter) {
        final Config config = Config.getInstance();

        // Other commands
        this.addCommand(new HelpCommand(this));
        this.addCommand(new UserInformationCommand());
        this.addCommand(new PingCommand());
        this.addCommand(new StatusCommand());
        this.addCommand(new InviteCommand());
        this.addCommand(new SettingsCommand());

        // Moderation commands
        if (config.getBoolean("loadModerationCommands")) {
            this.addCommand(new KickCommand());
            this.addCommand(new BanCommand());
            this.addCommand(new UnbanCommand());
        }

        // Fun commands
        if (config.getBoolean("loadFunCommands")) {
            this.addCommand(new MemeCommand());
            this.addCommand(new PatCommand());
            this.addCommand(new HugCommand());
//            this.addCommand(new MinecraftCommand());
        }

        // Music commands
        if (config.getBoolean("loadMusicCommands")) {
            this.addCommand(new JoinVoiceChatCommand());
            this.addCommand(new PlayCommand());
            this.addCommand(new StopCommand());
            this.addCommand(new QueueCommand(eventWaiter));
            this.addCommand(new SkipCommand());
            this.addCommand(new NowPlayingCommand());
            this.addCommand(new ResumeCommand());
            this.addCommand(new PauseCommand());
        }
    }

    private void addCommand(CommandInterface command) {
        boolean nameFound = this.commands.stream().anyMatch((it) -> {
            // it command
            boolean found = false;
            // Invoke invoke
            if (it.getInvoke().equalsIgnoreCase(command.getInvoke())) found = true;

            // Invoke alias
            if (command.getAliases().stream().anyMatch((it2) -> it2.equalsIgnoreCase(it.getInvoke()))) found = true;

            // Alias invoke
            if (it.getAliases().stream().anyMatch((it2) -> it2.equalsIgnoreCase(command.getInvoke()))) found = true;

            // Alias alias
            if (it.getAliases().stream().anyMatch((it2) -> command.getAliases().contains(it2))) found = true;

            return found;
        });

        if (nameFound) {
            throw new IllegalArgumentException("There's already a command defined with that name.");
        }

        this.commands.add(command);
    }

    @Nullable
    public CommandInterface getCommand(String invoke) {
        String invokeLowerCase = invoke.toLowerCase();

        for (CommandInterface command : this.commands) {
            if (command.getInvoke().equals(invokeLowerCase) || command.getAliases().contains(invokeLowerCase)) {
                return command;
            }
        }

        return null;
    }

    public List<CommandInterface> getCommands() {
        return this.commands;
    }

    public void handle(GuildMessageReceivedEvent event, String prefix) {
        final String[] splitMessage = event.getMessage().getContentRaw().replaceFirst("(?i)" + Pattern.quote(prefix), "").split("\\s+");
        final String invoke = splitMessage[0].toLowerCase();
        final List<String> args = Arrays.asList(splitMessage).subList(1, splitMessage.length);

        CommandInterface command = this.getCommand(invoke);

        if (command != null) {
            event.getChannel().sendTyping().queue();

            CommandContext ctx = new CommandContext(event, args);

            boolean enabledCommand = DatabaseManager.instance.getEnabledCategories(event.getGuild().getIdLong()).stream().anyMatch((it) -> it == command.getCategory());

            if (!enabledCommand) {
                event.getChannel().sendMessage("This command is disabled on this server. Please contact an server administrator if you think this is an error.").queue();
                return;
            }

            for (UserPermission annotation : command.getClass().getAnnotationsByType(UserPermission.class)) {
                if (!ctx.getMember().hasPermission(annotation.value())) {
                    ctx.getChannel().sendMessage(String.format("You don't have the permission ``%s``", annotation.value())).queue();
                    return;
                }
            }

            for (SelfPermission annotation : command.getClass().getAnnotationsByType(SelfPermission.class)) {
                if (!ctx.getSelfMember().hasPermission(annotation.value())) {
                    ctx.getChannel().sendMessage(String.format("Oops. It looks like I don't have the permission ``%s``", annotation.value())).queue();
                    return;
                }
            }

            command.handle(ctx);
        }
    }
}
