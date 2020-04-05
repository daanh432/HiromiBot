package nl.daanh.hiromibot.utils;

import lavalink.client.io.Link;
import lavalink.client.io.jda.JdaLavalink;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import nl.daanh.hiromibot.Bot;

import javax.annotation.Nonnull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

import static nl.daanh.hiromibot.Bot.SHARD_COUNT;

public class LavalinkUtils {
    private static String token;
    private static JdaLavalink lavalink;

    public LavalinkUtils(String token) {
        LavalinkUtils.token = token;
        LavalinkUtils.init();
    }

    private static void init() {
        if (!isEnabled()) return;
        try {
            JdaLavalink lavalink = new JdaLavalink(
                    LavalinkUtils.getIdFromToken(LavalinkUtils.token),
                    SHARD_COUNT,
                    shardId -> Bot.getInstance().getShardManager().getShardById(shardId)
            );
            lavalink.addNode(new URI("ws://node1.daanhendriks.nl:2333"), "NmkMsMfdun2NU34wQrjEYMtavTwPdTeGKZHt9EAyEyYZrkjchjs6rfZX8BkYF7ULwGD9xGzQG3bQSVaBYWpdX4nbxpPdJR3qQUdwx3fMQ4yfK6fYZ4m6ppv7Z85mNPLzPkBaMxyer5UPUdhLnMk9X2C7PAcQxFp7qd6anc6W4jCyCmHkcdkJstdddJmQRcVdJFRqRkXd6Y4xrfwxUGMUBYgBhZPHznDxYKqCQpRLbcRCZXBk5DkxqpJt9Gt5uj7T");
            LavalinkUtils.lavalink = lavalink;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static JdaLavalink getLavalink() {
        return lavalink;
    }

    public static boolean isEnabled() {
        return true;
    }

//    public IPlayer createPlayer(long guildId) {
//        return isEnabled()
//                ? lavalink.getLink(String.valueOf(guildId)).getPlayer()
//                : new LavaplayerPlayerWrapper(audioUtils.getPlayerManager().createPlayer());
//    }

    public static void openConnection(VoiceChannel channel) {
        final AudioManager audioManager = channel.getGuild().getAudioManager();

        // Turn on the deafen icon for the bot
        audioManager.setSelfDeafened(true);

        if (isEnabled()) {
            lavalink.getLink(channel.getGuild()).connect(channel);
        } else {
            audioManager.openAudioConnection(channel);
        }
    }

    public static void closeConnection(Guild guild) {
        if (isEnabled()) {
            lavalink.getLink(guild).disconnect();
        } else {
            guild.getAudioManager().closeAudioConnection();
        }
    }

    public static boolean isConnected(Guild g) {
        return isEnabled() ?
                lavalink.getLink(g).getState() == Link.State.CONNECTED :
                g.getAudioManager().isConnected();
    }

    public static VoiceChannel getConnectedChannel(@Nonnull Guild guild) {
        // NOTE: never use the local audio manager, since the audio connection may be remote
        // there is also no reason to look the channel up remotely from lavalink, if we have access to a real guild
        // object here, since we can use the voice state of ourselves (and lavalink 1.x is buggy in keeping up with the
        // current voice channel if the bot is moved around in the client)
        if (guild.getSelfMember().getVoiceState() != null) {
            return guild.getSelfMember().getVoiceState().getChannel();
        }
        return null;
    }

    private static String getIdFromToken(String token) {
        return new String(
                Base64.getDecoder().decode(
                        token.split("\\.")[0]
                )
        );
    }
}
