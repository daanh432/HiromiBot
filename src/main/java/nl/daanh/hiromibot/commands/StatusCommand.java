package nl.daanh.hiromibot.commands;

import net.dv8tion.jda.api.sharding.ShardManager;
import nl.daanh.hiromibot.Bot;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.EmbedUtils;

import java.util.List;

public class StatusCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        StringBuilder stringBuilder = new StringBuilder();
        ShardManager shardManager = Bot.getInstance().getShardManager();

        stringBuilder.append("Aww! You're interested in me how sweet. Well here you go:\n");
        stringBuilder.append("I'm currently serving ").append(shardManager.getGuildCache().size()).append(" guilds\n");
        stringBuilder.append("Also we're on shard ").append(ctx.getJDA().getShardInfo().getShardId()).append(" together with ").append(ctx.getJDA().getGuildCache().size()).append(" others!\n");
        stringBuilder.append("\n");
        stringBuilder.append("Thanks for using our bot. Greetings HiromiBot team. :heart:");

        ctx.getChannel().sendMessage(EmbedUtils.embedMessage(stringBuilder.toString()).build()).queue();
    }

    @Override
    public String getHelp() {
        return "Shows the current status of the bot. Including guild count shard etc.\n" +
                "Usage: ``status``";
    }

    @Override
    public String getInvoke() {
        return "status";
    }

    @Override
    public CATEGORY getCategory() {
        return CATEGORY.OTHER;
    }

    @Override
    public List<String> getAliases() {
        return List.of("stats", "stat");
    }
}
