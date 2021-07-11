package com.ebicep.warlords.effects;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.WarlordsEvents;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class FallingBlockWaveEffect {
    private final List<Stand> stands;

    public FallingBlockWaveEffect(Location center, double range, double speed, Material material, byte damage) {
        stands = new ArrayList<>((int) (Math.pow(Math.ceil(range), 2) * Math.PI * 1.1));
        double doubleRange = range * range;
        for (int x = (int) -range; x <= range; x++) {
            for (int z = (int) -range; z <= range; z++) {
                double distanceSquared = x * x + z * z;
                if (distanceSquared < doubleRange) {
                    stands.add(new Stand(center.clone().add(x, 0, z), (int) (-Math.sqrt(distanceSquared) / speed), material, damage));
                }
                if ((int) (Math.random() * 5) == 1) z++;
            }
            //hypixel random ass holes effect = more immersion
            if ((int) (Math.random() * 5) == 1) x++;
        }
        Collections.sort(stands, Comparator.comparing(Stand::getTimer).reversed());
    }

    public void play() {
        new BukkitRunnable() {
            @Override
            public void run() {
                int size = stands.size();
                if (size == 0) {
                    this.cancel();
                    return;
                }
                ListIterator<Stand> itr = stands.listIterator(size);
                while (itr.hasPrevious()) {
                    if (itr.previous().tick()) {
                        itr.remove();
                    }
                }
            }

        }.runTaskTimer(Warlords.getInstance(), 1, 1);
    }

    class Stand {
        private final Location loc;
        private int timer;
        private final Material material;
        private final byte damage;
        private FallingBlock fallingBlock;

        public Stand(Location loc, int timer, Material material, byte damage) {
            this.loc = loc;
            this.timer = timer;
            this.material = material;
            this.damage = damage;
        }

        public int getTimer() {
            return timer;
        }

        public boolean tick() {
            timer++;
            if (timer == 0) {
                fallingBlock = loc.getWorld().spawnFallingBlock(loc, material, damage);
                fallingBlock.setVelocity(new Vector(0, 0.05, 0));
                fallingBlock.setDropItem(false);
                WarlordsEvents.addEntityUUID(fallingBlock.getUniqueId());

                return false;
            } else if (timer == 6) {
                if (fallingBlock != null) {
                    fallingBlock.remove();
                }
                return true;
            }
            return false;
        }
    }
}