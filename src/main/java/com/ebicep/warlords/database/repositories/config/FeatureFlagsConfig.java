package com.ebicep.warlords.database.repositories.config;

import org.bson.Document;

public class FeatureFlagsConfig implements ConfigManager.Config {

    private Document featureFlagsConfig = new Document();

    @Override
    public String getName() {
        return "FEATURE_FLAGS";
    }

    @Override
    public void load(Document doc) {
        this.featureFlagsConfig = doc != null ? doc : new Document();
    }

    @Override
    public Document getConfigDocument() {
        return this.featureFlagsConfig;
    }

}
