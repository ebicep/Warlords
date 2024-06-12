
package com.ebicep.warlords.database.configuration;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import javax.annotation.Nonnull;

@ReadingConverter
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
        ChatChannels.sendDebugMessage((CommandIssuer) null, "Could not convert " + s + " to SkillBoosts");
        return null;
    }

}
