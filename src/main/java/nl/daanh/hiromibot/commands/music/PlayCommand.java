package nl.daanh.hiromibot.commands.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromibot.objects.CommandContext;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.EmbedUtils;
import nl.daanh.hiromibot.utils.LavalinkUtils;
import nl.daanh.hiromibot.utils.RandomUtils;
import nl.daanh.hiromibot.utils.music.PlayerManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class PlayCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        List<String> args = ctx.getArgs();
        Member member = ctx.getMember();
        Member selfMember = ctx.getSelfMember();
        String input = String.join(" ", args);

        if (selfMember.getVoiceState() != null && !selfMember.getVoiceState().inVoiceChannel()) {
            if (member.getVoiceState() != null && member.getVoiceState().inVoiceChannel() && member.getVoiceState().getChannel() != null && selfMember.hasPermission(member.getVoiceState().getChannel(), Permission.VOICE_CONNECT)) {
                LavalinkUtils.openConnection(member.getVoiceState().getChannel());
            } else {
                channel.sendMessage(EmbedUtils.defaultMusicEmbed("I'd love to join you. But I can't find you! *sobs*", false).build()).queue();
                return;
            }
        }

        if (!RandomUtils.inSameVoiceChannel(member, selfMember)
                && selfMember.getVoiceState() != null
                && selfMember.getVoiceState().inVoiceChannel()) {
            channel.sendMessage(EmbedUtils.defaultMusicEmbed("You have to be in the voice channel to add songs.", false).build()).queue();
            return;
        }

        if (args.isEmpty()) {
            channel.sendMessage(EmbedUtils.defaultMusicEmbed(String.format("Please specify a url or song title you want me to play. %s", this.getUsage()), false).build()).queue();
            return;
        }

        if (!isUrl(input)) {
            String ytSearched = searchYouTube(input);

            if (ytSearched == null) {
                EmbedBuilder embedBuilder = EmbedUtils.defaultMusicEmbed(String.format("Please specify a valid YouTube or SoundCloud link. %s", this.getUsage()), false);
                channel.sendMessage(embedBuilder.build()).queue();
                return;
            }

            input = ytSearched;
        }

        PlayerManager manager = PlayerManager.getInstance();
        manager.loadAndPlay(channel, input);
    }

    private boolean isUrl(String input) {
        try {
            new URL(input);
            return true;
        } catch (MalformedURLException exception) {
            return false;
        }
    }

    private String searchYouTube(String input) {
        // TODO Implemment YouTube API
        return null;
    }

    @Override
    public String getHelp() {
        return "Plays a song from a youtube video or playlist. \n" + this.getUsage();
    }

    public String getUsage() {
        return "Usage: ``play [video url|playlist url]``";
    }

    @Override
    public CATEGORY getCategory() {
        return CATEGORY.MUSIC;
    }

    @Override
    public String getInvoke() {
        return "play";
    }
}
