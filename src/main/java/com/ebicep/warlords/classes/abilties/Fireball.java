package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.internal.AbstractProjectileBase;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Fireball extends AbstractProjectileBase {

    private static final int MAX_FULL_DAMAGE_DISTANCE = 50;
    private static final double DIRECT_HIT_MULTIPLIER = 1.15;
    private static final float HITBOX = 3.75f;

    public Fireball() {
        super("Fireball", -334.4f, -433.4f, 0, 70, 20, 175, 2, 250, false);
    }

    @Override
    protected String getActivationSound() {
        return "mage.fireball.activation";
    }

    @Override
    protected void playEffect(Location currentLocation, int animationTimer) {
        ParticleEffect.DRIP_LAVA.display(0, 0, 0, 0.35F, 5, currentLocation, 500);
        ParticleEffect.SMOKE_NORMAL.display(0, 0, 0, 0.001F, 7, currentLocation, 500);
        ParticleEffect.FLAME.display(0, 0, 0, 0.06F, 1, currentLocation, 500);
    }

    @Override
    protected void onHit(WarlordsPlayer shooter, Location currentLocation, Location startingLocation, WarlordsPlayer victim) {
        ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0.5F, 1, currentLocation, 500);
        ParticleEffect.LAVA.display(0.5F, 0, 0.5F, 2F, 10, currentLocation, 500);
        ParticleEffect.CLOUD.display(0.3F, 0.3F, 0.3F, 1F, 3, currentLocation, 500);

        for (Player player1 : currentLocation.getWorld().getPlayers()) {
            player1.playSound(currentLocation, "mage.fireball.impact", 2, 1);
        }

        double distanceSquared = currentLocation.distanceSquared(startingLocation);
        double toReduceBy = MAX_FULL_DAMAGE_DISTANCE * MAX_FULL_DAMAGE_DISTANCE > distanceSquared ? 1 : 
            1 - (Math.sqrt(distanceSquared) - MAX_FULL_DAMAGE_DISTANCE) / 100.;
        if (toReduceBy < 0) toReduceBy = 0;
        if (victim != null) {
            victim.addHealth(
                    shooter,
                    name,
                    (float) (minDamageHeal * DIRECT_HIT_MULTIPLIER * toReduceBy),
                    (float) (maxDamageHeal * DIRECT_HIT_MULTIPLIER * toReduceBy),
                    critChance,
                    critMultiplier
            );
        }
        
        for (WarlordsPlayer nearEntity : PlayerFilter
                .entitiesAround(currentLocation, HITBOX, HITBOX, HITBOX)
                .excluding(victim)
                .aliveEnemiesOf(shooter)
        ) {
            nearEntity.addHealth(
                    shooter,
                    name,
                    (float) (minDamageHeal * toReduceBy),
                    (float) (maxDamageHeal * toReduceBy),
                    critChance,
                    critMultiplier
            );
        }
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Shoot a fireball that will explode\n" +
                "§7for §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " §7damage. A\n" +
                "§7direct hit will cause the enemy\n" +
                "§7to take an additional §c15% §7extra\n" +
                "§7damage. §7Has an optimal range of §e" + MAX_FULL_DAMAGE_DISTANCE + " §7blocks.";
    }
	
}
