package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractProjectileBase;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FlameBurst extends AbstractProjectileBase {

    private static final float HITBOX = 5;

    public FlameBurst() {
        super("Flame Burst", 557, 753, 9.4f, 60, 25, 185, 1.65, 200, false);
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Launch a flame burst that will explode\n" +
                "§7for §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage. The critical\n" +
                "§7chance increases by §c1% §7for each\n" +
                "§7travelled block. Up to 100%.";
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
    protected void updateSpeed(Vector speedVector, int ticksLived) {
        speedVector.multiply(1.0275);
    }

    @Override
    protected String getActivationSound() {
        return "mage.fireball.activation";
    }

    @Override
    protected float getSoundPitch() {
        return 1;
    }

    @Override
    protected float getSoundVolume() {
        return 2;
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
    protected int onHit(@Nonnull InternalProjectile projectile, @Nullable WarlordsPlayer hit) {
        WarlordsPlayer shooter = projectile.getShooter();
        Location startingLocation = projectile.getStartingLocation();
        Location currentLocation = projectile.getCurrentLocation();

        Utils.playGlobalSound(currentLocation, "mage.flameburst.impact", 2, 1);

        ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0.5F, 2, currentLocation, 500);
        ParticleEffect.LAVA.display(0.5F, 0, 0.5F, 2F, 10, currentLocation, 500);
        ParticleEffect.CLOUD.display(0.3F, 0.3F, 0.3F, 1, 3, currentLocation, 500);

        int playersHit = 0;
        for (WarlordsPlayer nearEntity : PlayerFilter
                .entitiesAround(currentLocation, HITBOX, HITBOX, HITBOX)
                .aliveEnemiesOf(shooter)
        ) {
            playersHit++;
            if (nearEntity.onHorse()) {
                numberOfDismounts++;
            }

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

        return playersHit;
    }
}
