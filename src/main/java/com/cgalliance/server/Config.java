package com.cgalliance.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Конфигурация серверных настроек
 *
 * @author Iskander Yagfarov
 * @since 06.04.2018
 */
public class Config {

    /**
     * Холдер экземпляра серверных настроек
     */
    private static class ConfigHolder {
        public static final Config HOLDER_INSTANCE = new Config();
    }

    /**
     * Возвращает единственный экземпляр серверных настроек
     *
     * @return экземпляр серверных настроек
     */
    public static Config getInstance() {
        return ConfigHolder.HOLDER_INSTANCE;
    }

    /** Имя ключа для получения значения порта */
    private static final String PORT = "port";

    /** Путь до файла с серверными настройками */
    private final String SERVER_PROPERTIES_FILE_PATH =  "src/main/resources/server.properties";

    /** Настройки */
    private Properties properties;

    /**
     *  Конструктор
     */
    private Config() {
        properties = new Properties();
        initProperties();
    }

    /**
     * Инициализируем серверные настройки
     */
    private void initProperties() {
        try (FileInputStream fis = new FileInputStream(SERVER_PROPERTIES_FILE_PATH)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Не удалось найти файл с серверными настройками по указаному пути: %s!", SERVER_PROPERTIES_FILE_PATH));
        }
    }

    /**
     * Возвращает порт
     *
     * @return порт
     */
    public Integer getPort() {
        return Integer.valueOf(properties.getProperty(PORT));
    }
}
