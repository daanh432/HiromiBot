package nl.daanh.hiromibot;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromibot.commands.HelpCommand;
import nl.daanh.hiromibot.commands.PingCommand;
import nl.daanh.hiromibot.commands.UserInformationCommand;
import nl.daanh.hiromibot.commands.fun.HugCommand;
import nl.daanh.hiromibot.commands.fun.MemeCommand;
import nl.daanh.hiromibot.commands.fun.MinecraftCommand;
import nl.daanh.hiromibot.commands.fun.PatCommand;
import nl.daanh.hiromibot.commands.moderation.BanCommand;
import nl.daanh.hiromibot.commands.moderation.KickCommand;
import nl.daanh.hiromibot.commands.moderation.SettingsCommand;
import nl.daanh.hiromibot.commands.moderation.UnbanCommand;
import nl.daanh.hiromibot.commands.music.*;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.SettingsUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CommandManager {
    private final List<CommandInterface> commands = new ArrayList<>();

    public CommandManager() {
        Config config = Config.getInstance();
        // Other commands
        addCommand(new HelpCommand(this));
        addCommand(new UserInformationCommand());
        addCommand(new PingCommand());

        // Moderation commands
        if (config.getBoolean("loadModerationCommands")) {
            addCommand(new SettingsCommand());
            addCommand(new KickCommand());
            addCommand(new BanCommand());
            addCommand(new UnbanCommand());
        }

        // Fun commands
        if (config.getBoolean("loadFunCommands")) {
            addCommand(new MemeCommand());
            addCommand(new PatCommand());
            addCommand(new HugCommand());
            addCommand(new MinecraftCommand());
        }

        // Music commands
        if (config.getBoolean("loadMusicCommands")) {
            addCommand(new JoinVoiceChatCommand());
            addCommand(new LeaveVoiceChatCommand());
            addCommand(new PlayCommand());
            addCommand(new StopCommand());
            addCommand(new QueueCommand());
            addCommand(new SkipCommand());
            addCommand(new NowPlayingCommand());
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
            if (it.getAliases().stream().anyMatch((it2) -> command.getAliases().indexOf(it2) > -1)) found = true;

            return found;
        });

        if (nameFound) {
            throw new IllegalArgumentException("There's already a command defined with that name.");
        }

        commands.add(command);
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

            boolean enabledCommand = SettingsUtil.getEnabledCategories(event.getGuild().getIdLong()).stream().anyMatch((it) -> it == command.getCategory());

            if (!enabledCommand) {
                event.getChannel().sendMessage("This command is disabled on this server. Please contact an server administrator if you think this is an error").queue();
                return;
            }

            command.handle(ctx);
        }
    }
}
