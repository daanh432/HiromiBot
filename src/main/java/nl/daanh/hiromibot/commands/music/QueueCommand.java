package nl.daanh.hiromibot.commands.music;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.music.GuildMusicManager;
import nl.daanh.hiromibot.utils.music.PlayerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class QueueCommand implements CommandInterface {
    private final Paginator.Builder pbuilder;

    public QueueCommand(EventWaiter eventWaiter) {
        pbuilder = new Paginator.Builder().setColumns(1)
                .setItemsPerPage(20)
                .showPageNumbers(true)
                .waitOnSinglePage(false)
                .useNumberedItems(true)
                .setFinalAction(m -> {
                    try {
                        m.clearReactions().queue();
                    } catch (PermissionException ex) {
                        m.delete().queue();
                    }
                })
                .setEventWaiter(eventWaiter)
                .setTimeout(1, TimeUnit.MINUTES);
    }

    @Override
    public void handle(CommandContext ctx) {
        List<String> args = ctx.getArgs();
        TextChannel channel = ctx.getChannel();
        Guild guild = ctx.getGuild();
        Member member = ctx.getMember();
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(guild);
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();

        if (queue.isEmpty()) {
            EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed("It looks like nothing is queued. Add something to the queue with the following command `hi!play <url>`", false);
            channel.sendMessage(embedBuilder.build()).queue();
            return;
        }

        List<AudioTrack> tracks = new ArrayList<>(queue);
        int currentPage = 0;
        int pageSize = 20;
        int pageCount = (int) Math.ceil(tracks.size() / (double) pageSize);

        if (!args.isEmpty()) {
            try {
                int specifiedPage = Integer.parseInt(args.get(0)) - 1;
                if (specifiedPage >= 0 && specifiedPage < pageCount) {
                    currentPage = specifiedPage;
                } else {
                    channel.sendMessage(EmbedUtils.defaultMusicEmbed("Specified page is out of range. " + getUsage(), false).build()).queue();
                    return;
                }
            } catch (NumberFormatException exception) {
                channel.sendMessage(EmbedUtils.defaultMusicEmbed("Specified page is not valid. " + getUsage(), false).build()).queue();
                return;
            }
        }

        // Pagination in list

//        tracks.subList(end, size).clear();
        pbuilder.clearItems();

        for (AudioTrack track : tracks) {
            AudioTrackInfo trackInfo = track.getInfo();
            String trackDuration = String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(trackInfo.length),
                    TimeUnit.MILLISECONDS.toSeconds(trackInfo.length) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(trackInfo.length))
            );
            pbuilder.addItems(String.format("%s - (%s)\n", trackInfo.title, trackDuration));
        }


        Paginator p = pbuilder
                .setText(String.format("Here you go %#s! This is the current queue.\n" +
                        "Use the buttons down below to switch pages.", member))
                .setUsers(member.getUser())
                .build();

        p.paginate(channel, currentPage);


    }

    @Override
    public String getHelp() {
        return "Shows the songs that are in queue to be played.";
    }

    public String getUsage() {
        return "Usage: ``queue <page number>``";
    }

    @Override
    public CATEGORY getCategory() {
        return CATEGORY.MUSIC;
    }

    @Override
    public String getInvoke() {
        return "queue";
    }

    @Override
    public List<String> getAliases() {
        return List.of("schedule");
    }
}
