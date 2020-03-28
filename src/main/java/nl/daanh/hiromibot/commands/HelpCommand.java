package nl.daanh.hiromibot.commands;

import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromibot.CommandManager;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.SettingsUtil;

import java.util.List;

public class HelpCommand implements CommandInterface {
    private final CommandManager commandManager;

    public HelpCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Override
    public void handle(CommandContext ctx) {
        List<String> args = ctx.getArgs();
        Long guildId = ctx.getGuild().getIdLong();
        TextChannel channel = ctx.getChannel();

        if (args.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append("List of commands:\n");
            commandManager.getCommands().stream().map(CommandInterface::getInvoke).forEach(
                    (it) -> builder.append("`")
                            .append(SettingsUtil.getPrefix(guildId))
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
