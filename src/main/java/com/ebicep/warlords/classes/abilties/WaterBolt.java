package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.internal.AbstractProjectileBase;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WaterBolt extends AbstractProjectileBase {

    private static final int MAX_FULL_DAMAGE_DISTANCE = 40;
    private static final double DIRECT_HIT_MULTIPLIER = 1.15;
    private static final float HITBOX = 3.75f;

    public WaterBolt() {
        super("Water Bolt", 328, 452, 0, 85, 20, 175, 2, 90, true);
    }

    @Override
    protected String getActivationSound() {
        return "mage.waterbolt.activation";
    }

    @Override
    protected void playEffect(Location currentLocation, int animationTimer) {
        ParticleEffect.DRIP_WATER.display(0.3f, 0.3f, 0.3f, 0.1F, 2, currentLocation, 500);
        ParticleEffect.ENCHANTMENT_TABLE.display(0, 0, 0, 0.1F, 1, currentLocation, 500);
        ParticleEffect.VILLAGER_HAPPY.display(0, 0, 0, 0.1F, 1, currentLocation, 500);
        ParticleEffect.CLOUD.display(0, 0, 0, 0F, 1, currentLocation, 500);
    }

    @Override
    protected void onHit(WarlordsPlayer shooter, Location currentLocation, Location startingLocation, WarlordsPlayer victim) {
        ParticleEffect.HEART.display(1, 1, 1, 0.2F, 3, currentLocation, 500);
        ParticleEffect.VILLAGER_HAPPY.display(1, 1, 1, 0.2F, 5, currentLocation, 500);

        for (Player player1 : shooter.getWorld().getPlayers()) {
            player1.playSound(currentLocation, "mage.waterbolt.impact", 2, 1);
        }

        double distanceSquared = currentLocation.distanceSquared(startingLocation);
        double toReduceBy = MAX_FULL_DAMAGE_DISTANCE * MAX_FULL_DAMAGE_DISTANCE > distanceSquared ? 1 : 
            1 - (Math.sqrt(distanceSquared) - MAX_FULL_DAMAGE_DISTANCE) / 100.;
        if (toReduceBy < 0) toReduceBy = 0;
        if (victim != null) {
            if (victim.isTeammateAlive(shooter)) {
                victim.addHealth(shooter,
                        name,
                        (float) (minDamageHeal * DIRECT_HIT_MULTIPLIER * toReduceBy),
                        (float) (maxDamageHeal * DIRECT_HIT_MULTIPLIER * toReduceBy),
                        critChance,
                        critMultiplier
                );
            } else {
                victim.addHealth(shooter,
                        name,
                        (float) (-231 * DIRECT_HIT_MULTIPLIER * toReduceBy),
                        (float) (-299 * DIRECT_HIT_MULTIPLIER * toReduceBy),
                        critChance,
                        critMultiplier
                );
            }
        }
        for (WarlordsPlayer nearEntity : PlayerFilter
                .entitiesAround(currentLocation, HITBOX, HITBOX, HITBOX)
                .excluding(victim)
                .isAlive()
        ) {
            if (nearEntity.isTeammateAlive(shooter)) {
                nearEntity.addHealth(
                        shooter,
                        name,
                        (float) (minDamageHeal * toReduceBy),
                        (float) (maxDamageHeal * toReduceBy),
                        critChance,
                        critMultiplier
                );
            } else {
                nearEntity.addHealth(
                        shooter,
                        name,
                        (float) (-231 * toReduceBy),
                        (float) (-299 * toReduceBy),
                        critChance,
                        critMultiplier
                );
            }
        }
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Shoot a bolt of water that will burst\n" +
                "§7for §c231 §7- §c299 §7damage and restore\n" +
                "§a" + minDamageHeal + " §7- §a" + maxDamageHeal + " §7health to allies. A\n" +
                "§7direct hit will cause §a15% §7increased\n" +
                "§7damage or healing for the target hit.\n" +
                "§7Has an optimal range of §e" + MAX_FULL_DAMAGE_DISTANCE + " §7blocks.";
    }
	
}
