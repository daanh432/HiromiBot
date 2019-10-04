package nl.daanh.hiromibot;

import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class Bot {

    private Bot() throws LoginException {
        new JDABuilder()
                .setToken(Secrets.TOKEN)
                .addEventListeners(new Listener(new CommandHandler()))
                .build();
    }

    public static void main(String[] args) throws LoginException {
        new Bot();
    }
}
