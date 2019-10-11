package nl.daanh.hiromibot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromibot.CommandHandler;
import nl.daanh.hiromibot.Constants;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.objects.DiscordBot;
import nl.daanh.hiromibot.utils.EmbedUtils;

import java.util.List;

public class HelpCommand implements CommandInterface {

    private CommandHandler commandHandler;

    public HelpCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event, DiscordBot discordBot) {
        if (args.isEmpty()) {
            if (event.getGuild().getSelfMember().hasPermission(event.getMessage().getTextChannel(), Permission.MESSAGE_EMBED_LINKS)) {
                event.getChannel().sendMessage(GenerateEmbed().build()).queue();
            } else {
                event.getChannel().sendMessage("It looks like I don't have permission to send embeds O.O").queue();
            }
            return;
        }

        CommandInterface command = commandHandler.GetCommand(String.join("", args));

        if (command == null) {
            event.getChannel().sendMessage("This command doesn't exist\n" +
                    "Use `" + Constants.PREFIX + getInvoke() + "` for a list of commands").queue();
            return;
        }

        if (command.getInvoke() != null && command.getHelp() != null && command.getInvoke() != null) {
            event.getChannel().sendMessage("Help for the command `" + command.getInvoke() + "`\n" + command.getHelp()).queue();
        } else {
            event.getChannel().sendMessage("This command exists but has an build error. Please report to bot author.").queue();
        }
    }

    private EmbedBuilder GenerateEmbed() {
        EmbedBuilder builder = EmbedUtils.defaultEmbed()
                .setTitle("These are all my amazing commands!");
        StringBuilder descriptionBuilder = builder.getDescriptionBuilder();
        for (CommandInterface command : commandHandler.GetCommands()) {
            if (command != null && command.getHelp() != null && command.getInvoke() != null) {
                String commandHelp = command.getHelp().split("\n")[0];
                descriptionBuilder.append("`")
                        .append(command.getInvoke())
                        .append("` - ")
                        .append(commandHelp)
                        .append("\n");
            }
        }

        return builder;
    }

    @Override
    public String getHelp() {
        return "Show help information about all commands\n" +
                getUsage();
    }

    @Override
    public String getUsage() {
        return "Usage: `" + Constants.PREFIX + getInvoke() + "` || `" + Constants.PREFIX + getInvoke() + " command`";
    }

    @Override
    public String getInvoke() {
        return "help";
    }
}
