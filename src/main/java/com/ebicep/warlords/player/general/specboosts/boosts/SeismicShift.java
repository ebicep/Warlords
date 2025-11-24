package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.SeismicWaveBerserker;
import com.ebicep.warlords.abilities.internal.WoundingCooldown;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.event.EventHandler;

import java.util.List;

public class SeismicShift implements SpecBoostManager.SpecBoost<SeismicShift> {

    private int seismicWaveCooldownReductionTicks;
    private int seismicWaveWoundingPercent;
    private int seismicWaveWoundingTickDuration;

    @Override
    public void init() {
        this.seismicWaveCooldownReductionTicks = getValue("seismicWaveCooldownReductionTicks", int.class);
        this.seismicWaveWoundingPercent = getValue("seismicWaveWoundingPercent", int.class);
        this.seismicWaveWoundingTickDuration = getValue("seismicWaveWoundingTickDuration", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "seismicShift";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                seismicWaveCooldownReductionTicks,
                seismicWaveWoundingPercent,
                seismicWaveWoundingTickDuration
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public SeismicShift get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(SeismicWaveBerserker.class).forEach(seismicWave -> {
                seismicWave.getCooldown().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost", -seismicWaveCooldownReductionTicks / 20f);
            });
        }

        @EventHandler
        public void onDamageHealFinalEvent(WarlordsDamageHealingFinalEvent event) {
            if (!event.getSource().equals(warlordsEntity)) {
                return;
            }
            if (!event.getCause().equals("Seismic Wave")) {
                return;
            }
            WarlordsEntity target = event.getWarlordsEntity();
            WoundingCooldown.addWoundingCooldown(
                    target,
                    getStringName(),
                    warlordsEntity,
                    seismicWaveWoundingPercent,
                    seismicWaveWoundingTickDuration
            );
        }


    }

}

