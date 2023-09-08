package ru.nsu.bot;

import lombok.Data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Data
public class BotConfig {

    private static BotConfig instance;
    private String token;
    private String userName;
    private Properties properties;
    private static final String FILE_NAME = "config.properties";

    {
        try {
            properties = new Properties();
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(FILE_NAME)){
                properties.load(inputStream);
            } catch (IOException e){
                throw new IOException(String.format("Error loading properties file '%s'", FILE_NAME));
            }
            token = properties.getProperty("token");
            if (token == null){
                throw new IllegalArgumentException("Token value is null");
            }
            userName = properties.getProperty("botname");
            if (userName == null){
                throw new IllegalArgumentException("Bot name is null");
            }
        } catch (IOException e){
            throw new IllegalArgumentException("Bot initializer error: " + e.getMessage());
        }
    }
    public static BotConfig getInstance(){
        if (instance == null){
            instance = new BotConfig();
        }
        return instance;
    }
}
