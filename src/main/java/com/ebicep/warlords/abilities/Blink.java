package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.java.MathUtils;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Blink extends AbstractAbility implements BlueAbilityIcon, Heals<Blink.HealingValues>, HitBox, AbilityStats<Blink, Blink.BlinkStats> {

    private final BlinkStats stats = new BlinkStats();
    private final HealingValues healingValues = new HealingValues();
    private FloatModifiable radius = new FloatModifiable(13);
    private FloatModifiable radiusFlag = new FloatModifiable(3.5f);
    private float verticalLimit;
    private float verticalLimitFlag;
    private int damageReduction;
    private int damageReductionTickDuration;
    private float maxGroundTeleportDistance;

    public Blink() {
        super(AbstractAbilityBuilder.create("blink").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.radius = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("radius"), float.class));
        this.radiusFlag = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("radiusFlag"), float.class));
        this.verticalLimit = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("verticalLimit"), float.class);
        this.verticalLimitFlag = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("verticalLimitFlag"), float.class);
        this.damageReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageReduction"), int.class);
        this.damageReductionTickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageReductionTickDuration"), int.class);
        this.maxGroundTeleportDistance = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxGroundTeleportDistance"), float.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        wp.addInstance(InstanceBuilder.healing().ability(this).source(wp).value(healingValues.blinkHealing));
        wp.setRegenTickTimer(1);
        Location startLocation = wp.getEyeLocation();
        LocationBuilder locationBuilder = new LocationBuilder(startLocation);
        if (locationBuilder.getWorld().getBlockAt(locationBuilder.getBlockX(), locationBuilder.getBlockY() + 1, locationBuilder.getBlockZ()).getType() != Material.AIR) {
            locationBuilder.pitch(0);
        }
        float maxHorizontal = wp.hasFlag() ? radiusFlag.getCalculatedValue() : radius.getCalculatedValue();
        float maxVertical = wp.hasFlag() ? verticalLimitFlag : verticalLimit;
        int maxDistance = (int) MathUtils.calculateMaxDistance(Math.abs(locationBuilder.getPitch()), maxHorizontal, maxVertical) - 1;
        LocationBuilder endLocation = new LocationBuilder(locationBuilder).forward(maxDistance);
//        EffectUtils.displayParticle(Particle.HAPPY_VILLAGER, endLocation, 10, 0, 0, 0, 0);
        for (Block ignored : Utils.getTargetBlockInBetween(locationBuilder, maxDistance)) {
            if (!Utils.getTargetBlock(locationBuilder, 1).getType().isAir() ||
                    !locationBuilder.getBlock().getType().isAir()
                    || locationBuilder.distanceSquared(startLocation) > startLocation.distanceSquared(endLocation)
                // || !locationBuilder.clone().addY(1).getBlock().getType().isAir()
            ) {
                locationBuilder.centerXZBlock();
                boolean isSlab = locationBuilder.clone().addY(-1).getBlock().getBlockData() instanceof Slab;
                locationBuilder.addY(isSlab ? -0.5 : 0);
                break;
            }
            locationBuilder = locationBuilder.forward(1);
            EffectUtils.displayParticle(Particle.SMOKE, locationBuilder.clone().addY(-.5), 10, .1, .1, .1, 0);
        }
        Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 2, 1.5f);
        Location floorLocation = LocationUtils.getGroundLocation(locationBuilder.clone());
        wp.teleportLocationOnly(locationBuilder.getY() - floorLocation.getY() > maxGroundTeleportDistance ? locationBuilder : floorLocation);

        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Soul Switch Res",
                "SWITCH",
                SoulSwitch.class,
                null,
                wp,
                CooldownTypes.BUFF,
                cooldownManager -> {},
                damageReductionTickDuration
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * convertToDivisionDecimal(damageReduction);
            }
        });
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Teleport ")
                .blocks(radius)
                .text(" blocks forward and gain ")
                .percent(damageReduction, AbilityDescriptionBuilder.COLOR_BROWN)
                .text(" damage reduction for ")
                .durationTicks(damageReductionTickDuration)
                .text(". Heal for ")
                .heal(healingValues.blinkHealing)
                .text(" health and instantly active your passive regeneration.")
                .emptyLine()
                .text(" Blink has low vertical range.")
                .build();

    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return radius;
    }

    @Override
    public BlinkStats getAbilityStats() {
        return stats;
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    public int getDamageReduction() {
        return damageReduction;
    }

    public void setDamageReduction(int damageReduction) {
        this.damageReduction = damageReduction;
    }

    public float getVerticalLimit() {
        return verticalLimit;
    }

    public void setVerticalLimit(float verticalLimit) {
        this.verticalLimit = verticalLimit;
    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.SetValue blinkHealing = new Value.SetValue(0);

        private List<Value> values = List.of(blinkHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.blinkHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameHealing("blinkHealing"), Value.SetValue.class);
            this.values = List.of(blinkHealing);
        }

        public Value.SetValue getBlinkHealing() {
            return blinkHealing;
        }

    }

    public static class BlinkStats extends AbstractAbilityStats<Blink, BlinkStats> {

        @Override
        public Class<BlinkStats> getClazz() {
            return BlinkStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public BlinkStats merge(BlinkStats other, int multiplier) {
            BlinkStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public BlinkStats create() {
            return new BlinkStats();
        }

    }

}
