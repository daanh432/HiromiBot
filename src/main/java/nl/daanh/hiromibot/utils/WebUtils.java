package nl.daanh.hiromibot.utils;

import kong.unirest.*;
import kong.unirest.json.JSONObject;

public class WebUtils {
    private static String userAgent = "";
    private static String hiromiApiToken = "";

    private static GetRequest defaultRequest(String url) {
        return Unirest.get(url).header("User-Agent", userAgent);
    }

    private static GetRequest defaultApiRequest(String url) {
        return Unirest.get(url).header("User-Agent", userAgent).header("Authorization", hiromiApiToken);
    }

    public static void setUserAgent(String userAgent) {
        WebUtils.userAgent = userAgent;
    }

    public static void setHiromiApiToken(String token) {
        WebUtils.hiromiApiToken = token;
    }

    public static JSONObject fetchJsonFromUrl(String url) {
        return defaultRequest(url).asJson().getBody().getObject();
    }

    static JSONObject fetchJsonFromUrlApi(String url) {
        HttpResponse<JsonNode> jsonResponse = defaultApiRequest(url).asJson();
        if (jsonResponse.getStatus() == 200) {
            return jsonResponse.getBody().getObject();
        } else if (jsonResponse.getStatus() == 401) {
            throw new HiromiApiAuthException();
        } else if (jsonResponse.getStatus() == 429) {
            return new JSONObject().put("status", 429);
        } else {
            throw new HiromiApiException();
        }
    }

    static HttpRequestWithBody postToUrlApi(String url) {
        return Unirest.post(url).header("User-Agent", userAgent).header("Authorization", hiromiApiToken);
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