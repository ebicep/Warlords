package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;

public class WonderTrap extends AbstractAbility {

    public WonderTrap() {
        super("Wonder Trap", 375, 454, 10, 40, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "PLACEHOLDER";
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsPlayer wp, @Nonnull Player player) {
        // WIP
        new Trap(wp.getLocation(), wp, 4, 2, 1.5).runTaskTimer(Warlords.getInstance(), 40, 20);
        return true;
    }

    private class Trap extends BukkitRunnable {

        private WarlordsPlayer trapOwner;
        private float trapDuration;
        private float trapDelayDuration;
        private double trapRadius;
        private ArmorStand trapStand;

        public Trap(Location location, WarlordsPlayer trapOwner, float trapDuration, float trapDelayDuration, double trapRadius) {
            this.trapOwner = trapOwner;
            this.trapDuration = trapDuration;
            this.trapDelayDuration = trapDelayDuration;
            this.trapRadius = trapRadius;
            this.trapStand = location.getWorld().spawn(location, ArmorStand.class);

            trapStand.setGravity(false);
            trapStand.setVisible(true);
            trapStand.getLocation().add(0, 0, 0);
        }

        @Override
        public void cancel() {
            super.cancel();
            trapStand.remove();
        }

        WonderTrap tempTrap = new WonderTrap();

        int counter = (int) trapDelayDuration * 20;
        @Override
        public void run() {
            if (counter == 40) {
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
                            trapTarget.getCooldownManager().addRegularCooldown(name, "TRAP", WonderTrap.class, tempTrap, trapOwner, CooldownTypes.ABILITY, cooldownManager -> {
                            }, 4 * 20);
                        });
            }

            if (counter == 160) {
                this.cancel();
            }
        }
    }
}
