package nl.daanh.hiromibot;

import nl.daanh.hiromibot.objects.DiscordBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class Bot {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);

    private static HashMap<String, String> arguments = new HashMap<>();

    public static void main(String[] args) {
        LOGGER.info("Parsing arguments");
        parseArguments(args);
        LOGGER.info("Parsed arguments");
        DiscordBot discordBot = new DiscordBot(arguments.getOrDefault("token", "NULL"));
    }

    private static void parseArguments(String[] args) {
        for (String arg : args) {
            String[] parsedArg = arg.split(":");
            if (parsedArg.length == 2) {
                switch (parsedArg[0].toLowerCase()) {
                    case "token":
                    case "ipaddress":
                    case "username":
                    case "password":
                    case "database":
                        arguments.put(parsedArg[0].toLowerCase(), parsedArg[1]);
                        break;
                    default:
                        System.out.println(String.format("Unknown argument %s", parsedArg[0]));
                }
            }
        }
    }
}
