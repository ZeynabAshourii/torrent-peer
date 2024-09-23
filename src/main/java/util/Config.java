package util;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Config extends Properties {
    private static final Config instance = new Config();
    protected String configPath;
    private Config() {}
    public void setPath(String Path) { // sets config file path
        configPath = Path;
        try {
            load(new FileReader(configPath));
        } catch (IOException e) {
            System.out.println("Error Reading util.Config File : " + configPath);
        }
    }


    public <T> T getProperty(String K, Class<T> c) { // loads a single property from config file
        String value = getProperty(K);
        if (value == null)
            throw new RuntimeException("util.Config property: " + K + " not set");
        try {
            return c.getConstructor(String.class).newInstance(value);
        } catch (Exception ignored) {}
        return null;
    }


    @Override
    public String getProperty(String key) {
        var out = super.getProperty(key);
        if (out == null)
            throw new RuntimeException("util.Config property: " + key + " not set");
        return out;
    }

    public Map<String, String> getArray(String K) {
        String value = getProperty(K);
        if (value == null)
            throw new RuntimeException("util.Config property: " + K + " not set");
        String[] keys = value.split(",");
        Map<String, String> out = new HashMap<>();
        for (String key : keys)
            out.put(key, getProperty(key));
        return out;
    }

    public static Config getInstance() {
        return instance;
    }
}

