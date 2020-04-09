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

import nl.daanh.hiromibot.exceptions.HiromiApiAuthException;
import nl.daanh.hiromibot.exceptions.HiromiApiException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class WebUtils {
    public static OkHttpClient client = new OkHttpClient();

    private static String userAgent = "";
    private static String hiromiApiToken = "";

    private static Request.Builder defaultRequest(String url) {
        return new Request.Builder().url(url).addHeader("User-Agent", userAgent);
    }

    private static Request.Builder defaultApiRequest(String url) {
        return new Request.Builder().url(url).addHeader("User-Agent", userAgent).addHeader("Authorization", hiromiApiToken);
    }

    public static void setUserAgent(String userAgent) {
        WebUtils.userAgent = userAgent;
    }

    public static void setHiromiApiToken(String token) {
        WebUtils.hiromiApiToken = token;
    }

    public static JSONObject fetchJsonFromUrl(String url) {
        Request request = defaultRequest(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                return new JSONObject(Objects.requireNonNull(response.body()).string());
            }
            throw new RuntimeException("Empty json body");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONObject fetchJsonFromUrlApi(String url) {
        Request request = defaultApiRequest(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 200 && response.body() != null) {
                return new JSONObject(Objects.requireNonNull(response.body()).string());
            } else if (response.code() == 401) {
                throw new HiromiApiAuthException();
            } else if (response.code() == 429) {
                return new JSONObject().put("status", 429);
            } else {
                throw new HiromiApiException();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Request.Builder postToUrlApi(String url) {
        return new Request.Builder().url(url).addHeader("User-Agent", userAgent).addHeader("Authorization", hiromiApiToken);
    }
}