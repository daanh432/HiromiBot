package nl.daanh.hiromibot.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import nl.daanh.hiromibot.Config;
import nl.daanh.hiromibot.objects.CommandInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLDataSource implements DatabaseManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(MySQLDataSource.class);
    private final HikariDataSource ds;

    public MySQLDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/hiromi_mysql?serverTimezone=UTC");
        config.setUsername("root");
        config.setPassword("");
        config.setConnectionTestQuery("SELECT 1");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setPoolName("MysqlDataPool");
        config.setMaximumPoolSize(10);
        this.ds = new HikariDataSource(config);

        try (final Connection connection = this.getConnection()) {
            final Statement statement = connection.createStatement();
            // language=MySQL
            statement.execute("CREATE TABLE IF NOT EXISTS guild_settings (" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                    "guild_id VARCHAR(20) NOT NULL," +
                    "setting VARCHAR(20) NOT NULL," +
                    "value VARCHAR(255) NOT NULL" +
                    ");");

            LOGGER.info("Table initialised");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return this.ds.getConnection();
    }

    private boolean settingExists(long guildId, String setting) {
        try (final Connection connection = this.getConnection()) {
            // language=MySQL
            PreparedStatement statement = connection.prepareStatement("SELECT value FROM guild_settings WHERE guild_id = ? AND setting = ?");
            statement.setString(1, String.valueOf(guildId));
            statement.setString(2, setting);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
        return false;
    }

    @Override
    public String getPrefix(long guildId) {
        try (final Connection connection = this.getConnection()) {
            // language=MySQL
            PreparedStatement statement = connection.prepareStatement("SELECT value FROM guild_settings WHERE guild_id = ? AND setting = ?");
            statement.setString(1, String.valueOf(guildId));
            statement.setString(2, "prefix");

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("value");
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }

        return Config.getInstance().getString("prefix");
    }

    @Override
    public void setPrefix(long guildId, String newPrefix) {
        try (final Connection connection = this.getConnection()) {
            if (this.settingExists(guildId, "prefix")) {
                // language=MySQL
                PreparedStatement statement = connection.prepareStatement("UPDATE guild_settings SET value = ? WHERE guild_id = ? AND setting = 'prefix'");
                statement.setString(1, newPrefix);
                statement.setString(2, String.valueOf(guildId));
                statement.executeUpdate();
            } else {
                // language=MySQL
                PreparedStatement statement = connection.prepareStatement("INSERT INTO guild_settings (guild_id, setting, value) VALUES (?, 'prefix', ?)");
                statement.setString(1, String.valueOf(guildId));
                statement.setString(2, newPrefix);
                statement.execute();
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public boolean getMusicEnabled(long guildId) {
        try (final Connection connection = this.getConnection()) {
            // language=MySQL
            PreparedStatement statement = connection.prepareStatement("SELECT value FROM guild_settings WHERE guild_id = ? AND setting = 'musicEnabled'");
            statement.setString(1, String.valueOf(guildId));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("value").equalsIgnoreCase("true");
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }

        return true;
    }

    @Override
    public void setMusicEnabled(long guildId, boolean enabled) {
        try (final Connection connection = this.getConnection()) {
            if (this.settingExists(guildId, "musicEnabled")) {
                // language=MySQL
                PreparedStatement statement = connection.prepareStatement("UPDATE guild_settings SET value = ? WHERE guild_id = ? AND setting = 'musicEnabled'");
                statement.setString(1, enabled ? "true" : "false");
                statement.setString(2, String.valueOf(guildId));
                statement.executeUpdate();
            } else {
                // language=MySQL
                PreparedStatement statement = connection.prepareStatement("INSERT INTO guild_settings (guild_id, setting, value) VALUES (?, 'musicEnabled', ?)");
                statement.setString(1, String.valueOf(guildId));
                statement.setString(2, enabled ? "true" : "false");
                statement.execute();
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public boolean getFunEnabled(long guildId) {
        try (final Connection connection = this.getConnection()) {
            // language=MySQL
            PreparedStatement statement = connection.prepareStatement("SELECT value FROM guild_settings WHERE guild_id = ? AND setting = 'funEnabled'");
            statement.setString(1, String.valueOf(guildId));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("value").equalsIgnoreCase("true");
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }

        return true;
    }

    @Override
    public void setFunEnabled(long guildId, boolean enabled) {
        try (final Connection connection = this.getConnection()) {
            if (this.settingExists(guildId, "funEnabled")) {
                // language=MySQL
                PreparedStatement statement = connection.prepareStatement("UPDATE guild_settings SET value = ? WHERE guild_id = ? AND setting = 'funEnabled'");
                statement.setString(1, enabled ? "true" : "false");
                statement.setString(2, String.valueOf(guildId));
                statement.executeUpdate();
            } else {
                // language=MySQL
                PreparedStatement statement = connection.prepareStatement("INSERT INTO guild_settings (guild_id, setting, value) VALUES (?, 'funEnabled', ?)");
                statement.setString(1, String.valueOf(guildId));
                statement.setString(2, enabled ? "true" : "false");
                statement.execute();
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public boolean getModerationEnabled(long guildId) {
        try (final Connection connection = this.getConnection()) {
            // language=MySQL
            PreparedStatement statement = connection.prepareStatement("SELECT value FROM guild_settings WHERE guild_id = ? AND setting = 'moderationEnabled'");
            statement.setString(1, String.valueOf(guildId));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("value").equalsIgnoreCase("true");
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }

        return true;
    }

    @Override
    public void setModerationEnabled(long guildId, boolean enabled) {
        try (final Connection connection = this.getConnection()) {
            if (this.settingExists(guildId, "moderationEnabled")) {
                // language=MySQL
                PreparedStatement statement = connection.prepareStatement("UPDATE guild_settings SET value = ? WHERE guild_id = ? AND setting = 'moderationEnabled'");
                statement.setString(1, enabled ? "true" : "false");
                statement.setString(2, String.valueOf(guildId));
                statement.executeUpdate();
            } else {
                // language=MySQL
                PreparedStatement statement = connection.prepareStatement("INSERT INTO guild_settings (guild_id, setting, value) VALUES (?, 'moderationEnabled', ?)");
                statement.setString(1, String.valueOf(guildId));
                statement.setString(2, enabled ? "true" : "false");
                statement.execute();
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public List<CommandInterface.CATEGORY> getEnabledCategories(long guildId) {
        List<CommandInterface.CATEGORY> list = new ArrayList<>();
        list.add(CommandInterface.CATEGORY.OTHER);

        if (this.getMusicEnabled(guildId)) list.add(CommandInterface.CATEGORY.MUSIC);
        if (this.getFunEnabled(guildId)) list.add(CommandInterface.CATEGORY.FUN);
        if (this.getModerationEnabled(guildId)) list.add(CommandInterface.CATEGORY.MODERATION);

        return list;
    }

    @Nullable
    @Override
    public Long getCreateVoiceChannelId(long guildId) {
        try (final Connection connection = this.getConnection()) {
            // language=MySQL
            PreparedStatement statement = connection.prepareStatement("SELECT value FROM guild_settings WHERE guild_id = ? AND setting = 'createVoiceChannel'");
            statement.setString(1, String.valueOf(guildId));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Long.parseLong(resultSet.getString("value"));
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        } catch (NumberFormatException e) {
            // Ignore user error
        }

        return null;
    }

    @Override
    public void setCreateVoiceChannelId(long guildId, long voiceChannelId) {
        try (final Connection connection = this.getConnection()) {
            if (this.settingExists(guildId, "prefix")) {
                // language=MySQL
                PreparedStatement statement = connection.prepareStatement("UPDATE guild_settings SET value = ? WHERE guild_id = ? AND setting = 'createVoiceChannel'");
                statement.setString(1, String.valueOf(voiceChannelId));
                statement.setString(2, String.valueOf(guildId));
                statement.executeUpdate();
            } else {
                // language=MySQL
                PreparedStatement statement = connection.prepareStatement("INSERT INTO guild_settings (guild_id, setting, value) VALUES (?, 'createVoiceChannel', ?)");
                statement.setString(1, String.valueOf(guildId));
                statement.setString(2, String.valueOf(voiceChannelId));
                statement.execute();
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
