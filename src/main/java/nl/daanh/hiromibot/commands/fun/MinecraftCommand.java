package nl.daanh.hiromibot.commands.fun;

import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromibot.Constants;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.WebUtils;

import java.util.List;

public class MinecraftCommand implements CommandInterface {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        String errorMessage = "Dang it. Couldn't find a history of this user.";
        TextChannel textChannel = event.getChannel();
        if (args.isEmpty()) {
            textChannel.sendMessage("Arguments missing. " + getUsage()).queue();
            return;
        }

        try {
            JSONObject jsonResponse = WebUtils.fetchJsonFromUrl(String.format("https://some-random-api.ml/mc?username=%s", args.get(0)));
            if (!jsonResponse.has("username") || !jsonResponse.has("name_history")) {
                textChannel.sendMessage(errorMessage).queue();
                return;
            }

            EmbedBuilder embedBuilder = EmbedUtils.defaultEmbed();
            JSONArray nameHistory = jsonResponse.getJSONArray("name_history");

            for (int i = 0; i < nameHistory.length(); i++) {
                JSONObject name = nameHistory.getJSONObject(i);
                if (name.has("name") && name.has("changedToAt")) {
                    if (name.getString("changedToAt").contains("Orig")) {
                        embedBuilder.addField(name.getString("name"), "Users original name", false);
                    } else {
                        embedBuilder.addField(name.getString("name"), String.format("Changed to on %s", name.getString("changedToAt")), false);
                    }
                }
            }

            textChannel.sendMessage(embedBuilder.build()).queue();
        } catch (Exception e) {
            textChannel.sendMessage(errorMessage).queue();
        }
    }

    @Override
    public String getHelp() {
        return "Tells you the history about a minecraft user";
    }

    @Override
    public String getUsage() {
        return "Usage: `" + Constants.PREFIX + getInvoke() + " <minecraft username>`";
    }

    @Override
    public String getInvoke() {
        return "minecraft";
    }
}