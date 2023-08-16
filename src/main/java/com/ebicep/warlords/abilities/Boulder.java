package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractTimeWarp;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.earthwarden.BoulderBranch;
import com.ebicep.warlords.util.java.Pair;
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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Boulder extends AbstractAbility implements RedAbilityIcon {

    public int playersHit = 0;
    public int carrierHit = 0;
    public int warpsKnockbacked = 0;

    private final double boulderGravity = -0.0059;
    private double boulderSpeed = 0.290;
    private double hitbox = 5.5;
    private double velocity = 1.15;

    public Boulder() {
        super("Boulder", 451, 673, 7.05f, 80, 15, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Launch a giant boulder that shatters and deals")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text("damage to all enemies near the impact point and knocks them back slightly."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Hit", "" + playersHit));
        info.add(new Pair<>("Carriers Hit", "" + carrierHit));
        info.add(new Pair<>("Warps Knockbacked", "" + warpsKnockbacked));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(player.getLocation(), "shaman.boulder.activation", 2, 1);

        Location location = player.getLocation();
        Vector speed;
        if (pveMasterUpgrade) {
            speed = player.getLocation().getDirection().add(new Vector(0, 0.5, 0).multiply(boulderSpeed));
        } else {
            speed = player.getLocation().getDirection().multiply(boulderSpeed);
        }

        Location initialCastLocation = player.getLocation();

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

                    new GameRunnable(wp.getGame()) {
                        @Override
                        public void run() {
                            for (WarlordsEntity p : PlayerFilter
                                    .entitiesAround(newLoc, hitbox, hitbox, hitbox)
                                    .aliveEnemiesOf(wp)
                            ) {
                                playersHit++;
                                if (p.hasFlag()) {
                                    carrierHit++;
                                }
                                if (p.getCooldownManager().hasCooldownExtends(AbstractTimeWarp.class) && FlagHolder.playerTryingToPick(p)) {
                                    warpsKnockbacked++;
                                }
                                Vector v;
                                if (p == directHit) {
                                    v = initialCastLocation.toVector().subtract(p.getLocation().toVector()).normalize().multiply(-velocity).setY(0.2);
                                } else {
                                    v = p.getLocation().toVector().subtract(newLoc.toVector()).normalize().multiply(velocity).setY(0.2);
                                }
                                p.setVelocity(name, v, false, false);
                                p.addDamageInstance(wp, name, minDamageHeal, maxDamageHeal, critChance, critMultiplier);
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
                        }
                    }.runTaskLater(1);
                }
        );

        return true;
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


}