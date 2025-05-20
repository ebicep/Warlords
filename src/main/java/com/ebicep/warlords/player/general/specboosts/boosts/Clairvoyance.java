package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.HealingRain;
import com.ebicep.warlords.abilities.TimeWarpAquamancer;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ebicep.warlords.database.repositories.config.ConfigManager.DEFAULT_NAMESPACES;

public class Clairvoyance implements SpecBoostManager.SpecBoost<Clairvoyance> {

    private int healingRainDurationTicks;
    private float healingRainHealIncreasePercent;

    @Override
    public void init() {
        this.healingRainDurationTicks = getValue("healingRainDurationTicks", int.class);
        this.healingRainHealIncreasePercent = getValue("healingRainHealIncreasePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "clairvoyance";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                ConfigManager.getAbilityConfigValue(DEFAULT_NAMESPACES, "clairvoyance.healingValues.healing.value", int.class),
                ConfigManager.getAbilityConfigValue(DEFAULT_NAMESPACES, "clairvoyance.healingIncreasePercent", float.class),
                ConfigManager.getAbilityConfigValue(DEFAULT_NAMESPACES, "clairvoyance.speedIncrease", float.class),
                ConfigManager.getAbilityConfigValue(DEFAULT_NAMESPACES, "clairvoyance.tickDuration", int.class),
                healingRainDurationTicks,
                healingRainHealIncreasePercent
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public Clairvoyance get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private final Map<AbstractAbility, TimeWarpAquamancer> swappedAbilities = new HashMap<>();
        private final Map<HealingRain, Integer> oldRainDuration = new HashMap<>();

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            List<AbstractAbility> abilities = warlordsPlayer.getAbilities();
            for (int i = 0; i < abilities.size(); i++) {
                AbstractAbility ability = abilities.get(i);
                if (ability instanceof TimeWarpAquamancer timeWarpAquamancer) {
                    com.ebicep.warlords.abilities.Clairvoyance clairvoyance = new com.ebicep.warlords.abilities.Clairvoyance();
                    clairvoyance.init(clairvoyance.getBuilder());
                    abilities.set(i, clairvoyance);
                    swappedAbilities.put(clairvoyance, timeWarpAquamancer);
                } else if (ability instanceof HealingRain healingRain) {
                    oldRainDuration.put(healingRain, healingRain.getTickDuration());
                    healingRain.setTickDuration(healingRainDurationTicks);
                    healingRain.getHealValues().getRainHealing().forEachValue(floatModifiable ->
                            floatModifiable.addMultiplicativeModifierAdd("Spec Boost", healingRainHealIncreasePercent / 100)
                    );
                }
            }
            warlordsPlayer.resetAbilityTree();
        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {
            List<AbstractAbility> abilities = warlordsPlayer.getAbilities();
            for (int i = 0; i < abilities.size(); i++) {
                AbstractAbility ability = abilities.get(i);
                TimeWarpAquamancer timeWarpAquamancer = swappedAbilities.get(ability);
                if (timeWarpAquamancer != null) {
                    abilities.set(i, timeWarpAquamancer);
                    swappedAbilities.remove(ability);
                }
                if (ability instanceof HealingRain healingRain) {
                    Integer oldDuration = oldRainDuration.get(healingRain);
                    if (oldDuration != null) {
                        healingRain.setTickDuration(oldDuration);
                        oldRainDuration.remove(healingRain);
                    }
                    healingRain.getHealValues().getRainHealing().forEachValue(floatModifiable ->
                            floatModifiable.removeModifier("Spec Boost")
                    );
                }
            }
            warlordsPlayer.resetAbilityTree();
        }

    }

}
