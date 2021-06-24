package com.ebicep.warlords.effects;

import com.ebicep.warlords.Warlords;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ArmorStandWaveEffect {
    private final List<Stand> stands;

    public ArmorStandWaveEffect(Location center, double range, double speed, ItemStack texture) {
        stands = new ArrayList<>((int) (Math.pow(Math.ceil(range), 2) * Math.PI * 1.1));
        double doubleRange = range * range;
        for (int x = (int) -range; x <= range; x++) {
            for (int z = (int) -range; z <= range; z++) {
                double distanceSquared = x * x + z * z;
                if (distanceSquared < doubleRange) {
                    stands.add(new Stand(center.clone().add(x, -1, z), (int) (-Math.sqrt(distanceSquared) * speed), texture));
                }
            }
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
        private ArmorStand stand;
        private int timer;
        private final ItemStack texture;

        public Stand(Location loc, int timer, ItemStack texture) {
            this.loc = loc;
            this.timer = timer;
            this.texture = texture;
        }

        public int getTimer() {
            return timer;
        }

        public boolean tick() {
            timer++;
            if (timer >= 0) {
                if (stand == null) {
                    stand = loc.getWorld().spawn(loc, ArmorStand.class);
                    stand.setGravity(false);
                    stand.setHelmet(texture);
                    stand.setBasePlate(false);
                    stand.setVisible(false);
                    stand.setMarker(true);
                }
                stand.teleport(stand.getLocation().add(0, 0.3 - timer / 20D, 0));


                if (timer > 20) {
                    stand.remove();
                    return true;
                }
            }
            return false;
        }
    }
}