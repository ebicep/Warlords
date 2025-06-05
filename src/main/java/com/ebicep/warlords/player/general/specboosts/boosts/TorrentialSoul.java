package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.OrderOfEviscerate;
import com.ebicep.warlords.abilities.SoulSwitch;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.instances.type.EnergyInstance;
import org.bukkit.event.EventHandler;

import java.util.List;

public class TorrentialSoul implements SpecBoostManager.SpecBoost<TorrentialSoul> {

    private int maxEnergyIncrease;
    private int orderOfEviscerateSpeedIncreasePercent;
    private int orderOfEviscerateEnergyPerSecond;

    @Override
    public void init() {
        this.maxEnergyIncrease = getValue("maxEnergyIncrease", int.class);
        this.orderOfEviscerateSpeedIncreasePercent = getValue("orderOfEviscerateSpeedIncreasePercent", int.class);
        this.orderOfEviscerateEnergyPerSecond = getValue("orderOfEviscerateEnergyPerSecond", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "torrentialSoul";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(maxEnergyIncrease, orderOfEviscerateSpeedIncreasePercent, orderOfEviscerateEnergyPerSecond);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public TorrentialSoul get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsPlayer warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getEnergy().addAdditiveModifier("Spec Boost", maxEnergyIncrease);
            warlordsPlayer.getAbilitiesMatching(SoulSwitch.class).forEach(soulSwitch -> {
                soulSwitch.getEnergyCost().addOverridingModifier("Spec Boost", 0);
            });
            warlordsPlayer.getAbilitiesMatching(OrderOfEviscerate.class).forEach(orderOfEviscerate -> {
                orderOfEviscerate.setSpeedBuff(orderOfEviscerate.getSpeedBuff() + orderOfEviscerateSpeedIncreasePercent);
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!(cooldown.getCooldownObject() instanceof OrderOfEviscerate.OrderOfEviscerateData data) || !cooldown.getFrom().equals(warlordsEntity)) {
                return;
            }
            cooldown.addExtraEnergyInstance(new EnergyInstance() {
                @Override
                public float addEnergyGainPerTick(float energyGainPerTick) {
                    return energyGainPerTick + orderOfEviscerateEnergyPerSecond / 20f;
                }
            });
        }

    }

}
