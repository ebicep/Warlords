package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.WaterBolt;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.event.EventHandler;

import java.util.EnumSet;
import java.util.List;

public class Syringe implements SpecBoostManager.SpecBoost<Syringe> {

    private float waterBoltStatsDecreasePercent;
    private float waterBoltDirectHitHealingIncreasePercent;
    private float waterBoltDirectHitSpeedIncreasePercent;
    private int waterBoltDirectHitDurationTicks;

    @Override
    public void init() {
        this.waterBoltStatsDecreasePercent = getValue("waterBoltStatsDecreasePercent", float.class);
        this.waterBoltDirectHitHealingIncreasePercent = getValue("waterBoltDirectHitHealingIncreasePercent", float.class);
        this.waterBoltDirectHitSpeedIncreasePercent = getValue("waterBoltDirectHitSpeedIncreasePercent", float.class);
        this.waterBoltDirectHitDurationTicks = getValue("waterBoltDirectHitDurationTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "syringe";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(waterBoltStatsDecreasePercent, waterBoltDirectHitHealingIncreasePercent, waterBoltDirectHitSpeedIncreasePercent, waterBoltDirectHitDurationTicks);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public Syringe get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(WaterBolt.class).forEach(waterBolt -> {
                waterBolt.getDamageValues().getBoltDamage().forEachValue(floatModifiable ->
                        floatModifiable.addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "Spec Boost", -waterBoltStatsDecreasePercent / 100)
                );
                waterBolt.getHealValues().getBoltHealing().forEachValue(floatModifiable ->
                        floatModifiable.addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "Spec Boost", -waterBoltStatsDecreasePercent / 100)
                );
                waterBolt.getDirectHitMultiplier().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost", waterBoltDirectHitHealingIncreasePercent);
            });
        }

        @EventHandler
        public void onDamageHealFinalEvent(WarlordsDamageHealingFinalEvent event) {
            if (!event.getSource().equals(warlordsEntity)) {
                return;
            }
            if (!(event.getAbility() instanceof WaterBolt waterBolt)) {
                return;
            }
            EnumSet<InstanceFlags> flags = event.getInstanceFlags();
            if (!flags.contains(InstanceFlags.DIRECT_HIT)) {
                return;
            }
            WarlordsEntity target = event.getWarlordsEntity();
            if (target.isTeammate(warlordsEntity)) {
                target.addSpeedModifier(warlordsEntity, getStringName(), waterBoltDirectHitSpeedIncreasePercent, waterBoltDirectHitDurationTicks);
            }
            warlordsEntity.addSpeedModifier(warlordsEntity, getStringName(), waterBoltDirectHitSpeedIncreasePercent, waterBoltDirectHitDurationTicks);
        }
    }
}
