package com.ebicep.warlords.abilities;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.AbilityStats;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractAbilityStats;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.TextCooldown;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class WonderTrap extends AbstractAbility implements AbilityStats<WonderTrap, WonderTrap.WonderTrapStats> {

    // CURRENTLY UNUSED CONTENT

    private final WonderTrapStats stats = new WonderTrapStats();

    public WonderTrap() {
        super("Wonder Trap", 10, 40);
    }

    //    @Override
//    public void updateDescription(Player player) {
//        //description = "PLACEHOLDER";
//    }
    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "rogue.hearttoheart.activation", 2, 0.6f);

        Trap trap = new Trap(wp.getLocation(), wp, 200, 40, 3);
        trap.runTaskTimer(Warlords.getInstance(), 0, 0);

        TextCooldown textCooldown = new TextCooldown("Wonder Trap", "TRAP", WonderTrap.class, null, wp, CooldownTypes.ABILITY, cooldownManager -> {
        }, "2");
        wp.getCooldownManager().addCooldown(textCooldown);

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                switch (counter++) {
                    case 1 -> textCooldown.setText("1");
                    case 2 -> {
                        textCooldown.setText("READY");
                        trap.setCanEndEarly(true);
                    }
                    case 10 -> {
                        textCooldown.setRemove(true);
                        trap.cancel();
                        this.cancel();
                    }
                }

                if (counter > 2 && wp.isSneaking() && trap.isCanEndEarly()) {
                    trap.cancel();
                    this.cancel();
                    textCooldown.setRemove(true);
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 20);

        return true;
    }

    @Override
    public WonderTrapStats getAbilityStats() {
        return stats;
    }

    public static class WonderTrapStats extends AbstractAbilityStats<WonderTrap, WonderTrapStats> {

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public WonderTrapStats merge(WonderTrapStats other, int multiplier) {
            WonderTrapStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public Class<WonderTrapStats> getClazz() {
            return WonderTrapStats.class;
        }

        @Override
        public WonderTrapStats create() {
            return new WonderTrapStats();
        }
    }

    private class Trap extends BukkitRunnable {

        private final WarlordsEntity trapOwner;
        private final double trapRadius;
        private final ArmorStand trapStand;
        private int timeToLive;
        private int trapArmTime;
        private boolean canEndEarly = false;

        public Trap(Location location, WarlordsEntity trapOwner, int timeToLive, int trapArmTime, double trapRadius) {
            this.trapOwner = trapOwner;
            this.timeToLive = timeToLive;
            this.trapArmTime = trapArmTime;
            this.trapRadius = trapRadius;
            this.trapStand = Utils.spawnArmorStand(location, armorStand -> {
                armorStand.getEquipment().setHelmet(new ItemStack(Material.STONE));
                armorStand.getLocation().add(0, -2, 0);
            });
        }

        @Override
        public void run() {
            timeToLive--;
            trapArmTime--;

            if (trapOwner.isSneaking() && canEndEarly) {

                Utils.playGlobalSound(trapStand.getLocation(), "rogue.wondertrap.explosion", 2, 1.75f);

                EffectUtils.playStarAnimation(trapStand.getLocation().add(0, -2, 0), 3, Particle.FIREWORK);

                PlayerFilter.entitiesAround(trapStand, trapRadius, trapRadius, trapRadius)
                            .aliveEnemiesOf(trapOwner)
                            .forEach((trapTarget) -> {
//                            trapTarget.addDamageInstance(
//                                    trapOwner,
//                                    name,
//                                    minDamageHeal,
//                                    maxDamageHeal,
//                                    critChance,
//                                    critMultiplier
//                            );

                                //final Location loc = trapStand.getLocation();
                                //final Vector v = loc.toVector().subtract(loc.toVector()).normalize().multiply(-1.1).setY(0.15);
                                //trapTarget.setVelocity(v);

                                trapTarget.getCooldownManager().addRegularCooldown(
                                        "KB Increase",
                                        "KB",
                                        WonderTrap.class,
                                        null,
                                        trapOwner,
                                        CooldownTypes.DEBUFF,
                                        cooldownManager -> {
                                        },
                                        30 * 20
                                );
                            });
                this.cancel();
            }

                /*if (trapArmTime < 0) {
                    setCanEndEarly(true);

                    PlayerFilter.entitiesAround(trapStand, trapRadius, trapRadius, trapRadius)
                            .aliveEnemiesOf(trapOwner)
                            .forEach((trapTarget) -> {
                                trapTarget.addDamageInstance(
                                        trapOwner,
                                        name,
                                        minDamageHeal,
                                        maxDamageHeal,
                                        critChance,
                                        critMultiplier,
                                        false);
                                this.cancel();
                            });
                }*/

            if (timeToLive <= 0) {
                this.cancel();
            }
        }

        @Override
        public void cancel() {
            super.cancel();
            trapStand.remove();
        }

        public boolean isCanEndEarly() {
            return canEndEarly;
        }

        public void setCanEndEarly(boolean canEndEarly) {
            this.canEndEarly = canEndEarly;
        }
    }
}