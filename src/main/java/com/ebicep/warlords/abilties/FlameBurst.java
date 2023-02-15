package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractProjectileBase;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FlameBurst extends AbstractProjectileBase {

    private float hitbox = 5;
    private double acceleration = 1.0275;
    private double projectileWidth = 0.24D;

    public FlameBurst() {
        super("Flame Burst", 557, 753, 9.4f, 60, 25, 185, 1.65, 200, false);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Launch a flame burst that will explode for" + formatRangeDamage(minDamageHeal, maxDamageHeal) +
                "damage. The critical chance increases by §c1% §7for each travelled block. Up to 100%.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Hit", "" + playersHit));
        info.add(new Pair<>("Dismounts", "" + numberOfDismounts));

        return info;
    }

    @Override
    protected void playEffect(@Nonnull Location currentLocation, int ticksLived) {
        Matrix4d center = new Matrix4d(currentLocation);

        for (float i = 0; i < 4; i++) {
            double angle = Math.toRadians(i * 90) + ticksLived * 0.45;
            double width = projectileWidth;
            currentLocation.getWorld().spawnParticle(
                    Particle.FLAME,
                    center.translateVector(currentLocation.getWorld(), 0, Math.sin(angle) * width, Math.cos(angle) * width),
                    2,
                    0,
                    0,
                    0,
                    0,
                    null,
                    true
            );
        }
    }

    @Override
    protected int onHit(@Nonnull InternalProjectile projectile, @Nullable WarlordsEntity hit) {
        WarlordsEntity shooter = projectile.getShooter();
        Location startingLocation = projectile.getStartingLocation();
        Location currentLocation = projectile.getCurrentLocation();
        World world = currentLocation.getWorld();

        Utils.playGlobalSound(currentLocation, "mage.flameburst.impact", 2, 1);

        world.spawnParticle(Particle.EXPLOSION_LARGE, currentLocation, 2, 0, 0, 0, 0.5, null, true);
        world.spawnParticle(Particle.LAVA, currentLocation, 10, 0.5F, 0, 0.5F, 2, null, true);
        world.spawnParticle(Particle.CLOUD, currentLocation, 3, 0.3F, 0.3F, 0.3F, 1, null, true);

        int playersHit = 0;
        for (WarlordsEntity nearEntity : PlayerFilter
                .entitiesAround(currentLocation, hitbox, hitbox, hitbox)
                .aliveEnemiesOf(shooter)
                .excluding(projectile.getHit())
        ) {
            getProjectiles(projectile).forEach(p -> p.getHit().add(nearEntity));
            playersHit++;
            if (nearEntity.onHorse()) {
                numberOfDismounts++;
            }

            if (pveUpgrade) {
                nearEntity.addDamageInstance(
                        shooter,
                        name,
                        minDamageHeal + (int) Math.pow(currentLocation.distanceSquared(startingLocation), 0.675),
                        maxDamageHeal + (int) Math.pow(currentLocation.distanceSquared(startingLocation), 0.675),
                        critChance + (int) Math.pow(currentLocation.distanceSquared(startingLocation), 0.675),
                        critMultiplier + (int) Math.pow(currentLocation.distanceSquared(startingLocation), 0.675),
                        false
                );
            } else {
                nearEntity.addDamageInstance(
                        shooter,
                        name,
                        minDamageHeal,
                        maxDamageHeal,
                        critChance + (int) Math.pow(currentLocation.distanceSquared(startingLocation), 0.5),
                        critMultiplier,
                        false
                );
            }
        }

        return playersHit;
    }

    @Override
    protected void updateSpeed(Vector speedVector, int ticksLived) {
        speedVector.multiply(acceleration);
    }

    @Override
    protected String getActivationSound() {
        return "mage.fireball.activation";
    }

    @Override
    protected float getSoundVolume() {
        return 2;
    }

    @Override
    protected float getSoundPitch() {
        return 1;
    }

    public float getHitbox() {
        return hitbox;
    }

    public void setHitbox(float hitbox) {
        this.hitbox = hitbox;
    }


    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    public double getProjectileWidth() {
        return projectileWidth;
    }

    public void setProjectileWidth(double projectileWidth) {
        this.projectileWidth = projectileWidth;
    }
}
