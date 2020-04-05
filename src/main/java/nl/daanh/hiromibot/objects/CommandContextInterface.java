package nl.daanh.hiromibot.objects;

import lavalink.client.io.jda.JdaLink;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.sharding.ShardManager;
import nl.daanh.hiromibot.utils.LavalinkUtils;

public interface CommandContextInterface {

    Guild getGuild();

    GuildMessageReceivedEvent getEvent();

    default TextChannel getChannel() {
        return this.getEvent().getChannel();
    }

    default Message getMessage() {
        return this.getEvent().getMessage();
    }

    default Member getMember() {
        return this.getEvent().getMember();
    }

    default JDA getJDA() {
        return this.getEvent().getJDA();
    }

    default User getSelfUser() {
        return this.getJDA().getSelfUser();
    }

    default Member getSelfMember() {
        return this.getGuild().getSelfMember();
    }

    default ShardManager getShardManager() {
        return this.getJDA().getShardManager();
    }

    default JdaLink getLavalink() {
        return LavalinkUtils.getLavalink().getLink(this.getGuild());
    }
}
