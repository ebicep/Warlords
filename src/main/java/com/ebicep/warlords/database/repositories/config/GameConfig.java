package com.ebicep.warlords.database.repositories.config;

import com.ebicep.warlords.game.option.pvp.MountOption;
import org.bson.Document;

public class GameConfig implements ConfigManager.Config {

    public Document gameConfig;

    @Override
    public String getName() {
        return "GAME";
    }

    @Override
    public void load(Document doc) {
        this.gameConfig = doc;
        MountOption.mountItem = MountOption.getUpdatedMountItem();
    }

    @Override
    public Document getConfigDocument() {
        return this.gameConfig;
    }

}
