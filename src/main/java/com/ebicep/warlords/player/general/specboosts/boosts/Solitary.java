package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.LastStand;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.motionsystem.MotionModifierBuilder;
import net.kyori.adventure.text.TextComponent;

import java.util.List;

import static com.ebicep.warlords.database.repositories.config.ConfigManager.DEFAULT_NAMESPACES;

public class Solitary implements SpecBoostManager.SpecBoost<Solitary> {

    private float knockbackResistancePercent;

    @Override
    public void init() {
        this.knockbackResistancePercent = getValue("knockbackResistancePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "solitary";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                knockbackResistancePercent,
                ConfigManager.getAbilityConfigValue(DEFAULT_NAMESPACES, "solitary.damageReduction", float.class),
                ConfigManager.getAbilityConfigValue(DEFAULT_NAMESPACES, "solitary.healthPercentageHealing", float.class)
        );
    }

    @Override
    public TextComponent getDescription() {
        return appendAbility(getTextDescription(), new com.ebicep.warlords.abilities.Solitary());
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public Solitary get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getKnockback().addModifier(
                    new MotionModifierBuilder()
                            .setFrom(warlordsPlayer)
                            .setName("BASE")
                            .setModifier(-knockbackResistancePercent)
                            .setDuration(-1)
                            .build()
            );
            List<AbstractAbility> abilities = warlordsPlayer.getAbilities();
            for (int i = 0; i < abilities.size(); i++) {
                AbstractAbility ability = abilities.get(i);
                if (ability instanceof LastStand lastStand) {
                    com.ebicep.warlords.abilities.Solitary solitary = new com.ebicep.warlords.abilities.Solitary();
                    solitary.init(solitary.getBuilder());
                    abilities.set(i, solitary);
                }
            }
            warlordsPlayer.resetAbilityTree();
        }

    }

}
