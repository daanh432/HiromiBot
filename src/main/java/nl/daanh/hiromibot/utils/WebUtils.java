package nl.daanh.hiromibot.utils;

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

    static JSONObject fetchJsonFromUrlApi(String url) {
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

    static Request.Builder postToUrlApi(String url) {
        return new Request.Builder().url(url).addHeader("User-Agent", userAgent).addHeader("Authorization", hiromiApiToken);
    }
}

class HiromiApiException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Some unknown error occurred in the Hiromi API connection.";
    }
}

class HiromiApiAuthException extends HiromiApiException {
    @Override
    public String getMessage() {
        return "The token for the Hiromi API is not valid. Or another error happened during during authentication.";
    }
}

class HiromiApiTooManyRequestsException extends HiromiApiException {
    @Override
    public String getMessage() {
        return "You're being rate limited by the Hiromi API";
    }
}