package nl.daanh.hiromibot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import nl.daanh.hiromibot.utils.SettingsUtil;
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

        if (event.getAuthor().getIdLong() == Config.getInstance().getLong("owner") && rawMessage.equalsIgnoreCase(Config.getInstance().getString("prefix") + "shutdown")) {
            shutdown(event.getJDA());
            return;
        }

        // Check sender
        if (event.getAuthor().isBot() || event.getMessage().isWebhookMessage())
            return;

        // Check prefix
        String prefix = rawMessage.startsWith(Config.getInstance().getString("prefix")) ? Config.getInstance().getString("prefix") : SettingsUtil.getPrefix(event.getGuild().getIdLong());
        if (!(rawMessage.startsWith(Config.getInstance().getString("prefix")) || rawMessage.startsWith(prefix)))
            return;

        // Check if bot has required permissions to be able to execute in this channel
        if (!event.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_WRITE))
            return;


        commandHandler.HandleCommand(event, prefix);
    }

    private void shutdown(JDA jda) {
        jda.shutdown();
    }
}
