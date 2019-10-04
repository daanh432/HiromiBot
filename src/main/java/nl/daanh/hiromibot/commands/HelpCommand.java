package nl.daanh.hiromibot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromibot.CommandHandler;
import nl.daanh.hiromibot.Secrets;
import nl.daanh.hiromibot.objects.CommandInterface;

import java.util.List;

public class HelpCommand implements CommandInterface {

    private CommandHandler commandHandler;

    public HelpCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void Handle(List<String> args, GuildMessageReceivedEvent event) {
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
                    "Use `" + Secrets.PREFIX + GetInvoke() + "` for a list of commands").queue();
            return;
        }

        event.getChannel().sendMessage("Help for the command `" + command.GetInvoke() + "`\n" + command.GetHelp()).queue();
    }

    private EmbedBuilder GenerateEmbed() {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("These are all my amazing commands!");
        StringBuilder descriptionBuilder = builder.getDescriptionBuilder();
        commandHandler.GetCommands().forEach(command -> descriptionBuilder.append("`").append(command.GetInvoke()).append("` - ").append(command.GetHelp().split("\n")[0]).append("\n"));
        return builder;
    }

    @Override
    public String GetHelp() {
        return "Show help information about all commands\n" +
                "Usage: `" + Secrets.PREFIX + GetInvoke() + "` || `" + Secrets.PREFIX + GetInvoke() + " command`";
    }

    @Override
    public String GetInvoke() {
        return "help";
    }
}
