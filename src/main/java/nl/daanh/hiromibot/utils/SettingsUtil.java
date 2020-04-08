/*
 * HiromiBot, a multipurpose open source Discord bot
 * Copyright (c) 2019 - 2020 daanh432
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package nl.daanh.hiromibot.utils;

import nl.daanh.hiromibot.objects.CommandInterface;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SettingsUtil {
    private static final Integer EXPIRES_IN = 60; // 60 seconds
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsUtil.class);
    private static final HashMap<Long, JSONObject> settingsCache = new HashMap<>();

    private static String getGuildUrl(Long guildId) {
        return String.format("https://hiromi.daanh.nl/api/v1/settings/%s/guilds", guildId.toString());
    }

    private static String getDefaultSetting(String key) {
        // If value has not been found on the online api or in the cache return the default value
        switch (key) {
            case "prefix":
                return "hi!";
            case "musicEnabled":
                return "false";
            case "funEnabled":
            case "moderationEnabled":
                return "true";
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
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("id", guildId.toString())
                .addFormDataPart("setting", key)
                .addFormDataPart("value", value)
                .build();
        Request.Builder request = WebUtils.postToUrlApi(getGuildUrl(guildId));
        request.post(requestBody);

        try (Response response = WebUtils.client.newCall(request.build()).execute()) {
            if (response.code() == 201) {
                // Save new setting in cache immediately to instantly let settings take effect.
                if (settingsCache.containsKey(guildId)) {
                    JSONObject cachedSettings = settingsCache.get(guildId);
                    cachedSettings.put("local_expires_at", Instant.now().getEpochSecond() + EXPIRES_IN); // Expire in X seconds from now
                    cachedSettings.getJSONObject("data").put(key, value);
                    settingsCache.put(guildId, cachedSettings);
                }
            } else if (response.code() == 401) {
                throw new HiromiApiAuthException();
            } else if (response.code() == 429) {
                throw new HiromiApiTooManyRequestsException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getKey(Long guildId, String key) {
        // Check if guild settings are cached and not expired if not then it returns the value straight from the cache
        if (settingsCache.containsKey(guildId)) {
            JSONObject guildCache = settingsCache.get(guildId);
            if (guildCache.has("local_expires_at") && guildCache.has("data")) {
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

    public static Boolean getFunEnabled(Long guildId) {
        String funEnabled = getKey(guildId, "funEnabled").toLowerCase();
        return funEnabled.equals("on") || funEnabled.equals("true") || funEnabled.equals("enabled") || funEnabled.equals("1") || funEnabled.equals("enable");
    }

    public static void setFunEnabled(Long guildId, Boolean funEnabled) {
        writeKey(guildId, "funEnabled", funEnabled ? "true" : "false");
    }

    public static Boolean getModerationEnabled(Long guildId) {
        String moderationEnabled = getKey(guildId, "moderationEnabled").toLowerCase();
        return moderationEnabled.equals("on") || moderationEnabled.equals("true") || moderationEnabled.equals("enabled") || moderationEnabled.equals("1") || moderationEnabled.equals("enable");
    }

    public static void setModerationEnabled(Long guildId, Boolean moderationEnabled) {
        writeKey(guildId, "moderationEnabled", moderationEnabled ? "true" : "false");
    }

    public static List<CommandInterface.CATEGORY> getEnabledCategories(Long guildId) {
        List<CommandInterface.CATEGORY> list = new ArrayList<>();
        list.add(CommandInterface.CATEGORY.OTHER);

        if (SettingsUtil.getMusicEnabled(guildId)) list.add(CommandInterface.CATEGORY.MUSIC);
        if (SettingsUtil.getFunEnabled(guildId)) list.add(CommandInterface.CATEGORY.FUN);
        if (SettingsUtil.getModerationEnabled(guildId)) list.add(CommandInterface.CATEGORY.MODERATION);

        return list;
    }
}