package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractProjectileBase;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;

public class FrostBolt extends AbstractProjectileBase {

    private static final int MAX_FULL_DAMAGE_DISTANCE = 30;
    private static final double DIRECT_HIT_MULTIPLIER = 1.15;
    private static final float HITBOX = 4;

    public FrostBolt() {
        super("Frostbolt", 268.8f, 345.45f, 0, 70, 20, 175, 2, 300, false);
    }

    @Override
    protected String getActivationSound() {
        return "mage.frostbolt.activation";
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
    protected void playEffect(Location currentLocation, int animationTimer) {
        ParticleEffect.CLOUD.display(0, 0, 0, 0F, 1, currentLocation, 500);
    }

    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);
        this.playEffect(projectile);
    }

    @Override
    protected void onHit(WarlordsPlayer shooter, Location currentLocation, Location startingLocation, WarlordsPlayer victim) {
        ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0.0F, 1, currentLocation, 500);
        ParticleEffect.CLOUD.display(0.3F, 0.3F, 0.3F, 1F, 3, currentLocation, 500);

        Utils.playGlobalSound(currentLocation, "mage.frostbolt.impact", 2, 1);

        double distanceSquared = currentLocation.distanceSquared(startingLocation);
        double toReduceBy = MAX_FULL_DAMAGE_DISTANCE * MAX_FULL_DAMAGE_DISTANCE > distanceSquared ? 1 : 
            1 - (Math.sqrt(distanceSquared) - MAX_FULL_DAMAGE_DISTANCE) / 75;
        if (toReduceBy < .2) toReduceBy = .2;
        if (victim != null && victim.isEnemy(shooter)) {
            victim.getSpeed().addSpeedModifier("Frostbolt", -25, 2 * 20);
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
            nearEntity.getSpeed().addSpeedModifier("Frostbolt", -25, 2 * 20);
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
        description = "§7Shoot a frostbolt that will shatter\n" +
                "§7for §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage and slow\n" +
                "§7by §e25% §7for §62 §7seconds. A\n" +
                "§7direct hit will cause the enemy\n" +
                "§7to take an additional §c15% §7extra\n" +
                "§7damage." +
                "\n\n" +
                "§7Has an optimal range of §e" + MAX_FULL_DAMAGE_DISTANCE + " §7blocks.";
    }
	
}
