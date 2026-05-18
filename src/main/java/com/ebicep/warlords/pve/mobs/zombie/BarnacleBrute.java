package com.ebicep.warlords.pve.mobs.zombie;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.AdvancedMob;
import com.ebicep.warlords.pve.mobs.tiers.ChampionMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;

public class BarnacleBrute extends AbstractMob implements ChampionMob {

    private static final int HOOK_RANGE = 12;
    private static final int HOOK_COOLDOWN_TICKS = 10 * 20;
    private static final int INITIAL_HOOK_DELAY_TICKS = 4 * 20;
    private static final int HOOK_WINDUP_TICKS = 30;
    private static final double HOOK_PULL_STRENGTH = 1.6;

    private static final int CRUSHING_GRIP_WINDOW_TICKS = 4 * 20;

    private int hookCooldownTicks = INITIAL_HOOK_DELAY_TICKS;
    private int hookWindupTicks = 0;
    private WarlordsEntity hookTarget;

    private WarlordsEntity grippedTarget;
    private int gripTicksLeft = 0;

    public BarnacleBrute(Location spawnLocation) {
        super(
                spawnLocation,
                "Barnacle Brute",
                6000,
                0.18f,
                10,
                450,
                650
        );
    }

    public BarnacleBrute(
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
    public Mob getMobRegistry() {
        return Mob.BARNACLE_BRUTE;
    }

    @Override
    public double getDefaultAttackRange() {
        return 2.4;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_DROWNED_AMBIENT_WATER, 2, 0.7f);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (warlordsNPC == null || warlordsNPC.isDead() || !warlordsNPC.isActive()) {
            return;
        }

        tickCrushingGrip();
        tickHookline();
    }

    private void tickHookline() {
        if (hookTarget != null) {
            tickHookWindup();
            return;
        }

        if (hookCooldownTicks > 0) {
            hookCooldownTicks--;
            return;
        }

        startHookline();
    }

    private void startHookline() {
        List<WarlordsEntity> targets = PlayerFilter
                .entitiesAround(warlordsNPC, HOOK_RANGE, HOOK_RANGE, HOOK_RANGE)
                .aliveEnemiesOf(warlordsNPC)
                .filter(warlordsEntity -> warlordsEntity instanceof WarlordsPlayer)
                .toList();

        if (targets.isEmpty()) {
            hookCooldownTicks = 20;
            return;
        }

        hookTarget = targets.stream()
                .max(Comparator.comparingDouble(target -> target.getLocation().distanceSquared(warlordsNPC.getLocation())))
                .orElse(null);

        if (hookTarget == null) {
            hookCooldownTicks = 20;
            return;
        }

        hookWindupTicks = HOOK_WINDUP_TICKS;

        hookTarget.playSound(hookTarget.getLocation(), Sound.ENTITY_FISHING_BOBBER_THROW, 1, 0.6f);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_CHAIN_PLACE, 2, 0.7f);
    }

    private void tickHookWindup() {
        if (!isValidTarget(hookTarget, HOOK_RANGE + 4)) {
            clearHookline();
            return;
        }

        hookWindupTicks--;

        if (hookWindupTicks % 2 == 0) {
            drawHookLine(warlordsNPC.getLocation().clone().add(0, 1.2, 0), hookTarget.getLocation().clone().add(0, 1.1, 0), Particle.CRIT);
        }

        if (hookWindupTicks <= 0) {
            completeHookline();
        }
    }

    private void completeHookline() {
        if (!isValidTarget(hookTarget, HOOK_RANGE + 4)) {
            clearHookline();
            return;
        }

        pullTarget(hookTarget);

        grippedTarget = hookTarget;
        gripTicksLeft = CRUSHING_GRIP_WINDOW_TICKS;

        hookTarget.addSpeedModifier(warlordsNPC, "Hookline", -30, 2 * 20);
        hookTarget.sendMessage(Component.text("The Barnacle Brute drags you in.", NamedTextColor.RED));
        hookTarget.playSound(hookTarget.getLocation(), Sound.ENTITY_FISHING_BOBBER_RETRIEVE, 1, 0.7f);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_DROWNED_SHOOT, 2, 0.8f);

        clearHookline();
    }

    private void pullTarget(WarlordsEntity target) {
        Vector pull = warlordsNPC.getLocation().toVector().subtract(target.getLocation().toVector());

        if (pull.lengthSquared() == 0) {
            return;
        }

        pull.normalize().multiply(HOOK_PULL_STRENGTH);
        pull.setY(0.2);

        target.getEntity().setVelocity(pull);
    }

    private void tickCrushingGrip() {
        if (grippedTarget == null) {
            return;
        }

        if (!isValidTarget(grippedTarget, HOOK_RANGE + 6)) {
            clearCrushingGrip();
            return;
        }

        gripTicksLeft--;

        if (gripTicksLeft % 10 == 0) {
            grippedTarget.getWorld().spawnParticle(
                    Particle.SQUID_INK,
                    grippedTarget.getLocation().clone().add(0, 1.1, 0),
                    8,
                    .25,
                    .35,
                    .25,
                    .01
            );
        }

        if (gripTicksLeft <= 0) {
            clearCrushingGrip();
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        if (attacker != warlordsNPC) {
            return;
        }
        if (receiver != grippedTarget) {
            return;
        }
        if (gripTicksLeft <= 0) {
            clearCrushingGrip();
            return;
        }

        Utils.playGlobalSound(receiver.getLocation(), Sound.ENTITY_DROWNED_HURT, 2, 0.6f);

        clearCrushingGrip();
    }

    private boolean isValidTarget(WarlordsEntity target, int range) {
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

    private void drawHookLine(Location from, Location to, Particle particle) {
        Vector direction = to.toVector().subtract(from.toVector());
        double length = direction.length();

        if (length == 0) {
            return;
        }

        direction.normalize();

        for (double distance = 0; distance < length; distance += .35) {
            Location particleLocation = from.clone().add(direction.clone().multiply(distance));
            from.getWorld().spawnParticle(particle, particleLocation, 1, 0, 0, 0, 0);
        }
    }

    private void clearHookline() {
        hookTarget = null;
        hookWindupTicks = 0;
        hookCooldownTicks = HOOK_COOLDOWN_TICKS;
    }

    private void clearCrushingGrip() {
        grippedTarget = null;
        gripTicksLeft = 0;
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_DROWNED_DEATH, 2, 0.5f);
    }

    @Override
    public void cleanup(PveOption pveOption) {
        clearHookline();
        clearCrushingGrip();
        super.cleanup(pveOption);
    }

}
