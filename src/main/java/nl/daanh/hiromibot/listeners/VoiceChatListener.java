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

package nl.daanh.hiromibot.listeners;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import nl.daanh.hiromibot.database.DatabaseManager;
import nl.daanh.hiromibot.ratelimiting.RateLimitObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.*;

public class VoiceChatListener extends ListenerAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(VoiceChatListener.class);
    private static VoiceChatListener INSTANCE;
    private final VoiceChatGarbageCollection voiceChatGarbageCollection;
    private final List<VoiceChannel> managedChannels;

    private VoiceChatListener() {
        this.voiceChatGarbageCollection = new VoiceChatGarbageCollection();
        this.managedChannels = new ArrayList<>();
    }

    public static VoiceChatListener getInstance() {
        if (VoiceChatListener.INSTANCE == null) {
            VoiceChatListener.INSTANCE = new VoiceChatListener();
        }
        return VoiceChatListener.INSTANCE;
    }

    @Override
    public void onGuildVoiceJoin(@Nonnull GuildVoiceJoinEvent event) {
        Guild guild = event.getGuild();
        Member member = event.getMember();
        VoiceChannel channel = event.getChannelJoined();
        this.checkForCreate(guild, member, channel);
    }

    @Override
    public void onGuildVoiceMove(@Nonnull GuildVoiceMoveEvent event) {
        final Guild guild = event.getGuild();
        final Member member = event.getMember();
        final VoiceChannel joinedChannel = event.getChannelJoined();
        final VoiceChannel leftChannel = event.getChannelLeft();
        this.checkForCreate(guild, member, joinedChannel);
        this.checkForDelete(guild, leftChannel);
    }

    @Override
    public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
        final Guild guild = event.getGuild();
        final VoiceChannel channel = event.getChannelLeft();
        this.checkForDelete(guild, channel);
    }

    private void checkForDelete(@Nonnull Guild guild, @Nonnull VoiceChannel channel) {
        if (managedChannels.contains(channel)) {
            if (channel.getMembers().size() == 0 && guild.getSelfMember().hasPermission(channel, Permission.MANAGE_CHANNEL)) {
                channel.delete().queue();
                managedChannels.remove(channel);
                LOGGER.debug("Deleting channel");
            }
        }
    }

    private void checkForCreate(@Nonnull Guild guild, @Nonnull Member member, @Nonnull VoiceChannel channel) {
        Long createVoiceChannelId = DatabaseManager.instance.getCreateVoiceChannelId(guild.getIdLong());
        if (createVoiceChannelId != null) {
            if (createVoiceChannelId.equals(channel.getIdLong()) && !member.getUser().isFake() && !member.getUser().isBot()) {
                String channelName = String.format("%s's Channel", member.getEffectiveName());
                if (this.voiceChatGarbageCollection.canRun(guild)) {
                    if (channel.getParent() != null && guild.getSelfMember().hasPermission(channel.getParent(), Permission.MANAGE_CHANNEL)) {
                        channel.getParent().createVoiceChannel(channelName).queue(newChannel -> moveUserToChannel(guild, member, newChannel));
                    } else if (guild.getSelfMember().hasPermission(Permission.MANAGE_CHANNEL)) {
                        guild.createVoiceChannel(channelName).queue(newChannel -> moveUserToChannel(guild, member, newChannel));
                    }
                }
            }
        }
    }

    private void moveUserToChannel(@Nonnull Guild guild, @Nonnull Member member, @Nonnull VoiceChannel newChannel) {
        LOGGER.debug("Created new voice channel");
        managedChannels.add(newChannel);
        if (guild.getSelfMember().hasPermission(Permission.VOICE_MOVE_OTHERS)) {
            guild.moveVoiceMember(member, newChannel).queue();
            LOGGER.debug("Moving member to voice channel");
        }
    }
}

class VoiceChatGarbageCollection {
    private static final Logger LOGGER = LoggerFactory.getLogger(VoiceChatGarbageCollection.class);
    private static final int timeBetweenVoiceChannelCreations = 15;
    private final HashMap<Long, RateLimitObject> rateLimitMap;

    public VoiceChatGarbageCollection() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                LOGGER.debug("Running voice chat listener garbage collection");
                Iterator<Long> iter = rateLimitMap.keySet().iterator();
                while (iter.hasNext()) {
                    Long guildId = iter.next();
                    RateLimitObject rateLimitObject = rateLimitMap.get(guildId);

                    if (Instant.now().getEpochSecond() > rateLimitObject.getTime() + VoiceChatGarbageCollection.timeBetweenVoiceChannelCreations)
                        iter.remove();
                }
            }
        }, 5000, 120000); // Runs every 2 minutes with a delay of 5 seconds
        this.rateLimitMap = new HashMap<>();
    }

    private long currentTime() {
        return Instant.now().getEpochSecond();
    }

    public boolean canRun(@Nonnull Guild guild) {
        long currentTime = this.currentTime();
        RateLimitObject time = rateLimitMap.getOrDefault(guild.getIdLong(), new RateLimitObject(0L, 0));

        if (currentTime > time.getTime() + VoiceChatGarbageCollection.timeBetweenVoiceChannelCreations) {
            rateLimitMap.remove(guild.getIdLong());
            this.addRateLimit(guild);
            return true;
        } else {
            this.addRateLimit(guild);
            return false;
        }
    }

    private void addRateLimit(@Nonnull Guild guild) {
        if (!this.rateLimitMap.containsKey(guild.getIdLong())) {
            RateLimitObject rateLimitObject = new RateLimitObject(this.currentTime(), 1);
            this.rateLimitMap.put(guild.getIdLong(), rateLimitObject);
        }

        RateLimitObject rateLimitObject = this.rateLimitMap.get(guild.getIdLong());
        rateLimitObject.times++;
        LOGGER.debug(String.format("Rate limit count %s times", rateLimitObject.times));
        this.rateLimitMap.put(guild.getIdLong(), rateLimitObject);
    }
}