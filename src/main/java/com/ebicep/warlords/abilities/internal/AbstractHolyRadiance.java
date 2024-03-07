package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.HammerOfLight;
import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityTargetEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractHolyRadiance extends AbstractAbility implements BlueAbilityIcon, HitBox {

    public int playersHealed = 0;
    public int playersMarked = 0;

    private FloatModifiable radius;

    public AbstractHolyRadiance(
            String name,
            float minDamageHeal,
            float maxDamageHeal,
            float cooldown,
            float energyCost,
            float critChance,
            float critMultiplier,
            int radius
    ) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
        this.radius = new FloatModifiable(radius);
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        wp.addHealingInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);


        if (chain(wp)) {
            playersMarked++;
        }

        float rad = radius.getCalculatedValue();
        Set<WarlordsEntity> warlordsEntities = PlayerFilter
                .entitiesAround(wp, rad, rad, rad)
                .aliveTeammatesOfExcludingSelf(wp)
                .stream()
                .collect(Collectors.toSet());
        for (WarlordsEntity radianceTarget : warlordsEntities) {
            wp.getGame().registerGameTask(
                    new FlyingArmorStand(
                            wp.getLocation(),
                            radianceTarget,
                            wp,
                            1.1,
                            minDamageHeal.getCalculatedValue(),
                            maxDamageHeal.getCalculatedValue()
                    ).runTaskTimer(Warlords.getInstance(), 1, 1)
            );
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

    public abstract boolean chain(WarlordsEntity wp);

    @Override
    public void runEveryTick(@Nullable WarlordsEntity warlordsEntity) {
        radius.tick();
        super.runEveryTick(warlordsEntity);
    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return radius;
    }

    public class FlyingArmorStand extends BukkitRunnable {

        private final WarlordsEntity target;
        private final WarlordsEntity owner;
        private final double speed;
        private final ArmorStand armorStand;
        private final float minHeal;
        private final float maxHeal;

        public FlyingArmorStand(Location location, WarlordsEntity target, WarlordsEntity owner, double speed, float minHeal, float maxHeal) {
            this.armorStand = Utils.spawnArmorStand(location);
            this.target = target;
            this.speed = speed;
            this.owner = owner;
            this.minHeal = minHeal;
            this.maxHeal = maxHeal;
        }

        @Override
        public void run() {
            if (!owner.getGame().isFrozen()) {

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
                    playersHealed++;

                    target.addHealingInstance(
                            owner,
                            name,
                            minHeal,
                            maxHeal,
                            critChance,
                            critMultiplier
                    ).ifPresent(warlordsDamageHealingFinalEvent -> {
                        new CooldownFilter<>(owner, RegularCooldown.class)
                                .filterCooldownFrom(owner)
                                .filterCooldownClassAndMapToObjectsOfClass(HammerOfLight.class)
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
        }

        @Override
        public void cancel() {
            super.cancel();
            armorStand.remove();
        }
    }
}
