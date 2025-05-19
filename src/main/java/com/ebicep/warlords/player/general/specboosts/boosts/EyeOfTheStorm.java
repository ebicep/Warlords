package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.ChainLightning;
import com.ebicep.warlords.abilities.LightningBolt;
import com.ebicep.warlords.abilities.internal.AbstractPiercingProjectile;
import com.ebicep.warlords.events.player.ingame.WarlordsProjectileFireEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.bukkit.event.EventHandler;

import java.util.List;

public class EyeOfTheStorm implements SpecBoostManager.SpecBoost<EyeOfTheStorm> {

    private int maxTravelBlocks;
    private float velocityMultiplier;
    private float splashRadiusBlocks;
    private float damageResistancePercentFirstHit;
    private float maxDamageResistancePercent;

    @Override
    public void init() {
        this.maxTravelBlocks = getValue("maxTravelBlocks", int.class);
        this.velocityMultiplier = getValue("velocityMultiplier", float.class);
        this.splashRadiusBlocks = getValue("splashRadiusBlocks", float.class);
        this.damageResistancePercentFirstHit = getValue("damageResistancePercentFirstHit", float.class);
        this.maxDamageResistancePercent = getValue("maxDamageResistancePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "eyeOfTheStorm";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                maxTravelBlocks,
                velocityMultiplier,
                splashRadiusBlocks,
                damageResistancePercentFirstHit,
                maxDamageResistancePercent
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public EyeOfTheStorm get() {
        return this;
    }


    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;


        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(LightningBolt.class).forEach(lightningBolt -> {
                lightningBolt.getMaxDistance().addOverridingModifier("Spec Boost", maxTravelBlocks);
                lightningBolt.getProjectileSpeed().addMultiplicativeModifierMult("Spec Boost", velocityMultiplier);
                lightningBolt.getHitbox().addOverridingModifier("Spec Boost", splashRadiusBlocks);
            });
            warlordsPlayer.getAbilitiesMatching(ChainLightning.class).forEach(chainLightning -> {
                chainLightning.getDamageReductionPerBounce().addOverridingModifier("Spec Boost", damageResistancePercentFirstHit);
                chainLightning.getMaxDamageReduction().addOverridingModifier("Spec Boost", maxDamageResistancePercent);
            });
        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(LightningBolt.class).forEach(lightningBolt -> {
                lightningBolt.getMaxDistance().removeModifier("Spec Boost");
                lightningBolt.getProjectileSpeed().removeModifier("Spec Boost");
                lightningBolt.getHitbox().removeModifier("Spec Boost");
            });
            warlordsPlayer.getAbilitiesMatching(ChainLightning.class).forEach(chainLightning -> {
                chainLightning.getDamageReductionPerBounce().removeModifier("Spec Boost");
                chainLightning.getMaxDamageReduction().removeModifier("Spec Boost");
            });
        }

        @EventHandler
        public void onWarlordsProjectileFireEvent(WarlordsProjectileFireEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            if (!(event.getAbility() instanceof LightningBolt lightningBolt)) {
                return;
            }
            List<? extends AbstractPiercingProjectile<?, ?>.InternalProjectile> internalProjectiles = event.getInternalProjectiles();
            for (AbstractPiercingProjectile<?, ?>.InternalProjectile internalProjectile : internalProjectiles) {
                internalProjectile.addTask(new AbstractPiercingProjectile.InternalProjectileTask() {
                    @Override
                    public void run(AbstractPiercingProjectile<?, ?>.InternalProjectile projectile) {
                    }

                    @Override
                    public void onDestroy(AbstractPiercingProjectile<?, ?>.InternalProjectile projectile) {
                        lightningBolt.explode((AbstractPiercingProjectile.InternalProjectile) internalProjectile, warlordsEntity);
                    }
                });
            }
        }

    }

}