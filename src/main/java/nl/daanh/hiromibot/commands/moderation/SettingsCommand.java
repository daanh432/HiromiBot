/*
 * HiromiBot, a multipurpose open source Discord bot
 * Copyright (c) 2019 - 2020 daanh432
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package nl.daanh.hiromibot.commands.moderation;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.SettingsUtil;

import java.util.List;

public class SettingsCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        List<String> args = ctx.getArgs();
        GuildMessageReceivedEvent event = ctx.getEvent();

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
                channel.sendMessage("This setting is not found.\n" + this.getHelp()).queue();
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
                channel.sendMessage("This setting is not found.\n" + this.getHelp()).queue();
                break;
        }
    }

    @Override
    public String getHelp() {
        return "Change settings for this guild.\n" +
                "Usage: ``settings``\n" +
                "Usage: ``settings <setting>``\n" +
                "Usage: ``settings <setting> <value>``";
    }

    @Override
    public CATEGORY getCategory() {
        return CATEGORY.MODERATION;
    }

    @Override
    public String getInvoke() {
        return "settings";
    }
}
