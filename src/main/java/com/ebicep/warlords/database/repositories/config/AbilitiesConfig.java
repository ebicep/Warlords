package com.ebicep.warlords.database.repositories.config;

import org.bson.Document;

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

    @Override
    public Document getConfigDocument() {
        return this.abilitiesConfig;
    }

}
