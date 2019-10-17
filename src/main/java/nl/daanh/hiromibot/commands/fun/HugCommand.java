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

public class HugCommand implements CommandInterface {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        String errorMessage = "Oh no. It looks like something went wrong when I tried to hug that person for you. O.O";
        TextChannel textChannel = event.getChannel();
        Member member = event.getMember();

        if (args.isEmpty()) {
            textChannel.sendMessage("Who do you want to hug? " + getUsage()).queue();
            return;
        }

        try {
            JSONObject jsonResponse = WebUtils.fetchJsonFromUrl("https://some-random-api.ml/animu/hug");
            if (!jsonResponse.has("link")) {
                textChannel.sendMessage(errorMessage).queue();
                return;
            }

            List<Member> foundMembers = FinderUtil.findMembers(String.join(" ", args), event.getGuild());
            String hugTitle = String.format("UwU! %s hugged %s", member.getAsMention(), foundMembers.get(0).getAsMention());
            EmbedBuilder embedBuilder = EmbedUtils.embedImage(jsonResponse.getString("link"));
            embedBuilder.setDescription(hugTitle);
            textChannel.sendMessage(embedBuilder.build()).queue();
        } catch (Exception e) {
            textChannel.sendMessage(errorMessage).queue();
        }
    }

    @Override
    public String getHelp() {
        return "Hug someone! Sends a hug gif";
    }

    @Override
    public String getUsage() {
        return "Usage: `" + Constants.PREFIX + getInvoke() + " [user name/@user mention/user id]`";
    }

    @Override
    public String getInvoke() {
        return "hug";
    }
}