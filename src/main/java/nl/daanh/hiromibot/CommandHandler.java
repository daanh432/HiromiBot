package nl.daanh.hiromibot;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromibot.commands.HelpCommand;
import nl.daanh.hiromibot.commands.PingCommand;
import nl.daanh.hiromibot.commands.UserInformationCommand;
import nl.daanh.hiromibot.commands.moderation.BanCommand;
import nl.daanh.hiromibot.commands.moderation.KickCommand;
import nl.daanh.hiromibot.commands.moderation.SettingsCommand;
import nl.daanh.hiromibot.commands.moderation.UnbanCommand;
import nl.daanh.hiromibot.commands.music.JoinVoiceChatCommand;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.objects.DiscordBot;
import nl.daanh.hiromibot.utils.GuildSettingsUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;

public class CommandHandler {

    private final Map<String, CommandInterface> commands = new HashMap<>();
    private DiscordBot discordBot = null;

    public CommandHandler(Random randomGenerator, DiscordBot discordBot) {
        this.discordBot = discordBot;
        AddCommand(new HelpCommand(this));
        AddCommand(new PingCommand());
        AddCommand(new UserInformationCommand());
        AddCommand(new KickCommand());
        AddCommand(new BanCommand());
        AddCommand(new UnbanCommand());
        AddCommand(new SettingsCommand());
        AddCommand(new JoinVoiceChatCommand());
    }

    private void AddCommand(CommandInterface command) {
        if (!commands.containsKey(command.getInvoke())) {
            commands.put(command.getInvoke(), command);
        }
    }

    public CommandInterface GetCommand(@NotNull String name) {
        return commands.get(name);
    }

    public Collection<CommandInterface> GetCommands() {
        return commands.values();
    }

    void HandleCommand(GuildMessageReceivedEvent event, GuildSettingsUtils guildSettingsUtils) {
        final String[] splitMessage = event.getMessage().getContentRaw().replaceFirst("(?i)" + Pattern.quote(Constants.PREFIX) + "|" + Pattern.quote(guildSettingsUtils.getPrefix()), "").split("\\s+");
        final String command = splitMessage[0].toLowerCase();

        if (commands.containsKey(command)) {
            final List<String> args = Arrays.asList(splitMessage).subList(1, splitMessage.length);

            commands.get(command).handle(args, event, discordBot);
        }
    }

}
