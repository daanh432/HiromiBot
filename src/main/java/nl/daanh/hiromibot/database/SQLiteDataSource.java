package nl.daanh.hiromibot.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import nl.daanh.hiromibot.Config;
import nl.daanh.hiromibot.objects.CommandInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class SQLiteDataSource implements DatabaseManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteDataSource.class);
    private final HikariDataSource ds;

    public SQLiteDataSource() {
        try {
            final File dbFile = new File("database.db");

            if (!dbFile.exists()) {
                if (dbFile.createNewFile()) {
                    LOGGER.info("Created a new database file");
                } else {
                    LOGGER.info("Could not create database file");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:database.db");
        config.setConnectionTestQuery("SELECT 1");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);

        try (final Statement statement = getConnection().createStatement()) {
            final String defaultPrefix = Config.getInstance().getString("prefix");

            // language=SQLite
            statement.execute("CREATE TABLE IF NOT EXISTS guild_settings (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "guild_id VARCHAR(20) NOT NULL," +
                    "prefix VARCHAR(255) NOT NULL DEFAULT '" + defaultPrefix + "'" +
                    ");");

            LOGGER.info("Table initialised");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    @Override
    public String getPrefix(long guildId) {
        try {
            // language=SQLite
            PreparedStatement statement = this.getConnection().prepareStatement("SELECT prefix FROM guild_settings WHERE guild_id = ?");
            statement.setString(1, String.valueOf(guildId));

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Config.getInstance().getString("prefix");
    }

    @Override
    public void setPrefix(long guildId, String newPrefix) {
        try {
            // language=SQLite
            PreparedStatement statement = this.getConnection().prepareStatement("UPDATE guild_settings SET prefix = ? WHERE guild_id = ?");
            statement.setString(1, newPrefix);
            statement.setString(2, String.valueOf(guildId));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
