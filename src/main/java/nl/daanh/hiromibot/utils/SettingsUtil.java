package nl.daanh.hiromibot.utils;

import kong.unirest.HttpRequestWithBody;
import kong.unirest.UnirestException;
import kong.unirest.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;

public class SettingsUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsUtil.class);
    private static final HashMap<String, String> defaultSettings = new HashMap<>();
    private static final HashMap<Long, JSONObject> settingsCache = new HashMap<>();

    public SettingsUtil() {
        defaultSettings.put("prefix", "hi!");
        defaultSettings.put("musicEnabled", "false");
    }

    private static JSONObject fetchSettings(Long guildId) {
        JSONObject jsonObject = WebUtils.fetchJsonFromUrlApi(String.format("http://hiromiapi.test/api/v1/settings/%s/guilds", guildId));
        jsonObject.put("local_expires_at", Instant.now().getEpochSecond());
        settingsCache.put(guildId, jsonObject);
        return jsonObject;
    }

    private static void writeKey(Long guildId, String key, String value) {
        try {
            HttpRequestWithBody request = WebUtils.postToUrlApi(String.format("http://hiromiapi.test/api/v1/settings/%s/guilds", guildId));
            request.field("id", guildId.toString());
            request.field("setting", key);
            request.field("value", value);
            request.asEmpty();
        } catch (UnirestException exception) {
            LOGGER.error("An error occurred trying to update a setting for a guild.");
        }
    }

    private static String getKey(Long guildId, String key) {
        // Check if guild settings are cached and not expired if not then it returns the value straight from the cache
        if (settingsCache.containsKey(guildId) && settingsCache.get(guildId).has("local_expires_at")) {
            if (settingsCache.get(guildId).getLong("local_expires_at") > Instant.now().getEpochSecond()) {
                if (settingsCache.get(guildId).has(key)) {
                    return settingsCache.get(guildId).getString(key);
                }
            }
        }

        // Fetch settings from online api
        JSONObject jsonObject = fetchSettings(guildId);

        // If online api contains correct setting return it
        if (jsonObject.has(key)) {
            return jsonObject.getString(key);
        }

        // If value has not been found on the online api or in the cache return the default value
        String defaultSetting = defaultSettings.getOrDefault(key, "NO_DEFAULT_VALUE");
        writeKey(guildId, key, defaultSetting); // Make sure it gets stored in the cache from now on
        return defaultSetting;
    }

    public static String getPrefix(Long guildId) {
        return getKey(guildId, "prefix");
    }

    public static void setPrefix(Long guildId, String prefix) {
        writeKey(guildId, "prefix", prefix);
    }

    public static Boolean getMusicEnabled(Long guildId) {
        String musicEnabled = getKey(guildId, "musicEnabled").toLowerCase();
        return musicEnabled.equals("on") || musicEnabled.equals("true") || musicEnabled.equals("enabled") || musicEnabled.equals("1") || musicEnabled.equals("enable");
    }

    public static void setMusicEnabled(Long guildId, Boolean musicEnabled) {
        writeKey(guildId, "musicEnabled", musicEnabled ? "true" : "false");
    }
}