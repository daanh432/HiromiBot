package nl.daanh.hiromibot.utils;

import lavalink.client.io.Link;
import lavalink.client.io.jda.JdaLavalink;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import nl.daanh.hiromibot.Bot;
import nl.daanh.hiromibot.Config;
import nl.daanh.hiromibot.utils.music.PlayerManager;
import org.json.JSONArray;
import org.json.JSONObject;

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
        try {
            JdaLavalink lavalink = new JdaLavalink(
                    LavalinkUtils.getIdFromToken(LavalinkUtils.token),
                    SHARD_COUNT,
                    shardId -> Bot.getInstance().getShardManager().getShardById(shardId)
            );

            JSONArray lavalinkNodes = Config.getInstance().getJSONArray("lavalinkNodes");

            for (int i = 0; i < lavalinkNodes.length(); i++) {
                JSONObject lavalinkNode = lavalinkNodes.getJSONObject(i);
                lavalink.addNode(new URI(lavalinkNode.getString("url")), lavalinkNode.getString("password"));
            }

            LavalinkUtils.lavalink = lavalink;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static JdaLavalink getLavalink() {
        return lavalink;
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

        lavalink.getLink(channel.getGuild()).connect(channel);
    }

    public static void closeConnection(Guild guild) {
        PlayerManager.getInstance().purge(guild);
        lavalink.getLink(guild).disconnect();
        lavalink.getLink(guild).destroy();
    }

    public static boolean isConnected(Guild g) {
        return lavalink.getLink(g).getState() == Link.State.CONNECTED;
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
