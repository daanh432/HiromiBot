package nl.daanh.hiromibot.commands.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromibot.Constants;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.music.GuildMusicManager;
import nl.daanh.hiromibot.utils.music.PlayerManager;

import java.util.List;

public class StopCommand implements CommandInterface {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        PlayerManager playerManager = PlayerManager.getInstance();

        GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());

        musicManager.scheduler.getQueue().clear();
        musicManager.player.stopTrack();
        musicManager.player.setPaused(false);

        EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed("Stopping and clearing the queue", true);
        event.getChannel().sendMessage(embedBuilder.build()).queue();
    }

    @Override
    public String getHelp() {
        return "Stops the music player";
    }

    @Override
    public String getUsage() {
        return "Usage: `" + Constants.PREFIX + getInvoke() + "`";
    }

    @Override
    public String getInvoke() {
        return "stop";
    }
}
