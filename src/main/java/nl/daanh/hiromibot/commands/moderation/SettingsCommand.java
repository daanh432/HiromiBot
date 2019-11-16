package nl.daanh.hiromibot.commands.moderation;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromibot.Config;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.SettingsUtil;

import java.util.List;

public class SettingsCommand implements CommandInterface {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        TextChannel channel = event.getChannel();

        if (args.isEmpty()) {
            channel.sendMessage("WIP").queue();
        } else if (args.size() == 1) {
            SettingsGetterHandler(args, event);
        } else {
            SettingsSetterHandler(args, event);
        }
    }

    private void SettingsGetterHandler(List<String> args, GuildMessageReceivedEvent event) {
        Guild guild = event.getGuild();
        TextChannel channel = event.getChannel();
        switch (args.get(0).toLowerCase()) {
            case "prefix":
                channel.sendMessage(String.format("The prefix of this server is ``%s``", SettingsUtil.getPrefix(guild.getIdLong()))).queue();
                break;
            case "musicenabled":
            case "music":
                channel.sendMessage(String.format("Music is %s on this server.", SettingsUtil.getMusicEnabled(guild.getIdLong()) ? "enabled" : "disabled")).queue();
                break;
            case "funenabled":
            case "funcategory":
            case "fun":
                channel.sendMessage(String.format("Fun commands are %s on this server.", SettingsUtil.getFunEnabled(guild.getIdLong()) ? "enabled" : "disabled")).queue();
                break;
            default:
                channel.sendMessage("This setting is not found. " + getUsage()).queue();
                break;
        }
    }

    private void SettingsSetterHandler(List<String> args, GuildMessageReceivedEvent event) {
        Guild guild = event.getGuild();
        TextChannel channel = event.getChannel();
        switch (args.get(0).toLowerCase()) {
            case "prefix":
                SettingsUtil.setPrefix(guild.getIdLong(), args.get(1));
                channel.sendMessage(String.format("Changed prefix to ``%s``", args.get(1))).queue();
                break;
            case "musicenabled":
            case "music":
                String musicEnabled = args.get(1);
                if (musicEnabled.equals("on") || musicEnabled.equals("true") || musicEnabled.equals("enabled") || musicEnabled.equals("1") || musicEnabled.equals("enable")) {
                    SettingsUtil.setMusicEnabled(guild.getIdLong(), true);
                    channel.sendMessage("Enabled music on this server.").queue();
                } else {
                    SettingsUtil.setMusicEnabled(guild.getIdLong(), false);
                    channel.sendMessage("Disabled music on this server.").queue();
                }
                break;
            case "funenabled":
            case "funcategory":
            case "fun":
                String funEnabled = args.get(1);
                if (funEnabled.equals("on") || funEnabled.equals("true") || funEnabled.equals("enabled") || funEnabled.equals("1") || funEnabled.equals("enable")) {
                    SettingsUtil.setFunEnabled(guild.getIdLong(), true);
                    channel.sendMessage("Enabled fun commands on this server.").queue();
                } else {
                    SettingsUtil.setFunEnabled(guild.getIdLong(), false);
                    channel.sendMessage("Disabled fun commands on this server.").queue();
                }
                break;
            default:
                channel.sendMessage("This setting is not found. " + getUsage()).queue();
                break;
        }
    }

    @Override
    public String getHelp() {
        return "Change settings for this guild.";
    }

    @Override
    public String getUsage() {
        return "Usage: `" + Config.getInstance().getString("prefix") + getInvoke() + "`";
    }

    @Override
    public String getInvoke() {
        return "settings";
    }
}
