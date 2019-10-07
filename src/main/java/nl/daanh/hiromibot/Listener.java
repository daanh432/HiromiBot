package nl.daanh.hiromibot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

class Listener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private final CommandHandler commandHandler;

    Listener(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        LOGGER.info("{} is ready", event.getJDA().getSelfUser().getAsTag());
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        String rawMessage = event.getMessage().getContentRaw().toLowerCase();

        if (event.getAuthor().getIdLong() == Constants.OWNER && rawMessage.equalsIgnoreCase(Constants.PREFIX + "shutdown")) {
            shutdown(event.getJDA());
            return;
        }

        GuildSettings guildSettings = new GuildSettings(event.getGuild());
        String prefix = guildSettings.getPrefix();

        if (event.getAuthor().isBot() || event.getMessage().isWebhookMessage() || !(rawMessage.startsWith(Constants.PREFIX) || rawMessage.startsWith(prefix)))
            return;

        commandHandler.HandleCommand(event, guildSettings);
    }

    private void shutdown(JDA jda) {
        jda.shutdown();
    }
}
