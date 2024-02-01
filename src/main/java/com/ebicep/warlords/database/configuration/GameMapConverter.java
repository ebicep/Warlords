
package com.ebicep.warlords.database.configuration;

import com.ebicep.warlords.game.GameMap;
import org.springframework.core.convert.converter.Converter;

import javax.annotation.Nonnull;


public class GameMapConverter {

    public static class StringToGameMapConverter implements Converter<String, GameMap> {
        @Override
        public GameMap convert(@Nonnull String source) {
            for (GameMap map : GameMap.VALUES) {
                if (map.getDatabaseName().equalsIgnoreCase(source)) {
                    return map;
                }
            }
            return null;
        }
    }

    public static class GameMapToStringConverter implements Converter<GameMap, String> {
        @Override
        public String convert(@Nonnull GameMap source) {
            return source.getDatabaseName();
        }
    }

}
