package nl.daanh.hiromibot.commands.moderation;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromibot.objects.CommandInterface;

import java.util.List;

public class SettingsCommand implements CommandInterface {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        //TODO Handle method for settings command
    }

    @Override
    public String getHelp() {
        return "Change settings for this guild.";
    }

    @Override
    public String getUsage() {
        return "Usage: ``";
    }

    @Override
    public String getInvoke() {
        return "settings";
    }
}
