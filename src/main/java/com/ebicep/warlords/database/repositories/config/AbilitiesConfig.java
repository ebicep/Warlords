package com.ebicep.warlords.database.repositories.config;

import org.bson.Document;

import java.util.List;

public class AbilitiesConfig implements ConfigManager.Config {

    public Document abilitiesConfig;
    // TODO cache

    @Override
    public String getName() {
        return "ABILITIES";
    }

    @Override
    public void load(Document doc) {
        this.abilitiesConfig = doc;
    }

    public <T> T getValue(List<String> namespaces, String key, Class<T> fieldType) {
        return ConfigUtils.getValue(abilitiesConfig, namespaces, key, fieldType);
    }

    public <T> T getValue(List<String> namespaces, String key, Class<T> fieldType, T defaultValue) {
        return ConfigUtils.getValue(abilitiesConfig, namespaces, key, fieldType, defaultValue);
    }

}
