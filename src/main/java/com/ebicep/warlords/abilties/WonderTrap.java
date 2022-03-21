package com.ebicep.warlords.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.TextCooldown;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;

public class WonderTrap extends AbstractAbility {

    // CURRENTLY UNUSED CONTENT

    public WonderTrap() {
        super("Wonder Trap", 375, 454, 10, 40, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "PLACEHOLDER";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        WonderTrap tempTrap = new WonderTrap();

        Utils.playGlobalSound(player.getLocation(), "rogue.hearttoheart.activation", 2, 0.6f);

        Trap trap = new Trap(wp.getLocation(), wp, 200, 40, 3);
        trap.runTaskTimer(Warlords.getInstance(), 0, 0);

        TextCooldown textCooldown = new TextCooldown("Wonder Trap", "TRAP", WonderTrap.class, tempTrap, wp, CooldownTypes.ABILITY, cooldownManager -> {
        }, "2");
        wp.getCooldownManager().addCooldown(textCooldown);

        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                switch (counter++) {
                    case 1:
                        textCooldown.setText("1");
                        break;
                    case 2:
                        textCooldown.setText("READY");
                        trap.setCanEndEarly(true);
                        break;
                    case 10:
                        textCooldown.setRemove(true);
                        trap.cancel();
                        this.cancel();
                        break;
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

    private class Trap extends BukkitRunnable {

        private final WarlordsPlayer trapOwner;
        private int timeToLive;
        private int trapArmTime;
        private final double trapRadius;
        private final ArmorStand trapStand;
        private boolean canEndEarly = false;

        public Trap(Location location, WarlordsPlayer trapOwner, int timeToLive, int trapArmTime, double trapRadius) {
            this.trapOwner = trapOwner;
            this.timeToLive = timeToLive;
            this.trapArmTime = trapArmTime;
            this.trapRadius = trapRadius;
            this.trapStand = location.getWorld().spawn(location, ArmorStand.class);

            trapStand.setHelmet(new ItemStack(Material.STONE));
            trapStand.setGravity(false);
            trapStand.setVisible(false);
            trapStand.getLocation().add(0, -2, 0);
        }

        @Override
        public void cancel() {
            super.cancel();
            trapStand.remove();
        }

        @Override
        public void run() {
                timeToLive--;
                trapArmTime--;

                if (trapOwner.isSneaking() && canEndEarly) {

                    Utils.playGlobalSound(trapStand.getLocation(), "rogue.wondertrap.explosion", 2, 1.75f);

                    EffectUtils.playStarAnimation(trapStand.getLocation().add(0, -2, 0), 3, ParticleEffect.FIREWORKS_SPARK);

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

                                //final Location loc = trapStand.getLocation();
                                //final Vector v = loc.toVector().subtract(loc.toVector()).normalize().multiply(-1.1).setY(0.15);
                                //trapTarget.setVelocity(v);

                                WonderTrap tempTrap = new WonderTrap();

                                trapTarget.getCooldownManager().addRegularCooldown("KB Increase", "KB", WonderTrap.class, tempTrap, trapOwner, CooldownTypes.DEBUFF, cooldownManager -> {
                                }, 30 * 20);
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

        public boolean isCanEndEarly() {
            return canEndEarly;
        }

        public void setCanEndEarly(boolean canEndEarly) {
            this.canEndEarly = canEndEarly;
        }
    }
}
