package nl.daanh.hiromibot.objects;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public interface CommandInterface {

    void handle(List<String> args, GuildMessageReceivedEvent event);

    String getHelp();

    String getUsage();

    String getInvoke();
}
