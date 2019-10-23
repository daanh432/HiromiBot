package nl.daanh.hiromibot.commands.moderation;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromibot.Constants;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.SettingsUtil;

import java.util.List;

public class SettingsCommand implements CommandInterface {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        Guild guild = event.getGuild();
        TextChannel channel = event.getChannel();

        if (args.isEmpty()) {
            channel.sendMessage("WIP").queue();
            return;
        }

        if (args.size() == 1) {
            switch (args.get(0).toLowerCase()) {
                case "prefix":
                    channel.sendMessage(String.format("The prefix of this server is ``%s``", SettingsUtil.getPrefix(guild.getIdLong()))).queue();
                    break;
                case "musicenabled":
                    channel.sendMessage(String.format("Music is %s on this server.", SettingsUtil.getMusicEnabled(guild.getIdLong()) ? "enabled" : "disabled")).queue();
                    break;
                default:
                    channel.sendMessage("This setting is not found. " + getUsage()).queue();
                    break;
            }
        }

        if (args.size() == 2) {
            switch (args.get(0).toLowerCase()) {
                case "prefix":
                    SettingsUtil.setPrefix(guild.getIdLong(), args.get(1));
                    channel.sendMessage(String.format("Changed prefix to ``%s``", args.get(1))).queue();
                    break;
                case "musicenabled":
                    String musicEnabled = args.get(1);
                    if (musicEnabled.equals("on") || musicEnabled.equals("true") || musicEnabled.equals("enabled") || musicEnabled.equals("1") || musicEnabled.equals("enable")) {
                        SettingsUtil.setMusicEnabled(guild.getIdLong(), true);
                        channel.sendMessage("Enabled music on this server.").queue();
                    } else {
                        SettingsUtil.setMusicEnabled(guild.getIdLong(), false);
                        channel.sendMessage("Disabled music on this server.").queue();
                    }
                    break;
                default:
                    channel.sendMessage("This setting is not found. " + getUsage()).queue();
                    break;
            }
        }
    }

    @Override
    public String getHelp() {
        return "Change settings for this guild.";
    }

    @Override
    public String getUsage() {
        return "Usage: `" + Constants.PREFIX + getInvoke() + "`";
    }

    @Override
    public String getInvoke() {
        return "settings";
    }
}
