package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.WindfuryWeapon;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsAddSpeedModifierEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class SymphonicWindfury implements SpecBoostManager.SpecBoost<SymphonicWindfury> {

    private int windfuryExtraGuaranteedHits;
    private float speedIncreasePercent;
    private int speedDurationTicks;

    @Override
    public void init() {
        this.windfuryExtraGuaranteedHits = getValue("windfuryExtraGuaranteedHits", int.class);
        this.speedIncreasePercent = getValue("speedIncreasePercent", float.class);
        this.speedDurationTicks = getValue("speedDurationTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "symphonicWindfury";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(windfuryExtraGuaranteedHits, speedIncreasePercent, speedDurationTicks);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public SymphonicWindfury get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(WindfuryWeapon.class).forEach(windfuryWeapon -> {
                windfuryWeapon.setGuaranteedHits(windfuryWeapon.getGuaranteedHits() + windfuryExtraGuaranteedHits);
            });
        }

        @EventHandler
        public void onWarlordsAbilityActivate(WarlordsAbilityActivateEvent.Post event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            if (event.getAbility() instanceof WindfuryWeapon) {
                warlordsEntity.getCooldownManager().addCooldown(new RegularCooldown<>(
                        getStringName(),
                        "SYM",
                        Boost.class,
                        null,
                        warlordsEntity,
                        CooldownTypes.SPEC_BOOST,
                        cooldownManager -> {},
                        speedDurationTicks
                ) {
                    @Override
                    protected Listener getListener() {
                        return new Listener() {
                            @EventHandler
                            public void onAddSpeed(WarlordsAddSpeedModifierEvent event) {
                                if (event.getWarlordsEntity() != warlordsEntity) {
                                    return;
                                }
                                if (event.getModifier().getModifier() < 0) {
                                    event.setCancelled(true);
                                }
                            }
                        };
                    }
                });
                warlordsEntity.addKnockbackModifier(warlordsEntity, getStringName(), -100, speedDurationTicks);
                warlordsEntity.getSpeed().removeNegativeModifiers();
                warlordsEntity.addSpeedModifier(warlordsEntity, getStringName(), speedIncreasePercent, speedDurationTicks);
            }
        }

        @EventHandler
        public void onDamageHealEvent(WarlordsDamageHealingEvent event) {
            if (!event.getSource().equals(warlordsEntity)) {
                return;
            }
            if (!(event.getAbility() instanceof WindfuryWeapon windfuryWeapon)) {
                return;
            }
            event.getFlags().add(InstanceFlags.IGNORE_SELF_RES);
        }

    }

}
