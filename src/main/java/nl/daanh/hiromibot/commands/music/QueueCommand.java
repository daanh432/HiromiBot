package nl.daanh.hiromibot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import nl.daanh.hiromibot.Config;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.music.GuildMusicManager;
import nl.daanh.hiromibot.utils.music.PlayerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class QueueCommand implements CommandInterface {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        TextChannel textChannel = event.getChannel();
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(event.getGuild());
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();

        if (queue.isEmpty()) {
            EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed("It looks like nothing is queued. Add something to the queue with the following command `hi!play <url>`", false);
            textChannel.sendMessage(embedBuilder.build()).queue();
            return;
        }

        // Queue variables
        List<AudioTrack> tracks = new ArrayList<>(queue);
        int currentPage = 0;
        int pageSize = 15;
        int pageCount = (int) Math.ceil(tracks.size() / (double) pageSize);

        if (!args.isEmpty()) {
            try {
                int specifiedPage = Integer.parseInt(args.get(0)) - 1;
                if (specifiedPage >= 0 && specifiedPage < pageCount) {
                    currentPage = specifiedPage;
                } else {
                    textChannel.sendMessage(EmbedUtils.defaultMusicEmbed("Specified page is out of range. " + getUsage(), false).build()).queue();
                    return;
                }
            } catch (NumberFormatException exception) {
                textChannel.sendMessage(EmbedUtils.defaultMusicEmbed("Specified argument is not valid. " + getUsage(), false).build()).queue();
                return;
            }
        }

        // Pagination in list
        int start = Math.min(tracks.size(), Math.abs(currentPage * pageSize));
        tracks.subList(0, start).clear();
        int size = tracks.size();
        int end = Math.min(pageSize, size);
        tracks.subList(end, size).clear();

        // Embed generation of queue

        EmbedBuilder queueEmbedBuilder = EmbedUtils.defaultMusicEmbed(String.format("**Current queue: (Page %s / %s)**\n", currentPage + 1, pageCount), true);

        for (AudioTrack track : tracks) {
            AudioTrackInfo trackInfo = track.getInfo();
            String trackDuration = String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(trackInfo.length),
                    TimeUnit.MILLISECONDS.toSeconds(trackInfo.length) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(trackInfo.length))
            );
            queueEmbedBuilder.appendDescription(String.format("%s - (%s)\n", trackInfo.title, trackDuration));
        }

        textChannel.sendMessage(queueEmbedBuilder.build()).queue();
    }

    @Override
    public String getHelp() {
        return "Shows the songs that are in queue to be played.";
    }

    @Override
    public String getUsage() {
        return "Usage `" + Config.getInstance().getString("prefix") + getInvoke() + "`";
    }

    @Override
    public String getInvoke() {
        return "queue";
    }
}
