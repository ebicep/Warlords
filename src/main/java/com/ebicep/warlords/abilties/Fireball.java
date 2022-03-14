package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractProjectileBase;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class Fireball extends AbstractProjectileBase {

    private static final int MAX_FULL_DAMAGE_DISTANCE = 50;
    private static final double DIRECT_HIT_MULTIPLIER = 1.15;
    private static final float HITBOX = 4;

    public Fireball() {
        super("Fireball", 334.4f, 433.4f, 0, 70, 20, 175, 2, 300, false);
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
    protected void playEffect(@Nonnull Location currentLocation, int animationTimer) {
        ParticleEffect.DRIP_LAVA.display(0, 0, 0, 0.35F, 5, currentLocation, 500);
        ParticleEffect.SMOKE_NORMAL.display(0, 0, 0, 0.001F, 7, currentLocation, 500);
        ParticleEffect.FLAME.display(0, 0, 0, 0.06F, 1, currentLocation, 500);
    }

    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);
        this.playEffect(projectile);
    }

    @Override
    protected void onHit(@Nonnull WarlordsPlayer shooter, @Nonnull Location currentLocation, @Nonnull Location startingLocation, WarlordsPlayer victim) {
        ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0.5F, 1, currentLocation, 500);
        ParticleEffect.LAVA.display(0.5F, 0, 0.5F, 1.5f, 10, currentLocation, 500);
        ParticleEffect.CLOUD.display(0.3F, 0.3F, 0.3F, 1F, 3, currentLocation, 500);

        Utils.playGlobalSound(currentLocation, "mage.fireball.impact", 2, 1);

        double distanceSquared = startingLocation.distanceSquared(currentLocation);
        double toReduceBy = MAX_FULL_DAMAGE_DISTANCE * MAX_FULL_DAMAGE_DISTANCE > distanceSquared ? 1 : 
            1 - (Math.sqrt(distanceSquared) - MAX_FULL_DAMAGE_DISTANCE) / 75;
        if (toReduceBy < .2) toReduceBy = .2;
        if (victim != null) {
            victim.addDamageInstance(
                    shooter,
                    name,
                    (float) (minDamageHeal * DIRECT_HIT_MULTIPLIER * toReduceBy),
                    (float) (maxDamageHeal * DIRECT_HIT_MULTIPLIER * toReduceBy),
                    critChance,
                    critMultiplier,
                    false);
        }
        
        for (WarlordsPlayer nearEntity : PlayerFilter
                .entitiesAround(currentLocation, HITBOX, HITBOX, HITBOX)
                .excluding(victim)
                .aliveEnemiesOf(shooter)
        ) {
            nearEntity.addDamageInstance(
                    shooter,
                    name,
                    (float) (minDamageHeal * toReduceBy),
                    (float) (maxDamageHeal * toReduceBy),
                    critChance,
                    critMultiplier,
                    false);
        }
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Shoot a fireball that will explode\n" +
                "§7for §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage. A\n" +
                "§7direct hit will cause the enemy\n" +
                "§7to take an additional §c15% §7extra\n" +
                "§7damage." +
                "\n\n" +
                "§7Has an optimal range of §e" + MAX_FULL_DAMAGE_DISTANCE + " §7blocks.";
    }
	
}
