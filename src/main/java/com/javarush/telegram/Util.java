package com.javarush.telegram;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
public class Util {

    private Map<String, String> valueData;
    private Yaml yaml = new Yaml(new Constructor(Map.class));

    private String telegramBotName;
    private String telegramBotToken;
    private String openAIToken;

    public Map<String, String> loadYAMLFile(String filePath){
        try (InputStream inputStream = Util.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Yaml file not found.");
            }
            return yaml.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTelegramBotName() {
        return telegramBotName;
    }

    public void setTelegramBotName(String telegramBotName) {
        this.telegramBotName = telegramBotName;
    }

    public String getTelegramBotToken() {
        return telegramBotToken;
    }

    public void setTelegramBotToken(String telegramBotToken) {
        this.telegramBotToken = telegramBotToken;
    }

    public String getOpenAIToken() {
        return openAIToken;
    }

    public void setOpenAIToken(String openAIToken) {
        this.openAIToken = openAIToken;
    }

    public void loadTokens(){
        valueData = loadYAMLFile("tokens.yaml");
        setTelegramBotName(valueData.get("TELEGRAM_BOT_NAME"));
        setTelegramBotToken(valueData.get("TELEGRAM_BOT_TOKEN"));
        setOpenAIToken(valueData.get("OPEN_AI_TOKEN"));
    }

}
