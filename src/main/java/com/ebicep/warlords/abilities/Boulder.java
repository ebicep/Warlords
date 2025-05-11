package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.earthwarden.BoulderBranch;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Boulder extends AbstractAbility implements RedAbilityIcon, Damages<Boulder.DamageValues>, AbilityStats<Boulder, Boulder.BoulderStats> {

    private final DamageValues damageValues = new DamageValues();
    private final double boulderGravity = -0.0059;
    private final BoulderStats stats = new BoulderStats();
    private double boulderSpeed = 0.290;
    private double hitbox = 5.5;
    private double velocity = 1.15;

    public Boulder() {
        super(AbstractAbilityBuilder.create("Boulder").pvp());
    }

    public Boulder(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Launch a giant boulder that shatters and deals")
                .damage(damageValues.boulderDamage)
                .text("damage to all enemies near the impact point and knocks them back slightly.")
                .build();
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        Utils.playGlobalSound(wp.getLocation(), "shaman.boulder.activation", 2, 1);

        Location location = wp.getLocation();
        Vector speed = calculateSpeed(wp);

        Location initialCastLocation = wp.getLocation();

        Utils.spawnThrowableProjectile(
                wp.getGame(),
                Utils.spawnArmorStand(location, armorStand -> {
                    armorStand.getEquipment().setHelmet(new ItemStack(Material.TALL_GRASS));
                    armorStand.customName(Component.text("Boulder"));
                    armorStand.setCustomNameVisible(false);
                }),
                speed,
                boulderGravity,
                boulderSpeed,
                (newLoc, integer) -> wp.getLocation().getWorld().spawnParticle(
                        Particle.CRIT,
                        newLoc.clone().add(0, -1, 0),
                        6,
                        0.3F,
                        0.3F,
                        0.3F,
                        0.1F,
                        null,
                        true
                ),
                newLoc -> PlayerFilter
                        .entitiesAroundRectangle(newLoc, 1, 2, 1)
                        .aliveEnemiesOf(wp)
                        .findFirstOrNull(),
                (newLoc, directHit) -> {
                    Utils.playGlobalSound(newLoc, "shaman.boulder.impact", 2, 1);

                    // this was previously delayed by a tick idk why, if something breaks, you know why
                    for (WarlordsEntity p : PlayerFilter
                            .entitiesAround(newLoc, hitbox, hitbox, hitbox)
                            .aliveEnemiesOf(wp)
                    ) {
                        stats.targetsHit++;
                        if (p.hasFlag()) {
                            stats.carrierHit++;
                        }
                        if (p.getCooldownManager().hasCooldownExtends(AbstractTimeWarp.class) && FlagHolder.playerNearFlag(p)) {
                            stats.warpsKnockbacked++;
                        }
                        Vector v;
                        if (p == directHit) {
                            v = initialCastLocation.toVector().subtract(p.getLocation().toVector()).normalize().multiply(-velocity).setY(0.2);
                        } else {
                            v = p.getLocation().toVector().subtract(newLoc.toVector()).normalize().multiply(velocity).setY(0.2);
                        }
                        p.setVelocity(name, v, false, false);
                        p.addInstance(InstanceBuilder
                                .damage()
                                .ability(this)
                                .source(wp)
                                .value(damageValues.boulderDamage)
                        );
                    }

                    newLoc.setPitch(-12);
                    Location impactLocation = newLoc.clone().subtract(speed);
                    Utils.spawnFallingBlocks(impactLocation, 3, 10);

                    new GameRunnable(wp.getGame()) {

                        @Override
                        public void run() {
                            Utils.spawnFallingBlocks(impactLocation, 3.5, 20);
                        }

                    }.runTaskLater(1);

                    if (pveMasterUpgrade2) {
                        new FallingBlockWaveEffect(impactLocation.clone().add(0, 1, 0), 4, 1.2, Material.COARSE_DIRT).play();
                        Utils.playGlobalSound(impactLocation, "arcanist.beacon.impact", 2, .1f);
                        Utils.playGlobalSound(impactLocation, "arcanist.beacon.impact", 2, .1f);
                        Utils.playGlobalSound(impactLocation, "arcanist.beacon.impact", 2, .1f);
                        for (WarlordsEntity enemy : PlayerFilter
                                .entitiesAround(impactLocation, 5, 5, 5)
                                .aliveEnemiesOf(wp)
                        ) {
                            enemy.addInstance(InstanceBuilder
                                    .damage()
                                    .ability(this)
                                    .source(wp)
                                    .value(damageValues.earthquakeDamage)
                            );
                        }
                    }

                }
        );

        return true;
    }

    protected Vector calculateSpeed(WarlordsEntity we) {
        Vector speed;
        if (pveMasterUpgrade) {
            speed = we.getLocation().getDirection().add(new Vector(0, 0.5, 0).multiply(boulderSpeed));
        } else {
            speed = we.getLocation().getDirection().multiply(boulderSpeed);
        }
        return speed;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new BoulderBranch(abilityTree, this);
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getBoulderSpeed() {
        return boulderSpeed;
    }

    public void setBoulderSpeed(double boulderSpeed) {
        this.boulderSpeed = boulderSpeed;
    }

    public double getHitbox() {
        return hitbox;
    }

    public void setHitbox(double hitbox) {
        this.hitbox = hitbox;
    }

    @Override
    public BoulderStats getAbilityStats() {
        return stats;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable boulderDamage = new Value.RangedValueCritable(509, 686, 15, 175);
        private final Value.RangedValue earthquakeDamage = new Value.RangedValue(450, 630);
        private final List<Value> values = List.of(boulderDamage, earthquakeDamage);

        public Value.RangedValueCritable getBoulderDamage() {
            return boulderDamage;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

    public static class BoulderStats extends AbstractAbilityStats<Boulder, BoulderStats> {

        @Field("targets_hit")
        private int targetsHit = 0;
        @Field("carrier_hit")
        private int carrierHit = 0;
        @Field("warps_knockbacked")
        private int warpsKnockbacked = 0;

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Hit", targetsHit));
            statsDisplay.add(new AbilityStatDisplay("Carriers Hit", carrierHit));
            statsDisplay.add(new AbilityStatDisplay("Warps Knockbacked", warpsKnockbacked));
            return statsDisplay;
        }

        @Override
        public BoulderStats merge(BoulderStats other, int multiplier) {
            BoulderStats stats = super.merge(other, multiplier);
            stats.targetsHit = this.targetsHit + other.targetsHit * multiplier;
            stats.carrierHit = this.carrierHit + other.carrierHit * multiplier;
            stats.warpsKnockbacked = this.warpsKnockbacked + other.warpsKnockbacked * multiplier;
            return stats;
        }

        @Override
        public Class<BoulderStats> getClazz() {
            return BoulderStats.class;
        }

        @Override
        public BoulderStats create() {
            return new BoulderStats();
        }
    }
}
