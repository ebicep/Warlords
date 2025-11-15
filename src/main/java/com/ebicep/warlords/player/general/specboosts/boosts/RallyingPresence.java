package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.CrusadersStrike;
import com.ebicep.warlords.abilities.InspiringPresence;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.game.WarlordsFlagUpdatedEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.game.flags.PlayerFlagLocation;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.player.ingame.motionsystem.MotionModifierBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class RallyingPresence implements SpecBoostManager.SpecBoost<RallyingPresence> {

    private int energyPerSecondIncrease;
    private int speedIncreasePercent;
    private float inspiringPresenceDamageReductionPercent;
    private float flagSpeedIncreasePercent;
    private float flagKnockbackResistancePercent;
    private int crusaderStrikeEnergyIncrease;

    @Override
    public void init() {
        this.energyPerSecondIncrease = getValue("energyPerSecondIncrease", int.class);
        this.speedIncreasePercent = getValue("speedIncreasePercent", int.class);
        this.inspiringPresenceDamageReductionPercent = getValue("inspiringPresenceDamageReductionPercent", float.class);
        this.flagSpeedIncreasePercent = getValue("flagSpeedIncreasePercent", float.class);
        this.flagKnockbackResistancePercent = getValue("flagKnockbackResistancePercent", float.class);
        this.crusaderStrikeEnergyIncrease = getValue("crusaderStrikeEnergyIncrease", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "rallyingPresence";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                energyPerSecondIncrease,
                speedIncreasePercent,
                inspiringPresenceDamageReductionPercent,
                flagSpeedIncreasePercent,
                flagKnockbackResistancePercent,
                crusaderStrikeEnergyIncrease);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public RallyingPresence get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(InspiringPresence.class).forEach(inspiringPresence -> {
                inspiringPresence.setEnergyPerSecond(inspiringPresence.getEnergyPerSecond() + energyPerSecondIncrease);
                inspiringPresence.setSpeedBuff(inspiringPresence.getSpeedBuff() + speedIncreasePercent);
            });
        }

        @EventHandler
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!(cooldown instanceof RegularCooldown<?> regularCooldown)) {
                return;
            }
            if (!cooldown.getCooldownClass().equals(InspiringPresence.InspiringPresenceData.class) || !cooldown.getFrom().equals(warlordsEntity)) {
                return;
            }
            regularCooldown.addModifier(Modifier.DAMAGE_AFTER_INTERVENE_SELF, (e, currentDamageValue) -> {
                        currentDamageValue.addMultiplicativeModifierMult(getStringName(), AbstractAbility.convertToDivisionDecimal(inspiringPresenceDamageReductionPercent));
                    }
            );
        }

        @EventHandler
        public void onWarlordsFlagUpdated(WarlordsFlagUpdatedEvent event) {
            if (event.getNew() instanceof PlayerFlagLocation playerFlagLocation && playerFlagLocation.getPlayer().equals(warlordsEntity)) {
                warlordsEntity.getKnockback().addModifier(new MotionModifierBuilder()
                        .setFrom(warlordsEntity)
                        .setName(getStringName())
                        .setModifier(-flagKnockbackResistancePercent)
                        .setDuration(-1)
                        .build()
                );
                warlordsEntity.addSpeedModifier(warlordsEntity, getStringName(), flagSpeedIncreasePercent, -1);
                warlordsEntity.getAbilitiesMatching(CrusadersStrike.class).forEach(crusadersStrike -> {
                    crusadersStrike.setEnergyGiven(crusadersStrike.getEnergyGiven() + crusaderStrikeEnergyIncrease);
                });
            } else if (event.getOld() instanceof PlayerFlagLocation playerFlagLocation && playerFlagLocation.getPlayer().equals(warlordsEntity)) {
                warlordsEntity.getKnockback().removeModifier(getStringName());
                warlordsEntity.getSpeed().removeModifier(getStringName());
                warlordsEntity.getAbilitiesMatching(CrusadersStrike.class).forEach(crusadersStrike -> {
                    crusadersStrike.setEnergyGiven(crusadersStrike.getEnergyGiven() - crusaderStrikeEnergyIncrease);
                });
            }
        }

    }

}
