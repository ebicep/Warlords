package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.ArcaneShield;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.event.EventHandler;

import java.util.List;

public class ArcaneShatter implements SpecBoostManager.SpecBoost<ArcaneShatter> {

    private float energyPerMelee;
    private float healthIncrease;
    private float damageIncrease;
    private float range;
    private int stunTicks;

    @Override
    public void init() {
        this.energyPerMelee = getValue("energyPerMelee", float.class);
        this.healthIncrease = getValue("healthIncrease", float.class);
        this.damageIncrease = getValue("damageIncrease", float.class);
        this.range = getValue("range", float.class);
        this.stunTicks = getValue("stunTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "arcaneShatter";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(energyPerMelee, healthIncrease, damageIncrease, range, stunTicks);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public ArcaneShatter get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            AbstractPlayerClass playerClass = warlordsPlayer.getSpec();
            playerClass.setEnergyPerHit(playerClass.getEnergyPerHit() + energyPerMelee);
            warlordsPlayer.getHealth().addAdditiveModifier("Spec Boost (Base)", healthIncrease);
        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {
            AbstractPlayerClass playerClass = warlordsPlayer.getSpec();
//            playerClass.setEnergyPerHit(playerClass.getEnergyPerHit() - energyPerMelee); // TODO
            warlordsPlayer.getHealth().removeModifier("Spec Boost (Base)");
        }

        @EventHandler
        public void onWarlordsArcaneShieldBrokenEvent(ArcaneShield.WarlordsArcaneShieldBrokenEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            PlayerFilter.entitiesAround(warlordsEntity, range, range, range)
                        .aliveEnemiesOf(warlordsEntity)
                        .forEach(we -> we.setStunTicks(stunTicks));
            warlordsEntity.getCooldownManager().addCooldown(new RegularCooldown<>(
                    getStringName(),
                    null,
                    Boost.class,
                    null,
                    warlordsEntity,
                    CooldownTypes.SPEC_BOOST,
                    cooldownManager -> {},
                    stunTicks
            ) {
                @Override
                public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * AbstractAbility.convertToMultiplicationDecimal(damageIncrease);
                }
            });
        }

    }

}
