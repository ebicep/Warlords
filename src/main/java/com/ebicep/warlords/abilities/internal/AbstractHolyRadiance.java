package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.HammerOfLight;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityTargetEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractHolyRadiance extends AbstractAbility implements BlueAbilityIcon, HitBox, AbilityStats<AbstractHolyRadiance, AbstractHolyRadiance.AbstractHolyRadianceStats> {

    private final FloatModifiable radius;
    private final AbstractHolyRadianceStats stats = new AbstractHolyRadianceStats();

    public AbstractHolyRadiance(
            String name,
            float cooldown,
            float energyCost,
            int radius
    ) {
        super(name, cooldown, energyCost);
        this.radius = new FloatModifiable(radius);
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        Value.RangedValueCritable radianceHealing = getRadianceHealing();
        wp.addInstance(InstanceBuilder
                .healing()
                .ability(this)
                .source(wp)
                .value(radianceHealing)
        );

        if (chain(wp)) {
            stats.playersMarked++;
        }

        float rad = radius.getCalculatedValue();
        Set<WarlordsEntity> warlordsEntities = PlayerFilter
                .entitiesAround(wp, rad, rad, rad)
                .aliveTeammatesOfExcludingSelf(wp)
                .stream()
                .collect(Collectors.toSet());
        for (WarlordsEntity radianceTarget : warlordsEntities) {
            new FlyingArmorStand(
                    wp.getGame(),
                    wp.getLocation(),
                    radianceTarget,
                    wp,
                    1.1,
                    radianceHealing
            ).runTaskTimer(1, 1);
        }
        Bukkit.getPluginManager().callEvent(new WarlordsAbilityTargetEvent.WarlordsBlueAbilityTargetEvent(wp, name, warlordsEntities));

        wp.playSound(wp.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        Utils.playGlobalSound(wp.getLocation(), "paladin.holyradiance.activation", 2, 1);

        Location particleLoc = wp.getLocation().add(0, 1.2, 0);

        particleLoc.getWorld().spawnParticle(
                Particle.VILLAGER_HAPPY,
                particleLoc,
                2,
                1,
                1,
                1,
                0.1,
                null,
                true
        );
        particleLoc.getWorld().spawnParticle(
                Particle.SPELL,
                particleLoc,
                12,
                1,
                1,
                1,
                0.06,
                null,
                true
        );

        return true;
    }

    public abstract Value.RangedValueCritable getRadianceHealing();

    public abstract boolean chain(WarlordsEntity wp);

    @Override
    public FloatModifiable getHitBoxRadius() {
        return radius;
    }

    @Override
    public AbstractHolyRadianceStats getAbilityStats() {
        return stats;
    }

    public static class AbstractHolyRadianceStats extends AbstractAbilityStats<AbstractHolyRadiance, AbstractHolyRadianceStats> {

        @Field("targets_healed")
        private int playersHealed = 0;
        @Field("targets_marked")
        private int playersMarked = 0;

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Healed", String.valueOf(playersHealed)));
            statsDisplay.add(new AbilityStatDisplay("Targets Marked", String.valueOf(playersMarked)));
            return statsDisplay;
        }

        @Override
        public AbstractHolyRadianceStats merge(AbstractHolyRadianceStats other, int multiplier) {
            AbstractHolyRadianceStats stats = super.merge(other, multiplier);
            stats.playersHealed = this.playersHealed + other.playersHealed * multiplier;
            stats.playersMarked = this.playersMarked + other.playersMarked * multiplier;
            return stats;
        }

        @Override
        public Class<AbstractHolyRadianceStats> getClazz() {
            return AbstractHolyRadianceStats.class;
        }

        @Override
        public AbstractHolyRadianceStats create() {
            return new AbstractHolyRadianceStats();
        }
    }

    public class FlyingArmorStand extends GameRunnable {

        private final WarlordsEntity target;
        private final WarlordsEntity owner;
        private final double speed;
        private final ArmorStand armorStand;
        private final Value.RangedValueCritable radianceHealing;

        public FlyingArmorStand(Game game, Location location, WarlordsEntity target, WarlordsEntity owner, double speed, Value.RangedValueCritable radianceHealing) {
            super(game);
            this.armorStand = Utils.spawnArmorStand(location);
            this.target = target;
            this.speed = speed;
            this.owner = owner;
            this.radianceHealing = radianceHealing;
        }

        @Override
        public void run() {
            if (this.target.isDead()) {
                this.cancel();
                return;
            }

            if (target.getWorld() != armorStand.getWorld()) {
                this.cancel();
                return;
            }

            Location targetLocation = target.getLocation();
            Location armorStandLocation = armorStand.getLocation();
            double distance = targetLocation.distanceSquared(armorStandLocation);

            if (distance < speed * speed) {
                stats.playersHealed++;

                target.addInstance(InstanceBuilder
                        .healing()
                        .cause("Holy Radiance")
                        .source(owner)
                        .value(radianceHealing)
                ).ifPresent(warlordsDamageHealingFinalEvent -> {
                    new CooldownFilter<>(owner, RegularCooldown.class)
                            .filterCooldownFrom(owner)
                            .filterCooldownClassAndMapToObjectsOfClass(HammerOfLight.HammerOfLightData.class)
                            .forEach(hammerOfLight -> hammerOfLight.addAmountHealed(warlordsDamageHealingFinalEvent.getValue()));
                });
                this.cancel();
                return;
            }

            targetLocation.subtract(armorStandLocation);
            //System.out.println(Math.max(speed * 3.25 / targetLocation.lengthSquared() / 2, speed / 10));
            targetLocation.multiply(Math.max(speed * 3.25 / targetLocation.lengthSquared() / 2, speed / 10));

            armorStandLocation.add(targetLocation);
            this.armorStand.teleport(armorStandLocation);

            armorStandLocation.getWorld().spawnParticle(
                    Particle.SPELL,
                    armorStandLocation.add(0, 1.75, 0),
                    2,
                    0.01,
                    0,
                    0.01,
                    0.1,
                    null,
                    true
            );
        }

        @Override
        public void cancel() {
            super.cancel();
            armorStand.remove();
        }
    }
}