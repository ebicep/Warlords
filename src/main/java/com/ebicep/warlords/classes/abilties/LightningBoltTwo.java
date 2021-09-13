package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.internal.AbstractProjectileBase;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LightningBoltTwo extends AbstractProjectileBase {

    // REWRITE WIP

    private static final int MAX_FULL_DAMAGE_DISTANCE = 60;
    private static final float HITBOX = 4;

    public LightningBoltTwo() {
        super("Lightning Bolt", -334.4f, -433.4f, 0, 70, 20, 175, 2, 300, false);
    }

    @Override
    protected String getActivationSound() {
        return "shaman.lightningbolt.activation";
    }

    @Override
    protected void playEffect(Location currentLocation, int animationTimer) {

    }

    @Override
    protected void onHit(WarlordsPlayer shooter, Location currentLocation, Location startingLocation, WarlordsPlayer victim) {
        ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0.5F, 1, currentLocation, 500);

        for (Player player1 : currentLocation.getWorld().getPlayers()) {
            player1.playSound(currentLocation, "shaman.lightningbolt.impact", 2, 1);
        }

        if (victim != null) {
            victim.addHealth(
                    shooter,
                    name,
                    minDamageHeal,
                    maxDamageHeal,
                    critChance,
                    critMultiplier,
                    false
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
                    minDamageHeal,
                    maxDamageHeal,
                    critChance,
                    critMultiplier,
                    false
            );
        }
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Shoot a fireball that will explode\n" +
                "§7for §c" + -minDamageHeal + " §7- §c" + -maxDamageHeal + " §7damage. A\n" +
                "§7direct hit will cause the enemy\n" +
                "§7to take an additional §c15% §7extra\n" +
                "§7damage." +
                "\n\n" +
                "§7Has an optimal range of §e" + MAX_FULL_DAMAGE_DISTANCE + " §7blocks.";
    }

}
