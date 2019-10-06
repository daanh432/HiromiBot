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
        if (event.getAuthor().getIdLong() == Constants.OWNER && event.getMessage().getContentRaw().equalsIgnoreCase(Constants.PREFIX + "shutdown")) {
            shutdown(event.getJDA());
            return;
        }

        if (event.getAuthor().isBot() || event.getMessage().isWebhookMessage() || !event.getMessage().getContentRaw().toLowerCase().startsWith(Constants.PREFIX))
            return;

        commandHandler.HandleCommand(event);
    }

    private void shutdown(JDA jda) {
        jda.shutdown();
    }
}
