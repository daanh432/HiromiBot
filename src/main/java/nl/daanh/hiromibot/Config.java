package nl.daanh.hiromibot;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Config extends JSONObject {

    private static Config instance;

    public Config(File file) throws IOException {
        super(load(file));
        instance = this;
    }

    private static String load(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }

    public static Config getInstance() {
        return instance;
    }
}
