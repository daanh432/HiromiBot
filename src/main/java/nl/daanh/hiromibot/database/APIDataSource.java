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

package nl.daanh.hiromibot.database;

import nl.daanh.hiromibot.exceptions.HiromiApiAuthException;
import nl.daanh.hiromibot.exceptions.HiromiApiTooManyRequestsException;
import nl.daanh.hiromibot.objects.CommandInterface;
import nl.daanh.hiromibot.utils.WebUtils;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class APIDataSource implements DatabaseManager {
    private final Integer EXPIRES_IN = 60; // 60 seconds
    private final HashMap<Long, JSONObject> settingsCache = new HashMap<>();

    private String getGuildUrl(Long guildId) {
        return String.format("https://hiromi.daanh.nl/api/v1/settings/%s/guilds", guildId.toString());
    }

    private String getDefaultSetting(String key) {
        // If value has not been found on the online api or in the cache return the default value
        switch (key) {
            case "prefix":
                return "hi!";
            case "musicEnabled":
                return "false";
            case "funEnabled":
            case "moderationEnabled":
                return "true";
            case "createVoiceChannelId":
                return null;
            default:
                return String.format("NO_DEFAULT_VALUE_FOR_%s", key.toUpperCase());
        }
    }

    private JSONObject fetchSettings(Long guildId) {
        JSONObject jsonObject = WebUtils.fetchJsonFromUrlApi(getGuildUrl(guildId));
        if (jsonObject.has("setting")) {
            jsonObject.getJSONObject("setting").put("local_expires_at", Instant.now().getEpochSecond() + EXPIRES_IN); // Expire in X seconds from now
            settingsCache.put(guildId, jsonObject.getJSONObject("setting"));
            return jsonObject.getJSONObject("setting");
        }
        return jsonObject;
    }

    private void writeKey(Long guildId, String key, String value) {
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

    private String getKey(Long guildId, String key) {
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

    @Override
    public String getPrefix(long guildId) {
        return getKey(guildId, "prefix");
    }

    @Override
    public void setPrefix(long guildId, String newPrefix) {
        writeKey(guildId, "prefix", newPrefix);
    }

    @Override
    public boolean getMusicEnabled(long guildId) {
        String musicEnabled = getKey(guildId, "musicEnabled").toLowerCase();
        return musicEnabled.equals("on") || musicEnabled.equals("true") || musicEnabled.equals("enabled") || musicEnabled.equals("1") || musicEnabled.equals("enable");
    }

    @Override
    public void setMusicEnabled(long guildId, boolean enabled) {
        writeKey(guildId, "musicEnabled", enabled ? "true" : "false");
    }

    @Override
    public boolean getFunEnabled(long guildId) {
        String funEnabled = getKey(guildId, "funEnabled").toLowerCase();
        return funEnabled.equals("on") || funEnabled.equals("true") || funEnabled.equals("enabled") || funEnabled.equals("1") || funEnabled.equals("enable");
    }

    @Override
    public void setFunEnabled(long guildId, boolean enabled) {
        writeKey(guildId, "funEnabled", enabled ? "true" : "false");
    }

    @Override
    public boolean getModerationEnabled(long guildId) {
        String moderationEnabled = getKey(guildId, "moderationEnabled").toLowerCase();
        return moderationEnabled.equals("on") || moderationEnabled.equals("true") || moderationEnabled.equals("enabled") || moderationEnabled.equals("1") || moderationEnabled.equals("enable");
    }

    @Override
    public void setModerationEnabled(long guildId, boolean enabled) {
        writeKey(guildId, "moderationEnabled", enabled ? "true" : "false");
    }

    @Nullable
    @Override
    public Long getCreateVoiceChannelId(long guildId) {
        try {
            return Long.parseLong(getKey(guildId, "createVoiceChannelId"));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public void setCreateVoiceChannelId(long guildId, long voiceChannelId) {
        writeKey(guildId, "createVoiceChannelId", String.valueOf(voiceChannelId));
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
}
