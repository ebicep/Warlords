package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.RecklessCharge;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.List;

import static com.ebicep.warlords.database.repositories.config.ConfigManager.DEFAULT_NAMESPACES;

public class HealingLink implements SpecBoostManager.SpecBoost<HealingLink> {

    @Override
    public void init() {
    }

    @Override
    public String getConfigFieldName() {
        return "healingLink";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                ConfigManager.getAbilityConfigValue(DEFAULT_NAMESPACES, "healingLink.energyCost", float.class),
                ConfigManager.getAbilityConfigValue(DEFAULT_NAMESPACES, "healingLink.cooldown", float.class),
                ConfigManager.getAbilityConfigValue(DEFAULT_NAMESPACES, "healingLink.healingValues.linkHealing.value", float.class),
                ConfigManager.getAbilityConfigValue(DEFAULT_NAMESPACES, "healingLink.tickDuration", int.class),
                ConfigManager.getAbilityConfigValue(DEFAULT_NAMESPACES, "healingLink.damagePercentTaken", float.class),
                ConfigManager.getAbilityConfigValue(DEFAULT_NAMESPACES, "healingLink.healingValues.linkEndHealing.value", float.class)
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public HealingLink get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            List<AbstractAbility> abilities = warlordsPlayer.getAbilities();
            for (int i = 0; i < abilities.size(); i++) {
                AbstractAbility ability = abilities.get(i);
                if (ability instanceof RecklessCharge) {
                    com.ebicep.warlords.abilities.HealingLink healingLink = new com.ebicep.warlords.abilities.HealingLink();
                    healingLink.init(healingLink.getBuilder());
                    abilities.set(i, healingLink);
                }
            }
            warlordsPlayer.resetAbilityTree();
        }

    }

}
