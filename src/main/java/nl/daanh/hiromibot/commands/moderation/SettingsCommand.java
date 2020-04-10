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
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromibot.database.DatabaseManager;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;

import java.util.List;

public class SettingsCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        List<String> args = ctx.getArgs();

        if (args.isEmpty()) {
            channel.sendMessage("WIP").queue();
        } else if (args.size() == 1) {
            SettingsGetterHandler(args, ctx);
        } else {
            SettingsSetterHandler(args, ctx);
        }
    }

    private void SettingsGetterHandler(List<String> args, CommandContext ctx) {
        Guild guild = ctx.getGuild();
        TextChannel channel = ctx.getChannel();
        switch (args.get(0).toLowerCase()) {
            case "prefix":
                channel.sendMessage(String.format("The prefix of this server is ``%s``", DatabaseManager.instance.getPrefix(guild.getIdLong()))).queue();
                break;
            case "musicenabled":
            case "music":
                channel.sendMessage(String.format("Music is %s on this server.", DatabaseManager.instance.getMusicEnabled(guild.getIdLong()) ? "enabled" : "disabled")).queue();
                break;
            case "funenabled":
            case "funcategory":
            case "fun":
                channel.sendMessage(String.format("Fun commands are %s on this server.", DatabaseManager.instance.getFunEnabled(guild.getIdLong()) ? "enabled" : "disabled")).queue();
                break;
            case "jointocreatechannel":
            case "createchannel":
                Long getCreateChannel = DatabaseManager.instance.getCreateVoiceChannelId(guild.getIdLong());
                if (getCreateChannel == null) {
                    channel.sendMessage(String.format("The join to create channel is not set.\nTo set this to your current channel use:\n``%s``", ctx.getMessage().getContentRaw() + " set")).queue();
                    break;
                }
                channel.sendMessage(String.format("The join to create channel is ``<#%s>``", getCreateChannel)).queue();
                break;
            default:
                channel.sendMessage("This setting is not found.\n" + this.getHelp()).queue();
                break;
        }
    }

    private void SettingsSetterHandler(List<String> args, CommandContext ctx) {
        Guild guild = ctx.getGuild();
        TextChannel channel = ctx.getChannel();
        Member member = ctx.getMember();
        switch (args.get(0).toLowerCase()) {
            case "prefix":
                DatabaseManager.instance.setPrefix(guild.getIdLong(), args.get(1));
                channel.sendMessage(String.format("Changed prefix to ``%s``", args.get(1))).queue();
                break;
            case "musicenabled":
            case "music":
                String musicEnabled = args.get(1);
                if (musicEnabled.equals("on") || musicEnabled.equals("true") || musicEnabled.equals("enabled") || musicEnabled.equals("1") || musicEnabled.equals("enable")) {
                    DatabaseManager.instance.setMusicEnabled(guild.getIdLong(), true);
                    channel.sendMessage("Enabled music on this server.").queue();
                } else {
                    DatabaseManager.instance.setMusicEnabled(guild.getIdLong(), false);
                    channel.sendMessage("Disabled music on this server.").queue();
                }
                break;
            case "funenabled":
            case "funcategory":
            case "fun":
                String funEnabled = args.get(1);
                if (funEnabled.equals("on") || funEnabled.equals("true") || funEnabled.equals("enabled") || funEnabled.equals("1") || funEnabled.equals("enable")) {
                    DatabaseManager.instance.setFunEnabled(guild.getIdLong(), true);
                    channel.sendMessage("Enabled fun commands on this server.").queue();
                } else {
                    DatabaseManager.instance.setFunEnabled(guild.getIdLong(), false);
                    channel.sendMessage("Disabled fun commands on this server.").queue();
                }
                break;
            case "jointocreatechannel":
            case "createchannel":
                if (member.getVoiceState() != null && member.getVoiceState().inVoiceChannel() && member.getVoiceState().getChannel() != null) {
                    DatabaseManager.instance.setCreateVoiceChannelId(guild.getIdLong(), member.getVoiceState().getChannel().getIdLong());
                    channel.sendMessage(String.format("I've set the join to create channel to: ``<#%s>``", member.getVoiceState().getChannel().getIdLong())).queue();
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
