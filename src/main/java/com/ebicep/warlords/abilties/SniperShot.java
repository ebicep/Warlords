package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractProjectileBase;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class SniperShot extends AbstractProjectileBase {

    // dont ask

    public SniperShot() {
        super("Sniper Shot", 2209, 3654, 10, 100, 100, 300, 30, 500, false);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Shoot a massive projectile at rapid speed," +
                "dealing " + format(minDamageHeal) + " - " + format(maxDamageHeal) + " damage to the first target it hits.";
    }

    @Nullable
    @Override
    protected String getActivationSound() {
        return "rogue.wondertrap.explosion";
    }

    @Override
    protected float getSoundPitch() {
        return 1;
    }

    @Override
    protected float getSoundVolume() {
        return 100;
    }

    @Override
    protected void playEffect(@Nonnull Location currentLocation, int ticksLived) {
        Matrix4d center = new Matrix4d(currentLocation);

        for (float i = 0; i < 4; i++) {
            double angle = Math.toRadians(i * 90) + ticksLived * 0.45;
            double width = 0.12D;
            ParticleEffect.FLAME.display(0, 0, 0, 0, 2,
                    center.translateVector(currentLocation.getWorld(), 0, Math.sin(angle) * width, Math.cos(angle) * width), 500);
        }
    }

    @Override
    protected void onHit(@Nonnull WarlordsPlayer shooter, @Nonnull Location currentLocation, @Nonnull Location startingLocation, @Nullable WarlordsPlayer hit) {
        if (hit != null) {
            hit.addDamageInstance(
                    shooter,
                    name,
                    minDamageHeal,
                    maxDamageHeal,
                    critChance,
                    critMultiplier,
                    false);
        }
    }
}
