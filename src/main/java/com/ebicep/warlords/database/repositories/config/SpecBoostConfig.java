package com.ebicep.warlords.database.repositories.config;

import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import org.bson.Document;

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

    @Override
    public Document getConfigDocument() {
        return this.specBoostConfig;
    }

}
