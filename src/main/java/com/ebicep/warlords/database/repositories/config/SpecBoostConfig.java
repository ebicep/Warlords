package com.ebicep.warlords.database.repositories.config;

import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import org.bson.Document;

import java.util.List;

public class SpecBoostConfig implements ConfigManager.Config {

    public Document specBoostConfig;

    @Override
    public String getName() {
        return "SPEC_BOOSTS";
    }

    @Override
    public void load(Document doc) {
        this.specBoostConfig = doc;
        SpecBoostManager.init();
    }

    public <T> T getValue(List<String> namespaces, String key, Class<T> fieldType) {
        return ConfigUtils.getValue(specBoostConfig, namespaces, key, fieldType);
    }

    public <T> T getValue(List<String> namespaces, String key, Class<T> fieldType, T defaultValue) {
        return ConfigUtils.getValue(specBoostConfig, namespaces, key, fieldType, defaultValue);
    }

}
