package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.ChainLightning;
import com.ebicep.warlords.abilities.LightningBolt;
import com.ebicep.warlords.abilities.LightningRod;
import com.ebicep.warlords.abilities.internal.AbstractPiercingProjectile;
import com.ebicep.warlords.events.player.ingame.WarlordsProjectileFireEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsTotemPlaceEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.java.MathUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;

import java.util.List;

public class EyeOfTheStorm implements SpecBoostManager.SpecBoost<EyeOfTheStorm> {

    private int maxTravelBlocks;
    private float velocityIncreasePercentage;
    private float splashRadiusBlocks;
    private float damageResistancePercentFirstHit;
    private float maxDamageResistancePercent;
    private float totemPlaceRangeHorizontal;
    private float totemPlaceRangeVertical;

    @Override
    public void init() {
        this.maxTravelBlocks = getValue("maxTravelBlocks", int.class);
        this.velocityIncreasePercentage = getValue("velocityIncreasePercentage", float.class);
        this.splashRadiusBlocks = getValue("splashRadiusBlocks", float.class);
        this.damageResistancePercentFirstHit = getValue("damageResistancePercentFirstHit", float.class);
        this.maxDamageResistancePercent = getValue("maxDamageResistancePercent", float.class);
        this.totemPlaceRangeHorizontal = getValue("totemPlaceRangeHorizontal", float.class);
        this.totemPlaceRangeVertical = getValue("totemPlaceRangeVertical", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "eyeOfTheStorm";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                maxTravelBlocks,
                velocityIncreasePercentage,
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
                lightningBolt.getProjectileSpeed().addMultiplicativeModifierAdd("Spec Boost", (velocityIncreasePercentage + 100) / 100);
                lightningBolt.getHitbox().addOverridingModifier("Spec Boost", splashRadiusBlocks);
            });
            warlordsPlayer.getAbilitiesMatching(ChainLightning.class).forEach(chainLightning -> {
                chainLightning.getDamageReductionPerBounce().addOverridingModifier("Spec Boost", damageResistancePercentFirstHit);
                chainLightning.getMaxDamageReduction().addOverridingModifier("Spec Boost", maxDamageResistancePercent);
            });
            warlordsPlayer.getAbilitiesMatching(LightningRod.class).forEach(lightningRod -> {
                lightningRod.setHorizontalTotemProcRange(Float.MAX_VALUE);
                lightningRod.setVerticalTotemProcRange(Float.MAX_VALUE);
            });
        }

        @EventHandler(ignoreCancelled = true)
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

        @EventHandler
        public void onWarlordsTotemPlaceEvent(WarlordsTotemPlaceEvent event) {
            if (!event.getWarlordsEntity().equals(warlordsEntity)) {
                return;
            }
            Location location = warlordsEntity.getLocation();
            double maxDistance = MathUtils.calculateMaxDistance(Math.abs(location.getPitch()), totemPlaceRangeHorizontal, totemPlaceRangeVertical);
            Block targetBlock = Utils.getTargetBlock(warlordsEntity, (int) maxDistance);
            if (targetBlock.getType() == Material.AIR) {
                event.setCancelled(true);
                return;
            }
            Location blockLocation = targetBlock.getLocation().clone().add(.6, 0, .6).clone();
            while (blockLocation.getBlock().getType() != Material.AIR) {
                blockLocation.setY(blockLocation.getY() + 1);
                if (blockLocation.getY() > location.getY() + totemPlaceRangeVertical) {
                    break;
                }
            }
            event.getLocation().set(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ());
        }

    }

}