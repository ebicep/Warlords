package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.CapacitorTotem;
import com.ebicep.warlords.abilities.LightningBolt;
import com.ebicep.warlords.abilities.LightningRod;
import com.ebicep.warlords.abilities.internal.AbstractPiercingProjectile;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityPlaceEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsProjectileFireEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.java.MathUtils;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;

import java.util.List;

public class EyeOfTheStorm implements SpecBoostManager.SpecBoost<EyeOfTheStorm> {

    private int maxTravelBlocks;
    private float velocityIncreasePercentage;
    private float splashRadiusBlocks;
    private float chainCooldownReductionIncrease;
    private float totemPlaceRangeHorizontal;
    private float totemPlaceRangeVertical;
    private float verticalCheckPlaceLimit;

    @Override
    public void init() {
        this.maxTravelBlocks = getValue("maxTravelBlocks", int.class);
        this.velocityIncreasePercentage = getValue("velocityIncreasePercentage", float.class);
        this.splashRadiusBlocks = getValue("splashRadiusBlocks", float.class);
        this.chainCooldownReductionIncrease = getValue("chainCooldownReductionIncrease", float.class);
        this.totemPlaceRangeHorizontal = getValue("totemPlaceRangeHorizontal", float.class);
        this.totemPlaceRangeVertical = getValue("totemPlaceRangeVertical", float.class);
        this.verticalCheckPlaceLimit = getValue("verticalCheckPlaceLimit", float.class);
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
                chainCooldownReductionIncrease,
                totemPlaceRangeHorizontal
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
                lightningBolt.getMaxDistance().addModifier(FloatModifiable.ModifierType.OVERRIDING, "Spec Boost", maxTravelBlocks);
                lightningBolt.getProjectileSpeed().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Spec Boost", (velocityIncreasePercentage + 100) / 100);
                lightningBolt.getSplashRadius().addModifier(FloatModifiable.ModifierType.OVERRIDING, "Spec Boost", splashRadiusBlocks);
                lightningBolt.setCooldownReduction(lightningBolt.getCooldownReduction() + chainCooldownReductionIncrease);
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
        public void onWarlordsTotemPlaceEvent(WarlordsAbilityPlaceEvent event) {
            if (!event.getWarlordsEntity().equals(warlordsEntity)) {
                return;
            }
            if (!(event.getAbility() instanceof CapacitorTotem)) {
                return;
            }
            Location location = warlordsEntity.getLocation();
            double maxDistance = MathUtils.calculateMaxDistance(Math.abs(location.getPitch()), totemPlaceRangeHorizontal, totemPlaceRangeVertical);
            Block targetBlock = Utils.getTargetBlock(warlordsEntity, (int) maxDistance);
            Location targetLocation = LocationUtils.getGroundLocation(targetBlock.getLocation()).add(.6, 0, .6);
            while (targetLocation.getBlock().getType() != Material.AIR) {
                targetLocation.setY(targetLocation.getY() + 1);
                if (targetLocation.getY() > location.getY() + verticalCheckPlaceLimit) {
                    break;
                }
            }
            if (targetLocation.getBlock().getType() != Material.AIR) {
                LocationBuilder newTargetLocation = new LocationBuilder(targetLocation).faceTowards(warlordsEntity.getLocation()).forward(1);
                while (newTargetLocation.getBlock().getType() != Material.AIR) {
                    newTargetLocation.forward(1);
                }
                targetLocation = LocationUtils.getGroundLocation(newTargetLocation);
            }
            event.getLocation().set(targetLocation.getBlockX() + .6, targetLocation.getBlockY(), targetLocation.getBlockZ() + .6);
        }

        @EventHandler(ignoreCancelled = true)
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!(cooldown.getCooldownObject() instanceof CapacitorTotem.CapacitorTotemData data) || !cooldown.getFrom().equals(warlordsEntity)) {
                return;
            }
            data.proc();
        }

    }

}