package nl.daanh.hiromibot.commands.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromibot.Constants;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.music.PlayerManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class PlayCommand implements CommandInterface {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        TextChannel textChannel = event.getChannel();

        if (args.isEmpty()) {
            EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed(String.format("Please specify a url or song title you want me to play. %s", getUsage()), false);
            textChannel.sendMessage(embedBuilder.build()).queue();
            return;
        }

        String input = String.join(" ", args);

        if (!isUrl(input) && !input.startsWith("ytsearch:")) {
            // TODO Youtube API for search
            EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed(String.format("Please specify a valid YouTube or SoundCloud link. %s", getUsage()), false);
            textChannel.sendMessage(embedBuilder.build()).queue();
            return;
        }

        PlayerManager manager = PlayerManager.getInstance();
        manager.loadAndPlay(event.getChannel(), input);
    }

    private boolean isUrl(String input) {
        try {
            new URL(input);
            return true;
        } catch (MalformedURLException exception) {
            return false;
        }
    }

    @Override
    public String getHelp() {
        return "Plays a song from a youtube video or playlist. \n" + getUsage();
    }

    @Override
    public String getUsage() {
        return "Usage: `" + Constants.PREFIX + getInvoke() + " [url, playlist url]`";
    }

    @Override
    public String getInvoke() {
        return "play";
    }
}
