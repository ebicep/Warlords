package com.ebicep.warlords.pve.mobs.creaking;

import com.ebicep.customentities.nms.pve.pathfindergoals.NPCTargetAggroWarlordsEntityGoal;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.EliteMob;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class SovereignGuardian extends AbstractMob implements EliteMob {

    private boolean beingWatched;

    public SovereignGuardian(Location spawnLocation) {
        super(
                spawnLocation,
                "Sovereign Guardian",
                11000,
                0.75f,
                20,
                1200,
                1600
        );
    }

    public SovereignGuardian(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        );
    }

    @Override
    public void giveGoals() {
        npc.getDefaultBehaviorController().addBehavior(
                new NPCTargetAggroWarlordsEntityGoal(
                        npc,
                        70,
                        target -> !beingWatched
                )
        );
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        beingWatched = isBeingWatched(option);

        if (!beingWatched || npc == null || !npc.isSpawned()) {
            return;
        }

        npc.getNavigator().cancelNavigation();

        Entity entity = npc.getEntity();
        Vector velocity = entity.getVelocity();
        entity.setVelocity(new Vector(0, velocity.getY(), 0));
    }

    private boolean isBeingWatched(PveOption option) {
        if (npc == null || !npc.isSpawned()) {
            return false;
        }

        Entity guardian = npc.getEntity();
        Location guardianCenter = guardian.getLocation().add(0, guardian.getHeight() * 0.5, 0);

        return option.getGame()
                .warlordsPlayers()
                .anyMatch(warlordsPlayer -> {
                    if (!(warlordsPlayer.getEntity() instanceof Player player)) {
                        return false;
                    }

                    if (player.isDead() || player.getGameMode() == GameMode.SPECTATOR) {
                        return false;
                    }

                    if (player.getWorld() != guardian.getWorld()) {
                        return false;
                    }

                    Location eyeLocation = player.getEyeLocation();
                    // blocks
                    double distanceSquared = eyeLocation.distanceSquared(guardianCenter);
                    if (distanceSquared > 20 * 20) {
                        return false;
                    }

                    if (!player.hasLineOfSight(guardian)) {
                        return false;
                    }

                    Vector toGuardian = guardianCenter.toVector().subtract(eyeLocation.toVector());
                    double distance = toGuardian.length();

                    if (distance == 0) {
                        return true;
                    }

                    Vector lookDirection = eyeLocation.getDirection().normalize();
                    Vector targetDirection = toGuardian.multiply(1 / distance);

                    double dot = lookDirection.dot(targetDirection);

                    return dot > 0.4;
                });
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.SOVEREIGN_GUARDIAN;
    }
}