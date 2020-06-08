package nl.daanh.hiromibot.database;

import nl.daanh.hiromibot.objects.CommandInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;

public class MySQLDataSource implements DatabaseManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(MySQLDataSource.class);
//    private final HikariDataSource ds;


    @Override
    public String getPrefix(long guildId) {
        return null;
    }

    @Override
    public void setPrefix(long guildId, String newPrefix) {

    }

    @Override
    public boolean getMusicEnabled(long guildId) {
        return false;
    }

    @Override
    public void setMusicEnabled(long guildId, boolean enabled) {

    }

    @Override
    public boolean getFunEnabled(long guildId) {
        return false;
    }

    @Override
    public void setFunEnabled(long guildId, boolean enabled) {

    }

    @Override
    public boolean getModerationEnabled(long guildId) {
        return false;
    }

    @Override
    public void setModerationEnabled(long guildId, boolean enabled) {

    }

    @Override
    public List<CommandInterface.CATEGORY> getEnabledCategories(long guildId) {
        return null;
    }

    @Nullable
    @Override
    public Long getCreateVoiceChannelId(long guildId) {
        return null;
    }

    @Override
    public void setCreateVoiceChannelId(long guildId, long voiceChannelId) {

    }
}
