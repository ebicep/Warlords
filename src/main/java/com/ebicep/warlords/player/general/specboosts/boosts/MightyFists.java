package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.Berserk;
import com.ebicep.warlords.abilities.WoundingStrikeBerserker;
import com.ebicep.warlords.abilities.internal.WoundingCooldown;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsPlayerWoundedEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.bukkit.event.EventHandler;

import java.util.List;

public class MightyFists implements SpecBoostManager.SpecBoost<MightyFists> {

    private float woundingIncreasePercent;
    private float consecutiveStrikeWoundingIncreasePercent;
    private float maxConsecutiveStrikeWoundingIncreasePercent;
    private float seismicWaveGroundSlamWoundingPercent;
    private int seismicWaveGroundSlamWoundingDurationTicks;

    @Override
    public void init() {
        this.woundingIncreasePercent = getValue("woundingIncreasePercent", float.class);
        this.consecutiveStrikeWoundingIncreasePercent = getValue("consecutiveStrikeWoundingIncreasePercent", float.class);
        this.maxConsecutiveStrikeWoundingIncreasePercent = getValue("maxConsecutiveStrikeWoundingIncreasePercent", float.class);
        this.seismicWaveGroundSlamWoundingPercent = getValue("seismicWaveGroundSlamWoundingPercent", float.class);
        this.seismicWaveGroundSlamWoundingDurationTicks = getValue("seismicWaveGroundSlamWoundingDurationTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "mightyFists";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(woundingIncreasePercent,
                consecutiveStrikeWoundingIncreasePercent,
                maxConsecutiveStrikeWoundingIncreasePercent,
                seismicWaveGroundSlamWoundingPercent,
                seismicWaveGroundSlamWoundingDurationTicks
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public MightyFists get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;

            warlordsPlayer.getAbilitiesMatching(WoundingStrikeBerserker.class).forEach(woundingStrike -> {
                woundingStrike.getWounding().addAdditiveModifier("Spec Boost", woundingIncreasePercent);
            });
        }

        @EventHandler
        public void onDamageHealFinalEvent(WarlordsDamageHealingFinalEvent event) {
            if (!event.getSource().equals(warlordsEntity)) {
                return;
            }
            if (!event.getCause().equals("Seismic Wave") && !event.getAbility().getName().equals("Ground Slam")) {
                return;
            }
            if (!warlordsEntity.getCooldownManager().hasCooldown(Berserk.class)) {
                return;
            }
            WarlordsEntity target = event.getWarlordsEntity();
            WoundingCooldown.addWoundingCooldown(
                    target,
                    getStringName(),
                    warlordsEntity,
                    seismicWaveGroundSlamWoundingPercent,
                    seismicWaveGroundSlamWoundingDurationTicks
            );
        }

        @EventHandler
        public void onPlayerWoundedEvent(WarlordsPlayerWoundedEvent event) {
            if (!event.getFrom().equals(warlordsEntity)) {
                return;
            }
            if (!event.getName().equals("Wounding Strike")) {
                return;
            }
            WoundingCooldown woundingCooldown = event.getWoundingCooldown();
            if (woundingCooldown == null) {
                return;
            }
            WoundingCooldown.WoundingData woundingData = woundingCooldown.getCooldownObject();
            for (WoundingCooldown.WoundingData.WoundingInstance instance : woundingData.instances()) {
                if (instance.getFrom().equals(warlordsEntity) && instance.getName().equals("Wounding Strike")) {
                    instance.setAmount(Math.min(maxConsecutiveStrikeWoundingIncreasePercent, instance.getAmount() + consecutiveStrikeWoundingIncreasePercent));
                    instance.setTicksLeft(event.getTickDuration());
                    woundingCooldown.updateTicksLeft();
                    event.setCancelled(true);
                    return;
                }
            }
        }

    }

}

