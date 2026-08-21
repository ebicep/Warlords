package com.ebicep.warlords.database.repositories.config;

import com.ebicep.warlords.pve.newitems.NewItemsUtils;
import org.bson.Document;

public class NewItemsConfig implements ConfigManager.Config {

    public Document newItemsConfig;

    @Override
    public String getName() {
        return "NEW_ITEMS";
    }

    @Override
    public void load(Document doc) {
        this.newItemsConfig = doc;
        NewItemsUtils.reloadConfig();
    }

    @Override
    public Document getConfigDocument() {
        return this.newItemsConfig;
    }

}
