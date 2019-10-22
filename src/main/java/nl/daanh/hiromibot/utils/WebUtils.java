package nl.daanh.hiromibot.utils;

import kong.unirest.GetRequest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

public class WebUtils {
    private static String userAgent = "";
    private static String authorization = "";

    private static GetRequest defaultRequest(String url) {
        return Unirest.get(url).header("User-Agent", userAgent);
    }

    public static void setUserAgent(String userAgent) {
        WebUtils.userAgent = userAgent;
    }

    public static JSONObject fetchJsonFromUrl(String url) {
        HttpResponse<JsonNode> response = defaultRequest(url).asJson();
        return response.getBody().getObject();
    }

    public static JSONObject fetchJronFromUrlAuthorization(String url) {
        HttpResponse<JsonNode> response = defaultRequest(url).header("Authorization", authorization).asJson();
        return response.getBody().getObject();
    }
}