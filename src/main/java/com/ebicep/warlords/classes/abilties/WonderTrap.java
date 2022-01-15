package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
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

        return true;
    }

    private class Trap extends BukkitRunnable {

        private WarlordsPlayer trapOwner;
        private WarlordsPlayer trapTarget;
        private float trapDuration;
        private float trapDelayDuration;
        private double trapRadius;
        private ArmorStand trapStand;

        public Trap(Location location, WarlordsPlayer trapOwner, WarlordsPlayer trapTarget, float trapDuration, float trapDelayDuration, double trapRadius, ArmorStand trapStand) {
            this.trapOwner = trapOwner;
            this.trapTarget = trapTarget;
            this.trapDuration = trapDuration;
            this.trapDelayDuration = trapDelayDuration;
            this.trapRadius = trapRadius;
            this.trapStand = location.getWorld().spawn(location, ArmorStand.class);

            trapStand.setGravity(false);
            trapStand.setVisible(false);
            trapStand.getLocation().add(0, -1, 0);
        }

        @Override
        public void cancel() {
            super.cancel();
            trapStand.remove();
        }

        @Override
        public void run() {

        }
    }
}
