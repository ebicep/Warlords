package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.Inferno;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import java.util.Collections;
import org.bukkit.event.EventHandler;

import java.util.List;

public class InfernoExplosion implements SpecBoostManager.SpecBoost<InfernoExplosion>{

    private int infernoDurationReductionTicks;
    private float infernoSpeedPercent;
    private float healthLossPercent;
    private float explosionDamageBase;
    private float healthToDamagePercent;
    private int explosionRadius;
    private float damageReductionPercent;
    private int damageReductionDurationTicks;
    private int explosionCritChance; // +30 from inferno bonus
    private int explosionCritMultiplier; // +30 from inferno bonus

    @Override
    public void init() {
        this.infernoDurationReductionTicks = getValue("infernoDurationReductionTicks", int.class);
        this.infernoSpeedPercent = getValue("infernoSpeedPercent", float.class);
        this.healthLossPercent = getValue("healthLossPercent", float.class);
        this.explosionDamageBase = getValue("explosionDamageBase", float.class);
        this.healthToDamagePercent = getValue("healthToDamagePercent", float.class);
        this.explosionRadius = getValue("explosionRadius", int.class);
        this.damageReductionPercent = getValue("damageReductionPercent", float.class);
        this.damageReductionDurationTicks = getValue("damageReductionDurationTicks", int.class);
        this.explosionCritChance = getValue("explosionCritChance", int.class);
        this.explosionCritMultiplier = getValue("explosionCritMultiplier", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "infernoExplosion";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(infernoDurationReductionTicks, infernoSpeedPercent, healthLossPercent, explosionDamageBase, healthToDamagePercent, explosionRadius, damageReductionPercent, damageReductionDurationTicks, explosionCritChance);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public InfernoExplosion get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        private Inferno infernoAbility;

        @Override
        public void apply(WarlordsPlayer warlordsplayer) {
            this.warlordsEntity = warlordsplayer;
            warlordsplayer.getAbilitiesMatching(Inferno.class).forEach(inferno -> {
                this.infernoAbility = inferno;
                inferno.setTickDuration(inferno.getTickDuration() - infernoDurationReductionTicks);
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!(cooldown instanceof RegularCooldown<?> regularCooldown)) {
                return;
            }
            if (!cooldown.getCooldownClass().equals(Inferno.class)) {
                return;
            }
            warlordsEntity.addSpeedModifier(warlordsEntity, cooldown.getName(), infernoSpeedPercent, regularCooldown.getTicksLeft());
            regularCooldown.setOnRemove((cooldownManager) -> triggerExplosion());
        }

        private void triggerExplosion() {
            if (warlordsEntity == null) return;

            float healthLost = warlordsEntity.getCurrentHealth() * healthLossPercent / 100;
            float explosionDamage = explosionDamageBase + healthLost * healthToDamagePercent / 100;

            warlordsEntity.addInstance(InstanceBuilder
                    .fall()
                    .source(warlordsEntity)
                    .value(healthLost)
            );
            for (WarlordsEntity target : PlayerFilter
                    .entitiesAround(warlordsEntity, explosionRadius, explosionRadius, explosionRadius)
                    .aliveEnemiesOf(warlordsEntity)
            ) {
                target.addInstance(InstanceBuilder
                        .damage()
                        .ability(infernoAbility)
                        .source(warlordsEntity)
                        .value(explosionDamage)
                        .critChance(explosionCritChance)
                        .critMultiplier(explosionCritMultiplier)
                );
            }
            warlordsEntity.getCooldownManager().addCooldown(new RegularCooldown<>(
                    "Inferno Shield", null, InfernoExplosion.class, null, warlordsEntity,
                    CooldownTypes.BUFF,
                    cooldownManager -> {},
                    damageReductionDurationTicks,
                    Collections.emptyList()
            ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE , (event, currentDamageValue) -> {
                currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, "Inferno Shield", 1 - damageReductionPercent / 100);
            }));
        }
    }
}
