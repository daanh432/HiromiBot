package nl.daanh.hiromibot.commands.fun;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import kong.unirest.json.JSONObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromibot.Constants;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.WebUtils;

import java.util.List;

public class PatCommand implements CommandInterface {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        String errorMessage = "Oh no. It looks like something went wrong when I tried to pat that person for you. >.<";
        TextChannel textChannel = event.getChannel();
        Member member = event.getMember();

        if (args.isEmpty()) {
            textChannel.sendMessage("Who do you want to pat? " + getUsage()).queue();
            return;
        }

        try {
            JSONObject jsonResponse = WebUtils.fetchJsonFromUrl("https://some-random-api.ml/animu/pat");
            if (!jsonResponse.has("link")) {
                textChannel.sendMessage(errorMessage).queue();
                return;
            }

            List<Member> foundMembers = FinderUtil.findMembers(String.join(" ", args), event.getGuild());
            String patTitle = String.format("UwU! %s pats %s", member.getAsMention(), foundMembers.get(0).getAsMention());
            EmbedBuilder embedBuilder = EmbedUtils.embedImage(jsonResponse.getString("link"));
            embedBuilder.setDescription(patTitle);
            textChannel.sendMessage(embedBuilder.build()).queue();
        } catch (Exception e) {
            textChannel.sendMessage(errorMessage).queue();
        }
    }

    @Override
    public String getHelp() {
        return "Pat someone! Sends a patting gif";
    }

    @Override
    public String getUsage() {
        return "Usage: `" + Constants.PREFIX + getInvoke() + " [user name/@user mention/user id]`";
    }

    @Override
    public String getInvoke() {
        return "pat";
    }
}