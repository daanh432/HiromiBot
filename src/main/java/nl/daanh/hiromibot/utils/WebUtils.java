package nl.daanh.hiromibot.utils;

import kong.unirest.GetRequest;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

public class WebUtils {
    private static String userAgent = "";
    private static String wolkeTokenHiromiApi = "";

    private static GetRequest defaultRequest(String url) {
        return Unirest.get(url).header("User-Agent", userAgent);
    }

    private static GetRequest defaultApiRequest(String url) {
        return Unirest.get(url).header("User-Agent", userAgent).header("Authorization", wolkeTokenHiromiApi);
    }

    public static void setUserAgent(String userAgent) {
        WebUtils.userAgent = userAgent;
    }

    public static void setWolkeTokenHiromiApi(String token) {
        WebUtils.wolkeTokenHiromiApi = token;
    }

    public static JSONObject fetchJsonFromUrl(String url) {
        return defaultRequest(url).asJson().getBody().getObject();
    }

    static JSONObject fetchJsonFromUrlApi(String url) {
        return defaultApiRequest(url).asJson().getBody().getObject();
    }

    static HttpRequestWithBody postToUrlApi(String url) {
        return Unirest.post(url).header("User-Agent", userAgent).header("Authorization", wolkeTokenHiromiApi);
    }
}