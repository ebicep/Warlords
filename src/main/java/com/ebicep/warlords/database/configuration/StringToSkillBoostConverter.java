
package com.ebicep.warlords.database.configuration;

import com.ebicep.warlords.player.general.SkillBoosts;
import org.springframework.core.convert.converter.Converter;

import javax.annotation.Nonnull;


public class StringToSkillBoostConverter implements Converter<String, SkillBoosts> {

    @Override
    public SkillBoosts convert(@Nonnull String s) {
        if (s.equals("VITALITY_LIQUOR")) {
            return SkillBoosts.VITALITY_CONCOCTION;
        }
        for (SkillBoosts skillBoost : SkillBoosts.VALUES) {
            if (skillBoost.name().equalsIgnoreCase(s)) {
                return skillBoost;
            }
        }
        return null;
    }

}
