package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.HeartToHeart;
import com.ebicep.warlords.abilities.SoulSwitch;
import com.ebicep.warlords.abilities.internal.AbstractStrike;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsPlayerSwapEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;

import java.util.Collections;
import java.util.List;

public class RiftAmbush implements SpecBoostManager.SpecBoost<RiftAmbush> {

    private float soulSwitchRadiusIncrease;
    private float soulSwitchVerticalLimit;
    private float soulSwitchDamage;
    private int soulSwitchDamageReductionIncreasePercent;
    private float tetherRadius;
    private int tetherTickDuration;

    @Override
    public void init() {
        this.soulSwitchRadiusIncrease = getValue("soulSwitchRadiusIncrease", float.class);
        this.soulSwitchVerticalLimit = getValue("soulSwitchVerticalLimit", float.class);
        this.soulSwitchDamage = getValue("soulSwitchDamage", float.class);
        this.soulSwitchDamageReductionIncreasePercent = getValue("soulSwitchDamageReductionIncreasePercent", int.class);
        this.tetherRadius = getValue("tetherRadius", float.class);
        this.tetherTickDuration = getValue("tetherTickDuration", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "riftAmbush";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(soulSwitchDamage, soulSwitchRadiusIncrease, soulSwitchDamageReductionIncreasePercent, tetherRadius, tetherTickDuration);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public RiftAmbush get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(SoulSwitch.class).forEach(soulSwitch -> {
                soulSwitch.getHitBoxRadius().addAdditiveModifier("Spec Boost", soulSwitchRadiusIncrease);
                soulSwitch.setVerticalLimit(soulSwitchVerticalLimit);
                soulSwitch.setDamageReduction(soulSwitch.getDamageReduction() + soulSwitchDamageReductionIncreasePercent);
            });
        }

        @EventHandler
        public void onWarlordsPlayerSwapEvent(WarlordsPlayerSwapEvent event) {
            if (!event.getWarlordsEntity().equals(warlordsEntity)) {
                return;
            }
            WarlordsEntity swappedPlayer = event.getSwappedPlayer();
            Location swappedPlayerLocation = swappedPlayer.getLocation();
            Location chainLocation = swappedPlayer.getLocation().clone().add(0, -2, 0);
            event.getStart().set(swappedPlayerLocation.getX(), swappedPlayerLocation.getY(), swappedPlayerLocation.getZ());
            swappedPlayer.addInstance(InstanceBuilder
                    .damage()
                    .cause(getStringName())
                    .source(warlordsEntity)
                    .value(soulSwitchDamage)
                    .flags(InstanceFlags.TRUE_DAMAGE)
            );
            AbstractStrike.giveStrikePriority(warlordsEntity, swappedPlayer, tetherTickDuration);
            float radius = tetherRadius * tetherRadius;
            warlordsEntity.getCooldownManager().addCooldown(new RegularCooldown<>(
                    getStringName(),
                    "TETHER",
                    Boost.class,
                    null,
                    warlordsEntity,
                    CooldownTypes.TRUE_DEBUFF,
                    cooldownManager -> {
                    },
                    tetherTickDuration,
                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        if (ticksElapsed % 2 == 0) {
                            Location playerLocation = swappedPlayer.getLocation();
                            EffectUtils.playChainAnimation(playerLocation.clone().add(0, .5, 0), chainLocation, HeartToHeart.ITEM_STACK, 2);
                            EffectUtils.playCylinderAnimation(swappedPlayerLocation, tetherRadius, Particle.INFESTED, 30, 3, 1);
                            if (playerLocation.distanceSquared(swappedPlayerLocation) >= radius) {
                                LocationBuilder newLocation = new LocationBuilder(playerLocation)
                                        .faceTowards(swappedPlayerLocation)
                                        .forward(1);
                                swappedPlayer.teleportLocationOnly(newLocation);
                            }
                        }
                    })
            ));
        }

    }

}
