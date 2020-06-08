package nl.daanh.hiromibot.database;

import nl.daanh.hiromibot.objects.CommandInterface;

import javax.annotation.Nullable;
import java.util.List;

public interface DatabaseManager {
    DatabaseManager instance = new MySQLDataSource();

    String getPrefix(long guildId);

    void setPrefix(long guildId, String newPrefix);

    boolean getMusicEnabled(long guildId);

    void setMusicEnabled(long guildId, boolean enabled);

    boolean getFunEnabled(long guildId);

    void setFunEnabled(long guildId, boolean enabled);

    boolean getModerationEnabled(long guildId);

    void setModerationEnabled(long guildId, boolean enabled);

    List<CommandInterface.CATEGORY> getEnabledCategories(long guildId);

    @Nullable
    Long getCreateVoiceChannelId(long guildId);

    void setCreateVoiceChannelId(long guildId, long voiceChannelId);
}
