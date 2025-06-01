package com.ebicep.warlords.database.repositories.config;

import org.bson.Document;

import java.util.List;

public class SpecializationsConfig implements ConfigManager.Config {

    public Document classesConfig;

    @Override
    public String getName() {
        return "CLASSES";
    }

    @Override
    public void load(Document doc) {
        this.classesConfig = doc;
    }

    public <T> T getValue(List<String> namespaces, String key, Class<T> fieldType) {
        return ConfigUtils.getValue(classesConfig, namespaces, key, fieldType);
    }

    public <T> T getValue(List<String> namespaces, String key, Class<T> fieldType, T defaultValue) {
        return ConfigUtils.getValue(classesConfig, namespaces, key, fieldType, defaultValue);
    }

    public <T> List<T> getListValue(List<String> namespaces, String key, Class<T> itemType) {
        return ConfigUtils.getListValue(classesConfig, namespaces, key, itemType);
    }

}
