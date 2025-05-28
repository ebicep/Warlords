package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.Berserk;
import com.ebicep.warlords.abilities.WoundingStrikeBerserker;
import com.ebicep.warlords.abilities.internal.WoundingData;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import org.bukkit.event.EventHandler;

import java.util.List;

import static com.ebicep.warlords.abilities.internal.WoundingData.*;

public class MightyFists implements SpecBoostManager.SpecBoost<MightyFists> {

    private float woundingIncreasePercent;
    private float seismicWaveGroundSlamWoundingPercent;
    private int seismicWaveGroundSlamWoundingDurationTicks;

    @Override
    public void init() {
        this.woundingIncreasePercent = getValue("woundingIncreasePercent", float.class);
        this.seismicWaveGroundSlamWoundingPercent = getValue("seismicWaveGroundSlamWoundingPercent", float.class);
        this.seismicWaveGroundSlamWoundingDurationTicks = getValue("seismicWaveGroundSlamWoundingDurationTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "mightyFists";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(woundingIncreasePercent, seismicWaveGroundSlamWoundingPercent, seismicWaveGroundSlamWoundingDurationTicks);
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
            WoundingData data = new WoundingData(seismicWaveGroundSlamWoundingPercent);
            applyNewWoundingInit(target);
            target.getCooldownManager()
                  .addCooldown(new RegularCooldown<>(
                          getStringName(),
                          "WND",
                          WoundingData.class,
                          data,
                          warlordsEntity,
                          CooldownTypes.DEBUFF,
                          cooldownManager -> {},
                          cooldownManager -> sendWoundExpired(target),
                          seismicWaveGroundSlamWoundingDurationTicks
                  ) {

                      @Override
                      public float modifyHealingFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                          return currentHealValue * (100 - data.amount()) / 100f;
                      }

                      @Override
                      public PlayerNameData addSuffixFromOther() {
                          return getSuffixFromOther(warlordsEntity, target);
                      }
                  });
        }

    }

}

