package nl.daanh.hiromibot.utils;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.MultipartBody;
import kong.unirest.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;

public class SettingsUtil {
    private static final Integer EXPIRES_IN = 60; // 60 seconds
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsUtil.class);
    private static final HashMap<Long, JSONObject> settingsCache = new HashMap<>();

    private static String getGuildUrl(Long guildId) {
        return String.format("http://hiromiapi.test/api/v1/settings/%s/guilds", guildId.toString());
    }

    private static String getDefaultSetting(String key) {
        // If value has not been found on the online api or in the cache return the default value
        switch (key) {
            case "prefix":
                return "hi!";
            case "musicEnabled":
                return "false";
            default:
                return String.format("NO_DEFAULT_VALUE_FOR_%s", key.toUpperCase());
        }
    }

    private static JSONObject fetchSettings(Long guildId) {
        JSONObject jsonObject = WebUtils.fetchJsonFromUrlApi(getGuildUrl(guildId));
        if (jsonObject.has("setting")) {
            jsonObject.getJSONObject("setting").put("local_expires_at", Instant.now().getEpochSecond() + EXPIRES_IN); // Expire in X seconds from now
            settingsCache.put(guildId, jsonObject.getJSONObject("setting"));
            return jsonObject.getJSONObject("setting");
        }
        return jsonObject;
    }

    private static void writeKey(Long guildId, String key, String value) {
        // Store new setting in the API to retain settings on reboot
        MultipartBody request = WebUtils.postToUrlApi(getGuildUrl(guildId))
                .field("id", guildId.toString())
                .field("setting", key)
                .field("value", value);

        HttpResponse<JsonNode> response = request.asJson();

        if (response.getStatus() == 200) {
            // Save new setting in cache immediately to instantly let settings take effect.
            if (settingsCache.containsKey(guildId)) {
                JSONObject cachedSettings = settingsCache.get(guildId);
                cachedSettings.getJSONObject("data").put(key, value);
                settingsCache.put(guildId, cachedSettings);
            }
        } else if (response.getStatus() == 401) {
            throw new HiromiApiAuthException();
        } else if (response.getStatus() == 429) {
            throw new HiromiApiTooManyRequestsException();
        }
    }

    private static String getKey(Long guildId, String key) {
        // Check if guild settings are cached and not expired if not then it returns the value straight from the cache
        if (settingsCache.containsKey(guildId)) {
            JSONObject guildCache = settingsCache.get(guildId);

            if (guildCache.has("local_expires_at") &&
                    guildCache.has("data")) {
                if (guildCache.getLong("local_expires_at") > Instant.now().getEpochSecond()) {
                    JSONObject guildSettingsCache = guildCache.getJSONObject("data");
                    if (guildSettingsCache.has(key)) {
                        return guildSettingsCache.getString(key);
                    } else {
                        return getDefaultSetting(key);
                    }
                }
            }
        }

        // Fetch settings from online api
        JSONObject jsonObject = fetchSettings(guildId);

        // If online api contains correct setting return it
        if (jsonObject.has("data") &&
                jsonObject.getJSONObject("data").has(key)) {
            return jsonObject.getJSONObject("data").getString(key);
        }

        return getDefaultSetting(key);
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