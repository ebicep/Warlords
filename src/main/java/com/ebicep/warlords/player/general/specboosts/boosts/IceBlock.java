package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.IceBarrier;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsPlayerHorseEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.motionsystem.MotionModifierBuilder;
import com.ebicep.warlords.player.ingame.motionsystem.speed.OverrideValueModifier;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class IceBlock implements SpecBoostManager.SpecBoost<IceBlock> {

    private int iceBarrierNewDurationTicks;
    private int recastDurationDecreaseTicks;
    private float recastDamageReductionPercent;
    private float recastMovementSpeed;
    private float recastMovementSpeedFlag;
    private int iceBlockRecastDelayTicks;

    @Override
    public void init() {
        this.iceBarrierNewDurationTicks = getValue("iceBarrierNewDurationTicks", int.class);
        this.recastDurationDecreaseTicks = getValue("recastDurationDecreaseTicks", int.class);
        this.recastDamageReductionPercent = getValue("recastDamageReductionPercent", float.class);
        this.recastMovementSpeed = getValue("recastMovementSpeed", float.class);
        this.recastMovementSpeedFlag = getValue("recastMovementSpeedFlag", float.class);
        this.iceBlockRecastDelayTicks = getValue("iceBlockRecastDelayTicks", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "iceBlock";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                iceBarrierNewDurationTicks,
                iceBlockRecastDelayTicks,
                recastDurationDecreaseTicks,
                recastDamageReductionPercent
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public IceBlock get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(IceBarrier.class).forEach(iceBarrier -> {
                iceBarrier.setTickDuration(iceBarrier.getTickDuration() + iceBarrierNewDurationTicks);
            });
        }


        @EventHandler(ignoreCancelled = true)
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!(cooldown instanceof RegularCooldown<?> regularCooldown) ||
                    !(cooldown.getCooldownObject() instanceof IceBarrier.IceBarrierData iceBarrierData) ||
                    !cooldown.getFrom().equals(warlordsEntity)
            ) {
                return;
            }
            AtomicReference<RegularCooldown<?>> blockCooldown = new AtomicReference<>(null);
            iceBarrierData.getIceBarrier().addSecondaryAbility(
                    iceBlockRecastDelayTicks,
                    () -> {
                        double previousJumpStrength;
                        if (warlordsEntity.getEntity() instanceof Player player) {
                            previousJumpStrength = player.getAttribute(Attribute.JUMP_STRENGTH).getBaseValue();
                            player.getAttribute(Attribute.JUMP_STRENGTH).setBaseValue(0);
                        } else {
                            previousJumpStrength = 0.42;
                        }
                        BoundingBox boundingBox = warlordsEntity.getEntity().getBoundingBox();
                        double x = boundingBox.getMaxX() - boundingBox.getMinX();
                        double y = boundingBox.getMaxY() - boundingBox.getMinY();
                        double z = boundingBox.getMaxZ() - boundingBox.getMinZ();
                        float scale = 1.2f;
                        Location location = new LocationBuilder(warlordsEntity.getLocation())
                                .yaw(0)
                                .pitch(0)
                                .subtract(
                                        boundingBox.getWidthX() / 2 + (boundingBox.getWidthX() * scale - boundingBox.getWidthX()) / 2,
                                        0,
                                        boundingBox.getWidthZ() / 2 + (boundingBox.getWidthZ() * scale - boundingBox.getWidthZ()) / 2
                                );
                        BlockDisplay display = warlordsEntity.getWorld().spawn(location, BlockDisplay.class, d -> {
                                    d.setBlock(Material.ICE.createBlockData());
                                    d.setTransformation(new Transformation(
                                                    new Vector3f(),
                                                    new AxisAngle4f(),
                                                    new Vector3f((float) x * scale, (float) y * scale, (float) z * scale),
                                                    new AxisAngle4f()
                                            )
                                    );
                                    d.setTeleportDuration(3);
                                }
                        );
                        RegularCooldown<Boost> cd = new RegularCooldown<>(
                                getStringName(),
                                "BLOCK",
                                Boost.class,
                                null,
                                warlordsEntity,
                                CooldownTypes.SPEC_BOOST,
                                cooldownManager -> {},
                                cooldownManager -> {
                                    display.remove();
                                    if (warlordsEntity.getEntity() instanceof Player player) {
                                        player.getAttribute(Attribute.JUMP_STRENGTH).setBaseValue(previousJumpStrength);
                                    }
                                },
                                regularCooldown.getTicksLeft() - recastDurationDecreaseTicks,
                                Collections.singletonList((c, ticksLeft, ticksElapsed) -> {
                                    display.teleport(new LocationBuilder(warlordsEntity.getLocation())
                                            .yaw(0)
                                            .pitch(0)
                                            .subtract(
                                                    boundingBox.getWidthX() / 2 + (boundingBox.getWidthX() * scale - boundingBox.getWidthX()) / 2,
                                                    0,
                                                    boundingBox.getWidthZ() / 2 + (boundingBox.getWidthZ() * scale - boundingBox.getWidthZ()) / 2
                                            ));
                                })
                        ) {
                            @Override
                            protected Listener getListener() {
                                return new Listener() {
                                    @EventHandler
                                    public void onWarlordsPlayerHorseEvent(WarlordsPlayerHorseEvent event) {
                                        if (event.getWarlordsEntity().equals(warlordsEntity)) {
                                            event.setCancelled(true);
                                        }
                                    }
                                };
                            }

                            @Override
                            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                return currentDamageValue * AbstractAbility.convertToDivisionDecimal(recastDamageReductionPercent);
                            }
                        };
                        blockCooldown.set(cd);
                        warlordsEntity.getCooldownManager().removeCooldown(regularCooldown);
                        warlordsEntity.addKnockbackModifier(warlordsEntity, getStringName(), -100, cd);
                        warlordsEntity.getCooldownManager().addCooldown(cd);
                        warlordsEntity.addSpeedModifier(new MotionModifierBuilder()
                                .setFrom(warlordsEntity)
                                .setName(getStringName())
                                .setModifier(0)
                                .linkToCooldown(warlordsEntity, cd)
                                .addAddons(new OverrideValueModifier(warlordsEntity.hasFlag() ? recastMovementSpeedFlag : recastMovementSpeed))
                                .build()
                        );
                    },
                    false,
                    secondaryAbility ->
                            (blockCooldown.get() == null && (!warlordsEntity.getCooldownManager().hasCooldown(regularCooldown) ||
                                    regularCooldown.getTicksLeft() <= recastDurationDecreaseTicks + 3)) || blockCooldown.get() != null
            );
        }

    }

}
