package nl.daanh.hiromibot.commands.music;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import nl.daanh.hiromibot.Constants;
import nl.daanh.hiromibot.objects.CommandInterface;

import java.util.List;

public class JoinVoiceChatCommand implements CommandInterface {
    @Override
    public void handle(List<String> args, GuildMessageReceivedEvent event) {
        TextChannel textChannel = event.getChannel();
        AudioManager audioManager = event.getGuild().getAudioManager();
        Member member = event.getMember();
        Member selfMember = event.getGuild().getSelfMember();

        if (audioManager.isConnected()) {
            if (audioManager.getConnectedChannel() != null) {
                textChannel.sendMessage(String.format("I'm already connected to %s.", audioManager.getConnectedChannel().getName())).queue();
                return;
            }

            textChannel.sendMessage("I'm already connected to a channel.").queue();
            return;
        }

        if (member != null) {
            GuildVoiceState memberVoiceState = member.getVoiceState();
            if (memberVoiceState != null) {
                if (!memberVoiceState.inVoiceChannel() || memberVoiceState.getChannel() == null) {
                    textChannel.sendMessage("You have to be connected to a voice channel first.").queue();
                    return;
                }

                VoiceChannel voiceChannel = memberVoiceState.getChannel();

                if (selfMember.hasPermission(voiceChannel, Permission.VOICE_CONNECT)) {
                    textChannel.sendMessageFormat("I'm missing the permission voice connect to join %s", voiceChannel).queue();
                    return;
                }

                audioManager.openAudioConnection(voiceChannel);
                textChannel.sendMessageFormat("Joining the voice channel %s", voiceChannel).queue();
                return;
            }
        }

        textChannel.sendMessage("An error occurred trying to execute that command. Please try again later.").queue();
    }

    @Override
    public String getHelp() {
        return "Makes the bot join a voice channel";
    }

    @Override
    public String getUsage() {
        return Constants.PREFIX + getInvoke();
    }

    @Override
    public String getInvoke() {
        return "join";
    }
}
