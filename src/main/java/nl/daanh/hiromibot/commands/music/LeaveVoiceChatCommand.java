package nl.daanh.hiromibot.commands.music;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import nl.daanh.hiromibot.Constants;
import nl.daanh.hiromibot.objects.CommandInterface;

import java.util.List;

public class LeaveVoiceChatCommand implements CommandInterface {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        TextChannel textChannel = event.getChannel();
        AudioManager audioManager = event.getGuild().getAudioManager();
        Member member = event.getMember();

        if (!audioManager.isConnected()) {
            textChannel.sendMessage("I'm not connected to a voice channel.").queue();
            return;
        }

        VoiceChannel voiceChannel = audioManager.getConnectedChannel();
        if (!voiceChannel.getMembers().contains(member)) {
            textChannel.sendMessage("You have to be in the voice channel to use this command.").queue();
            return;
        }

        String voiceChannelName = voiceChannel.getName();
        audioManager.closeAudioConnection();
        textChannel.sendMessageFormat("Leaving the channel `%s`.", voiceChannelName).queue();
    }

    @Override
    public String getHelp() {
        return "Makes the bot leave the voice channel";
    }

    @Override
    public String getUsage() {
        return Constants.PREFIX + getInvoke();
    }

    @Override
    public String getInvoke() {
        return "leave";
    }
}
