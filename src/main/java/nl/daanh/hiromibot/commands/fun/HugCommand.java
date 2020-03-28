package nl.daanh.hiromibot.commands.fun;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.WebUtils;
import org.json.JSONObject;

import java.util.List;

public class HugCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        String errorMessage = "Oh no. It looks like something went wrong when I tried to hug that person for you. O.O";
        TextChannel textChannel = ctx.getChannel();
        Member member = ctx.getMember();
        Guild guild = ctx.getGuild();
        List<String> args = ctx.getArgs();

        if (args.isEmpty()) {
            textChannel.sendMessage(this.getHelp()).queue();
            return;
        }

        try {
            JSONObject jsonResponse = WebUtils.fetchJsonFromUrl("https://some-random-api.ml/animu/hug");
            if (!jsonResponse.has("link")) {
                textChannel.sendMessage(errorMessage).queue();
                return;
            }

            List<Member> foundMembers = FinderUtil.findMembers(String.join(" ", args), guild);
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
        return "Oh oh oh yes! Hugs for everyone! (Sends a hug gif)\n" +
                "Usage: ``hug [username|mention|id]``";
    }

    @Override
    public CATEGORY getCategory() {
        return CATEGORY.FUN;
    }

    @Override
    public String getInvoke() {
        return "hug";
    }
}