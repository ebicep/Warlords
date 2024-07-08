package com.ebicep.warlords.game.option.whackamole;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.Location;

import java.util.concurrent.ThreadLocalRandom;

public class Hole {

    private final Location bottomLocation;
    private final Location topLocation;
    private Game game;
    private AbstractMob mob;
    private boolean active;
    private boolean attackable;

    public Hole(Location bottomLocation) {
        this.bottomLocation = bottomLocation;
        this.topLocation = bottomLocation.clone().add(0, 1, 0);
    }

    public void init(Game game) {
        this.game = game;
    }

    public void disable() {
        mob = null;
        active = false;
        attackable = false;
    }

    public void rise() {
        if (active || mob == null) {
            return;
        }
        active = true;
        new GameRunnable(game) {

            final Location currentLocation = bottomLocation.clone();
            final double speed = ThreadLocalRandom.current().nextDouble(.1, .3);
            int holdTicks = ThreadLocalRandom.current().nextInt(3, 6);

            @Override
            public void run() {
                if (mob == null || mob.getWarlordsNPC().isDead()) {
                    active = false;
                    this.cancel();
                    return;
                }
                if (attackable) {
                    if (holdTicks-- <= 0) {
                        retract();
                        this.cancel();
                        return;
                    }
                    return;
                }
                currentLocation.add(0, speed, 0);
                mob.getWarlordsNPC().teleport(currentLocation);
                if (currentLocation.getY() >= topLocation.getY()) {
                    attackable = true;
                }
            }

        }.runTaskTimer(0, 1);
    }

    private void retract() {
        new GameRunnable(game) {

            final Location currentLocation = topLocation.clone();
            final double speed = ThreadLocalRandom.current().nextDouble(.1, .25);

            @Override
            public void run() {
                if (mob == null) {
                    this.cancel();
                    return;
                }
                currentLocation.add(0, -speed, 0);
                mob.getWarlordsNPC().teleport(currentLocation);
                if (currentLocation.getY() <= bottomLocation.getY()) {
                    active = false;
                    attackable = false;
                    this.cancel();
                }
            }

        }.runTaskTimer(0, 1);
    }

    public Location getBottomLocation() {
        return bottomLocation;
    }

    public AbstractMob getMob() {
        return mob;
    }

    public void setMob(AbstractMob mob) {
        this.mob = mob;
    }

    public boolean isAttackable() {
        return attackable;
    }
}
