package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.HammerOfLight;
import com.ebicep.warlords.abilities.HammerOfLight.HammerOfLightData;
import com.ebicep.warlords.events.player.ingame.WarlordsHammerToCrownEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import org.bukkit.event.EventHandler;

import java.util.List;

public class LustrousCrown implements SpecBoostManager.SpecBoost<LustrousCrown> {

    private int hammerOfLightDurationReductionTicks;
    private float crownOfLightRadiusIncrease;
    private float crownOfLightHealingIncreasePercent;
    private float crownOfLightSpeedPercent;

    @Override
    public void init() {
        this.hammerOfLightDurationReductionTicks = getValue("hammerOfLightDurationReductionTicks", int.class);
        this.crownOfLightRadiusIncrease = getValue("crownOfLightRadiusIncrease", float.class);
        this.crownOfLightHealingIncreasePercent = getValue("crownOfLightHealingIncreasePercent", float.class);
        this.crownOfLightSpeedPercent = getValue("crownOfLightSpeedPercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "lustrousCrown";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(hammerOfLightDurationReductionTicks, crownOfLightRadiusIncrease, crownOfLightHealingIncreasePercent, crownOfLightSpeedPercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public LustrousCrown get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(HammerOfLight.class).forEach(hammerOfLight -> {
                hammerOfLight.setTickDuration(hammerOfLight.getTickDuration() - hammerOfLightDurationReductionTicks);
                hammerOfLight.getCrownRadius().addAdditiveModifier("Spec Boost", crownOfLightRadiusIncrease);
                hammerOfLight.setCrownBonusHealing(hammerOfLight.getCrownBonusHealing() + crownOfLightHealingIncreasePercent);
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onWarlordsHammerToCrownEventEvent(WarlordsHammerToCrownEvent event) {
            if (!event.getWarlordsEntity().equals(warlordsEntity)) {
                return;
            }
            AbstractCooldown<?> cooldown = event.getCooldown();
            if (!(cooldown.getCooldownObject() instanceof HammerOfLightData)) {
                return;
            }
            warlordsEntity.addSpeedModifier(warlordsEntity, getStringName(), crownOfLightSpeedPercent, cooldown);
        }

    }

}
