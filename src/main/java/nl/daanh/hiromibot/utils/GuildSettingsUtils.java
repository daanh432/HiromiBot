package nl.daanh.hiromibot.utils;

import net.dv8tion.jda.api.entities.Guild;
import nl.daanh.hiromibot.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public class GuildSettingsUtils {
    private Logger LOGGER = LoggerFactory.getLogger(GuildSettingsUtils.class);
    private File configFile;
    private String guildId;
    private String guildName;
    private Properties props;

    public GuildSettingsUtils(Guild guild) {
        this.guildId = guild.getId();
        this.guildName = guild.getName();
        props = new Properties();
        String fileName = "./guildConfigs/" + guildId + "/config.properties";
        configFile = new File(fileName);
        if (!checkIfConfigExists()) {
            createDefaultConfig();
        } else {
            FileInputStream inStream;
            try {
                inStream = new FileInputStream(fileName);
                props.load(inStream);
                inStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkIfConfigExists() {
        return configFile.exists();
    }

    private void createDefaultConfig() {
        if (configFile.getParentFile().mkdirs())
            LOGGER.info("Parent file config directories created.");

        try {
            props.setProperty("guildId", guildId);
            props.setProperty("guildName", guildName);
            props.setProperty("prefix", Constants.PREFIX);

            FileWriter writer = new FileWriter(configFile);
            props.store(writer, "Guild: " + guildId);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateConfigs() {

        if (configFile.getParentFile().mkdirs())
            LOGGER.info("Parent file config directories created.");

        try {
            if (!props.containsKey("guildId")) {
                props.setProperty("guildId", guildId);
            }

            if (!props.containsKey("guildName")) {
                props.setProperty("guildName", guildName);
            }

            if (!props.containsKey("prefix")) {
                props.setProperty("prefix", Constants.PREFIX);
            }

            FileWriter writer = new FileWriter(configFile);
            props.store(writer, "Guild: " + guildId);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeKey(String key, String value) {
        try {
            props.setProperty(key, value);
            FileWriter writer = new FileWriter(configFile);
            props.store(writer, "Guild: " + guildId);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getKey(String key) {
        try {
            FileReader reader = new FileReader(configFile);
            props = new Properties();
            props.load(reader);
            reader.close();
            return props.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getPrefix() {
        return getKey("prefix");
    }

    public void setPrefix(String prefix) {
        writeKey("prefix", prefix);
    }

}