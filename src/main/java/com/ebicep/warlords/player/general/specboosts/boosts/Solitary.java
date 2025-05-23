package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.LastStand;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ebicep.warlords.database.repositories.config.ConfigManager.DEFAULT_NAMESPACES;

public class Solitary implements SpecBoostManager.SpecBoost<Solitary> {

    @Override
    public void init() {
    }

    @Override
    public String getConfigFieldName() {
        return "solitary";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                ConfigManager.getAbilityConfigValue(DEFAULT_NAMESPACES, "solitary.damageReduction", float.class),
                ConfigManager.getAbilityConfigValue(DEFAULT_NAMESPACES, "solitary.healthPercentageHealing", float.class)
        );
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

        private final Map<AbstractAbility, LastStand> swappedAbilities = new HashMap<>();

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            List<AbstractAbility> abilities = warlordsPlayer.getAbilities();
            for (int i = 0; i < abilities.size(); i++) {
                AbstractAbility ability = abilities.get(i);
                if (ability instanceof LastStand lastStand) {
                    com.ebicep.warlords.abilities.Solitary solitary = new com.ebicep.warlords.abilities.Solitary();
                    solitary.init(solitary.getBuilder());
                    abilities.set(i, solitary);
                    swappedAbilities.put(solitary, lastStand);
                }
            }
            warlordsPlayer.resetAbilityTree();
        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {
            List<AbstractAbility> abilities = warlordsPlayer.getAbilities();
            for (int i = 0; i < abilities.size(); i++) {
                AbstractAbility ability = abilities.get(i);
                LastStand lastStand = swappedAbilities.get(ability);
                if (lastStand != null) {
                    abilities.set(i, lastStand);
                    swappedAbilities.remove(ability);
                }
            }
            warlordsPlayer.resetAbilityTree();
        }

    }

}
