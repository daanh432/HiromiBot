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
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.SettingsUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;

public class CommandHandler {
    private final Map<String, CommandInterface> allCommands = new HashMap<>();
    private final Map<String, CommandInterface> otherCommands = new HashMap<>();
    private final Map<String, CommandInterface> funCommands = new HashMap<>();
    private final Map<String, CommandInterface> moderationCommands = new HashMap<>();
    private final Map<String, CommandInterface> musicCommands = new HashMap<>();
    CommandHandler(Random randomGenerator) {
        Config config = Config.getInstance();
        // Other commands
        AddCommand(new HelpCommand(this), categories.other);
        AddCommand(new UserInformationCommand(), categories.other);
        AddCommand(new PingCommand(), categories.other);

        // Moderation commands
        if (config.getBoolean("loadModerationCommands")) {
            AddCommand(new SettingsCommand(), categories.moderation);
            AddCommand(new KickCommand(), categories.moderation);
            AddCommand(new BanCommand(), categories.moderation);
            AddCommand(new UnbanCommand(), categories.moderation);
        }

        // Fun commands
        if (config.getBoolean("loadFunCommands")) {
            AddCommand(new MemeCommand(), categories.fun);
            AddCommand(new PatCommand(), categories.fun);
            AddCommand(new HugCommand(), categories.fun);
            AddCommand(new MinecraftCommand(), categories.fun);
        }

        // Music commands
        if (config.getBoolean("loadMusicCommands")) {
            AddCommand(new JoinVoiceChatCommand(), categories.music);
            AddCommand(new LeaveVoiceChatCommand(), categories.music);
            AddCommand(new PlayCommand(), categories.music);
            AddCommand(new StopCommand(), categories.music);
            AddCommand(new QueueCommand(), categories.music);
            AddCommand(new SkipCommand(), categories.music);
            AddCommand(new NowPlayingCommand(), categories.music);
        }
    }

    private void AddCommand(CommandInterface command, categories category) {
        switch (category) {
            case fun:
                funCommands.put(command.getInvoke(), command);
                break;
            case moderation:
                moderationCommands.put(command.getInvoke(), command);
                break;
            case music:
                musicCommands.put(command.getInvoke(), command);
                break;
            default:
                otherCommands.put(command.getInvoke(), command);
                break;
        }
    }

    public CommandInterface GetCommand(@NotNull String name) {
        return allCommands.get(name);
    }

    public Collection<CommandInterface> GetCommands() {
        return allCommands.values();
    }

    void HandleCommand(GuildMessageReceivedEvent event, String prefix) {
        final Long guildId = event.getGuild().getIdLong();
        final String[] splitMessage = event.getMessage().getContentRaw().replaceFirst("(?i)" + Pattern.quote(prefix), "").split("\\s+");
        final String command = splitMessage[0].toLowerCase();
        final List<String> args = Arrays.asList(splitMessage).subList(1, splitMessage.length);

        if (SettingsUtil.getMusicEnabled(guildId) && musicCommands.containsKey(command)) {
            musicCommands.get(command).handle(args, event);
        } else if (SettingsUtil.getFunEnabled(guildId) && funCommands.containsKey(command)) {
            funCommands.get(command).handle(args, event);
        } else if (moderationCommands.containsKey(command)) {
            moderationCommands.get(command).handle(args, event);
        } else if (otherCommands.containsKey(command)) {
            otherCommands.get(command).handle(args, event);
        }
    }

    public enum categories {
        other,
        fun,
        moderation,
        music
    }
}
