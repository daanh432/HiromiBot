package nl.daanh.hiromibot.database;

public interface DatabaseManager {
    DatabaseManager instance = null;

    String getPrefix(long guildId);

    void setPrefix(long guildId, String newPrefix);

    boolean getMusicEnabled(long guildId);

    void setMusicEnabled(long guildId, boolean enabled);

    boolean getFunEnabled(long guildId);

    void setFunEnabled(long guildId, boolean enabled);

}
