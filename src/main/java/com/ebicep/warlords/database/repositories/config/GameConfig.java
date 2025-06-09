package com.ebicep.warlords.database.repositories.config;

import com.ebicep.warlords.game.option.pvp.HorseOption;
import org.bson.Document;

import java.util.List;

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

    public <T> T getValue(List<String> namespaces, String key, Class<T> fieldType) {
        return ConfigUtils.getValue(gameConfig, namespaces, key, fieldType);
    }

    public <T> T getValue(List<String> namespaces, String key, Class<T> fieldType, T defaultValue) {
        return ConfigUtils.getValue(gameConfig, namespaces, key, fieldType, defaultValue);
    }

}
