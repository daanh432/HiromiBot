/*
 * HiromiBot, a multipurpose open source Discord bot
 * Copyright (c) 2019 - 2020 daanh432
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
        BlockingQueue<AudioTrack> queue = playerManager.getQueue(guild);

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

        long totalDuration = 0;

        for (AudioTrack track : tracks) {
            AudioTrackInfo trackInfo = track.getInfo();
            totalDuration = totalDuration + trackInfo.length;
            String trackDuration = String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(trackInfo.length),
                    TimeUnit.MILLISECONDS.toSeconds(trackInfo.length) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(trackInfo.length))
            );
            pbuilder.addItems(String.format("%s - (%s)\n", trackInfo.title, trackDuration));
        }

        long hours = TimeUnit.MILLISECONDS.toHours(totalDuration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(totalDuration) - TimeUnit.HOURS.toMinutes(hours);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(totalDuration) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes);

        String totalDurationString = String.format("%h h, %d min, %d sec", hours, minutes, seconds);

        Paginator p = pbuilder
                .setText(String.format("Total queue length: %s.\n" +
                        "Use the buttons down below to switch pages.", totalDurationString))
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
