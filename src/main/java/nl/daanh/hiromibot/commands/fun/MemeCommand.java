package nl.daanh.hiromibot.commands.fun;

import kong.unirest.json.JSONObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromibot.Constants;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.WebUtils;

import java.util.List;

public class MemeCommand implements CommandInterface {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        String errorMessage = "Oh no. It looks like something went wrong trying to send that dank meme OwO";
        TextChannel textChannel = event.getChannel();
        try {
            JSONObject jsonResponse = WebUtils.fetchJsonFromUrl("https://some-random-api.ml/meme");
            if (!jsonResponse.has("image") || !jsonResponse.has("caption")) {
                textChannel.sendMessage(errorMessage).queue();
                return;
            }

            EmbedBuilder embedBuilder = EmbedUtils.embedImage(jsonResponse.getString("image"), jsonResponse.getString("caption"));
            textChannel.sendMessage(embedBuilder.build()).queue();
        } catch (Exception e) {
            textChannel.sendMessage(errorMessage).queue();
        }
    }

    @Override
    public String getHelp() {
        return "Sends a cool random meme";
    }

    @Override
    public String getUsage() {
        return "Usage: `" + Constants.PREFIX + getInvoke() + "`";
    }

    @Override
    public String getInvoke() {
        return "meme";
    }
}