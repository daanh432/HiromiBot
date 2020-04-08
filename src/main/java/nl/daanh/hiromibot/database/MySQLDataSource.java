package nl.daanh.hiromibot.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySQLDataSource implements DatabaseManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteDataSource.class);
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
}
