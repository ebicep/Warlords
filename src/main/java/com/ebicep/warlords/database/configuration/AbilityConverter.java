
package com.ebicep.warlords.database.configuration;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import javax.annotation.Nonnull;


public class AbilityConverter {

    @ReadingConverter
    public static class StringToAbilityConverter implements Converter<String, Ability<?>> {
        @Override
        public Ability<?> convert(@Nonnull String source) {
            for (Ability<?> ability : Ability.VALUES) {
                if (ability.getDatabaseName().equalsIgnoreCase(source)) {
                    return ability;
                }
            }
            ChatChannels.sendDebugMessage((CommandIssuer) null, "Could not convert " + source + " to GameMap");
            throw new IllegalArgumentException("Could not convert " + source + " to GameMap");
        }
    }

    @WritingConverter
    public static class AbilityToStringConverter implements Converter<Ability<?>, String> {
        @Override
        public String convert(@Nonnull Ability<?> source) {
            return source.getDatabaseName();
        }
    }

}
