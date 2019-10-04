package nl.daanh.hiromibot.commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromibot.Secrets;
import nl.daanh.hiromibot.objects.CommandInterface;

import java.util.List;

public class PingCommand implements CommandInterface {
    @Override
    public void Handle(List<String> args, GuildMessageReceivedEvent event) {
        long startTime = System.currentTimeMillis();
        event.getChannel().sendMessage("Pong!").queue(message -> {
            long finishTime = System.currentTimeMillis();
            event.getJDA().getRestPing().queue((ping) -> message.editMessage("Gateway ping is " + event.getJDA().getGatewayPing() + "ms, Rest API Ping is " + ping + "ms, message took " + (finishTime - startTime) + "ms to send.").queue());
        });
    }

    @Override
    public String GetHelp() {
        return "Returns amount of time bot takes to contact the Discord API\n" +
                "Usage: `" + Secrets.PREFIX + GetInvoke() + "`";
    }

    @Override
    public String GetInvoke() {
        return "ping";
    }
}
