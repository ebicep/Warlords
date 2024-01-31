
package com.ebicep.warlords.database.configuration;

import com.ebicep.warlords.game.GameMap;
import org.springframework.core.convert.converter.Converter;

/**
 * TODO Convert GameMap to string then static gamemap value
 */
public class GameMapConverter implements Converter<GameMap, GameMap> {


    @Override
    public GameMap convert(GameMap source) {
        return null;
    }

    @Override
    public <U> Converter<GameMap, U> andThen(Converter<? super GameMap, ? extends U> after) {
        return Converter.super.andThen(after);
    }
}
