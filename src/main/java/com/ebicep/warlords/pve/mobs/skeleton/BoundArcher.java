package com.ebicep.warlords.pve.mobs.skeleton;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.AdvancedMob;
import com.ebicep.warlords.pve.mobs.tiers.EliteMob;
import com.ebicep.warlords.pve.mobs.witherskeleton.Soulbinder;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

public class BoundArcher extends AbstractMob implements EliteMob {

    private static final int TARGET_RANGE = 30;
    private static final int PREFERRED_MIN_RANGE = 8;
    private static final int PREFERRED_MAX_RANGE = 16;
    private static final int SHOT_RANGE = 24;
    private static final int SHOT_COOLDOWN_TICKS = 3 * 20;
    private static final int INITIAL_SHOT_DELAY_TICKS = 2 * 20;
    private static final int SOUL_ARROW_DAMAGE = 400;
    private static final int SOUL_ARROW_SLOW_PERCENT = -20;
    private static final int SOUL_ARROW_SLOW_TICKS = 30;
    private static final double BACKSTEP_STRENGTH = .18;

    @Nullable
    private final Soulbinder owner;

    private WarlordsEntity target;
    private int targetRefreshTicks = 0;
    private int shotCooldownTicks = INITIAL_SHOT_DELAY_TICKS;

    public BoundArcher(Location spawnLocation) {
        this(spawnLocation, null);
    }

    public BoundArcher(Location spawnLocation, @Nullable Soulbinder owner) {
        this(
                spawnLocation,
                "Bound Archer",
                4500,
                .09f,
                0,
                150,
                250,
                owner
        );
    }

    public BoundArcher(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        this(
                spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                null
        );
    }

    public BoundArcher(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            @Nullable Soulbinder owner
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
        this.owner = owner;
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.BOUND_ARCHER;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_SKELETON_AMBIENT, 2, .6f);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (warlordsNPC == null || warlordsNPC.isDead() || !warlordsNPC.isActive()) {
            return;
        }

        tickTargeting();
        tickMovement();
        tickSoulArrow();
    }

    private void tickTargeting() {
        if (targetRefreshTicks > 0) {
            targetRefreshTicks--;
            if (isValidTarget(target, TARGET_RANGE)) {
                return;
            }
        }

        targetRefreshTicks = 10;
        target = findTarget();
    }

    @Nullable
    private WarlordsEntity findTarget() {
        List<WarlordsEntity> targets = PlayerFilter
                .entitiesAround(warlordsNPC, TARGET_RANGE, TARGET_RANGE, TARGET_RANGE)
                .aliveEnemiesOf(warlordsNPC)
                .filter(warlordsEntity -> warlordsEntity instanceof WarlordsPlayer)
                .toList();

        if (targets.isEmpty()) {
            return null;
        }

        return targets.stream()
                .min(Comparator.comparingDouble(target -> target.getLocation().distanceSquared(warlordsNPC.getLocation())))
                .orElse(null);
    }

    private void tickMovement() {
        if (!isValidTarget(target, TARGET_RANGE)) {
            removeTarget();
            return;
        }

        double distanceSquared = target.getLocation().distanceSquared(warlordsNPC.getLocation());

        if (distanceSquared < PREFERRED_MIN_RANGE * PREFERRED_MIN_RANGE) {
            removeTarget();
            moveAwayFromTarget(target);
            return;
        }

        if (distanceSquared > PREFERRED_MAX_RANGE * PREFERRED_MAX_RANGE) {
            setTarget(target);
            return;
        }

        removeTarget();
    }

    private void tickSoulArrow() {
        if (shotCooldownTicks > 0) {
            shotCooldownTicks--;
            return;
        }

        if (!isValidTarget(target, SHOT_RANGE)) {
            return;
        }

        fireSoulArrow(target);
        shotCooldownTicks = SHOT_COOLDOWN_TICKS;
    }

    private void fireSoulArrow(WarlordsEntity target) {
        Location from = warlordsNPC.getLocation().clone().add(0, 1.25, 0);
        Location to = target.getLocation().clone().add(0, 1.1, 0);

        drawParticleLine(from, to, Particle.SOUL_FIRE_FLAME);

        target.addInstance(InstanceBuilder
                .damage()
                .cause("Soul Arrow")
                .source(warlordsNPC)
                .value(SOUL_ARROW_DAMAGE)
        );

        target.addSpeedModifier(warlordsNPC, "Soul Arrow", SOUL_ARROW_SLOW_PERCENT, SOUL_ARROW_SLOW_TICKS);
        target.playSound(target.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, .6f);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_SKELETON_SHOOT, 2, .6f);
    }

    private void moveAwayFromTarget(WarlordsEntity target) {
        Vector direction = warlordsNPC.getLocation().toVector().subtract(target.getLocation().toVector());
        direction.setY(0);

        if (direction.lengthSquared() == 0) {
            return;
        }

        direction.normalize().multiply(BACKSTEP_STRENGTH);
        warlordsNPC.getEntity().setVelocity(direction);
    }

    private boolean isValidTarget(@Nullable WarlordsEntity target, int range) {
        if (target == null) {
            return false;
        }
        if (!target.isAlive() || !target.isActive()) {
            return false;
        }
        if (target.getWorld() != warlordsNPC.getWorld()) {
            return false;
        }
        if (!warlordsNPC.isEnemyAlive(target)) {
            return false;
        }
        return target.getLocation().distanceSquared(warlordsNPC.getLocation()) <= range * range;
    }

    private void drawParticleLine(Location from, Location to, Particle particle) {
        Vector direction = to.toVector().subtract(from.toVector());
        double length = direction.length();

        if (length == 0) {
            return;
        }

        direction.normalize();

        for (double distance = 0; distance < length; distance += .35) {
            from.getWorld().spawnParticle(particle, from.clone().add(direction.clone().multiply(distance)), 1, 0, 0, 0, 0);
        }
    }

    private void notifyOwner() {
        if (owner != null) {
            owner.onBoundArcherRemoved(this);
        }
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        notifyOwner();
        super.onDeath(killer, deathLocation, option);
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_SKELETON_DEATH, 2, .7f);
    }

    @Override
    public void cleanup(PveOption pveOption) {
        notifyOwner();
        super.cleanup(pveOption);
    }

}
