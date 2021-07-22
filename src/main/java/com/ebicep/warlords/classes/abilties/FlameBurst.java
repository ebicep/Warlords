package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.internal.AbstractProjectileBase;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.Matrix4d;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class FlameBurst extends AbstractProjectileBase {

    private static final float HITBOX = 5;

    public FlameBurst() {
        super("Flame Burst", -557, -753, 9.4f, 60, 25, 185, 1.65, 300, false);
    }

    @Override
    protected void updateSpeed(Vector speedVector, int ticksLived) {
        speedVector.multiply(1.0275);
    }

    @Override
    protected String getActivationSound() {
        return "mage.fireball.activation";
    }

    @Override
    protected void playEffect(Location currentLocation, int ticksLived) {
        Matrix4d center = new Matrix4d(currentLocation);

        for (float i = 0; i < 4; i++) {
            double angle = Math.toRadians(i * 90) + ticksLived * 0.45;
            double width = 0.24D;
            ParticleEffect.FLAME.display(0, 0, 0, 0, 2,
                    center.translateVector(currentLocation.getWorld(), 0, Math.sin(angle) * width, Math.cos(angle) * width), 500);
        }
    }

    @Override
    protected void onHit(WarlordsPlayer shooter, Location currentLocation, Location startingLocation, WarlordsPlayer victim) {
        ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0.5F, 2, currentLocation, 500);
        ParticleEffect.LAVA.display(0.5F, 0, 0.5F, 2F, 10, currentLocation, 500);

        for (Player player1 : currentLocation.getWorld().getPlayers()) {
            player1.playSound(currentLocation, "mage.flameburst.impact", 2, 1);
        }

        for (WarlordsPlayer nearEntity : PlayerFilter
                .entitiesAround(currentLocation, HITBOX, HITBOX, HITBOX)
                .aliveEnemiesOf(shooter)
        ) {
            nearEntity.addHealth(
                    shooter,
                    name,
                    minDamageHeal,
                    maxDamageHeal,
                    critChance + (int) Math.pow(currentLocation.distanceSquared(startingLocation), 2),
                    critMultiplier
            );
        }
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Launch a flame burst that will explode\n" +
            "§7for §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " §7damage. The critical\n" +
            "§7chance increases by §c1% §7for each\n" +
            "§7travelled block. Up to 100%.";
    }
	
}
