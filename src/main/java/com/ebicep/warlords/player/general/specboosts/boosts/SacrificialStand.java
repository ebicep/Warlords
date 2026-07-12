package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.LastStand;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.TextComponent;

import java.util.List;

import static com.ebicep.warlords.database.repositories.config.ConfigManager.DEFAULT_NAMESPACES;

public class SacrificialStand implements SpecBoostManager.SpecBoost<SacrificialStand> {

    @Override
    public void init() {
    }

    @Override
    public String getConfigFieldName() {
        return "sacrificialStand";
    }

    @Override
    public TextComponent getDescription() {
        return appendAbility(getTextDescription(), new com.ebicep.warlords.abilities.SacrificialStand());
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                ConfigManager.getAbilityConfigValue(DEFAULT_NAMESPACES, "sacrificialStand.radius", int.class),
                ConfigManager.getAbilityConfigValue(DEFAULT_NAMESPACES, "sacrificialStand.tickDuration", int.class),
                ConfigManager.getAbilityConfigValue(DEFAULT_NAMESPACES, "sacrificialStand.damageReductionPercent", int.class),
                ConfigManager.getAbilityConfigValue(DEFAULT_NAMESPACES, "sacrificialStand.allyHealMultiplierPercent", int.class)
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public SacrificialStand get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            List<AbstractAbility> abilities = warlordsPlayer.getAbilities();
            for (int i = 0; i < abilities.size(); i++) {
                AbstractAbility ability = abilities.get(i);
                if (ability instanceof LastStand) {
                    com.ebicep.warlords.abilities.SacrificialStand sacrificialStand = new com.ebicep.warlords.abilities.SacrificialStand();
                    sacrificialStand.init(sacrificialStand.getBuilder());
                    abilities.set(i, sacrificialStand);
                }
            }
            warlordsPlayer.resetAbilityTree();
        }

    }

}
