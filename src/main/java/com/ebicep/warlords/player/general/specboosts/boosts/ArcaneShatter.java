package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Shield;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

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
            warlordsPlayer.getEnergyPerHit().addAdditiveModifier("Spec Boost", energyPerMelee);
            warlordsPlayer.getHealth().addAdditiveModifier("Spec Boost (Base)", healthIncrease);
        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getHealth().removeModifier("Spec Boost (Base)");
        }

        @EventHandler(ignoreCancelled = true)
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!Objects.equals(cooldown.getName(), "Arcane Shield") || !(cooldown.getCooldownObject() instanceof Shield shield) || !cooldown.getFrom().equals(warlordsEntity)) {
                return;
            }
            Consumer<CooldownManager> oldOnRemove = cooldown.getOnRemove();
            cooldown.setOnRemove(cooldownManager -> {
                oldOnRemove.accept(cooldownManager);
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
                        cM -> {},
                        stunTicks
                ) {
                    @Override
                    public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        return currentDamageValue * AbstractAbility.convertToMultiplicationDecimal(damageIncrease);
                    }
                });
            });
        }


    }

}
