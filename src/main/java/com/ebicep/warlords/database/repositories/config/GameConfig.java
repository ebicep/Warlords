package com.ebicep.warlords.database.repositories.config;

import com.ebicep.warlords.game.option.pvp.HorseOption;
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
        HorseOption.horseItem = HorseOption.getUpdatedHorseItem();
    }

    @Override
    public Document getConfigDocument() {
        return this.gameConfig;
    }

}
