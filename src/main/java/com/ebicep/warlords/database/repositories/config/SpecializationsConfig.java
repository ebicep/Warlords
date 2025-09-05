package com.ebicep.warlords.database.repositories.config;

import org.bson.Document;

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

    @Override
    public Document getConfigDocument() {
        return this.classesConfig;
    }

}
