
package com.ebicep.warlords.database.configuration;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import javax.annotation.Nonnull;


public class GameMapConverter {

    @ReadingConverter
    public static class StringToGameMapConverter implements Converter<String, GameMap> {
        @Override
        public GameMap convert(@Nonnull String source) {
            for (GameMap map : GameMap.VALUES) {
                if (map.getDatabaseName().equalsIgnoreCase(source)) {
                    return map;
                }
            }
            ChatChannels.sendDebugMessage((CommandIssuer) null, "Could not convert " + source + " to GameMap");
            throw new IllegalArgumentException("Could not convert " + source + " to GameMap");
        }
    }

    @WritingConverter
    public static class GameMapToStringConverter implements Converter<GameMap, String> {
        @Override
        public String convert(@Nonnull GameMap source) {
            return source.getDatabaseName();
        }
    }

}
