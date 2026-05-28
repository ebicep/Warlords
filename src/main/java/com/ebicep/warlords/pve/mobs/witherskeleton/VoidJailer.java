package com.ebicep.warlords.pve.mobs.witherskeleton;

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
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class VoidJailer extends AbstractMob implements ChampionMob {

    private static final int PRISON_TARGET_RANGE = 20;
    private static final int INITIAL_PRISON_DELAY_TICKS = 5 * 20;
    private static final int PRISON_COOLDOWN_TICKS = 20 * 20;
    private static final int PRISON_CAST_TICKS = 2 * 20;
    private static final int PRISON_DURATION_TICKS = 10 * 20;
    private static final double PRISON_RADIUS = 5;
    private static final int PRISON_BREAK_DAMAGE = 6000;
    private static final int ESCAPE_PUNISH_DAMAGE = 1000;
    private static final int ESCAPE_PUNISH_COOLDOWN_TICKS = 20;
    private static final float IMPRISONED_PLAYER_DAMAGE_MULTIPLIER = .2f;

    private int prisonCooldownTicks = INITIAL_PRISON_DELAY_TICKS;
    private int prisonCastTicks = 0;
    private int prisonTicksLeft = 0;
    private int escapePunishCooldownTicks = 0;

    private WarlordsEntity castTarget;
    private WarlordsEntity prisonTarget;
    private Location prisonCenter;
    private float prisonDamageTaken = 0;

    public VoidJailer(Location spawnLocation) {
        super(
                spawnLocation,
                "Void Jailer",
                9000,
                0.14f,
                20,
                700,
                800
        );
    }

    public VoidJailer(
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
        return Mob.VOID_JAILER;
    }

    @Override
    public double getDefaultAttackRange() {
        return 2.4;
    }

    @Override
    public double getMobScale() {
        return 1.3;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 2, 0.5f);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (warlordsNPC == null || warlordsNPC.isDead() || !warlordsNPC.isActive()) {
            return;
        }

        if (prisonTarget != null) {
            tickVoidPrison();
            return;
        }

        if (castTarget != null) {
            tickPrisonCast();
            return;
        }

        if (prisonCooldownTicks > 0) {
            prisonCooldownTicks--;
            return;
        }

        startPrisonCast();
    }

    private void startPrisonCast() {
        List<WarlordsEntity> targets = PlayerFilter
                .entitiesAround(warlordsNPC, PRISON_TARGET_RANGE, PRISON_TARGET_RANGE, PRISON_TARGET_RANGE)
                .aliveEnemiesOf(warlordsNPC)
                .filter(warlordsEntity -> warlordsEntity instanceof WarlordsPlayer)
                .toList();

        if (targets.isEmpty()) {
            prisonCooldownTicks = 20;
            return;
        }

        castTarget = targets.get(ThreadLocalRandom.current().nextInt(targets.size()));
        prisonCastTicks = PRISON_CAST_TICKS;

        toggleStun(true);

        castTarget.sendMessage(Component.text("The Void Jailer begins sealing you in a prison.", NamedTextColor.DARK_PURPLE));
        castTarget.playSound(castTarget.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1, 0.5f);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_SCULK_SHRIEKER_SHRIEK, 2, 0.5f);
    }

    private void tickPrisonCast() {
        if (!isValidTarget(castTarget, PRISON_TARGET_RANGE + 8)) {
            clearPrisonCast(20);
            return;
        }

        prisonCastTicks--;

        if (prisonCastTicks % 5 == 0) {
            drawPrisonRing(castTarget.getLocation(), PRISON_RADIUS, Particle.PORTAL);
            castTarget.getWorld().spawnParticle(
                    Particle.SQUID_INK,
                    castTarget.getLocation().clone().add(0, 1.1, 0),
                    8,
                    .25,
                    .35,
                    .25,
                    .01
            );
        }

        if (prisonCastTicks <= 0) {
            activateVoidPrison();
        }
    }

    private void activateVoidPrison() {
        if (!isValidTarget(castTarget, PRISON_TARGET_RANGE + 8)) {
            clearPrisonCast(20);
            return;
        }

        prisonTarget = castTarget;
        prisonCenter = castTarget.getLocation().clone();
        prisonTicksLeft = PRISON_DURATION_TICKS;
        prisonDamageTaken = 0;
        escapePunishCooldownTicks = 0;

        prisonTarget.addSpeedModifier(warlordsNPC, "Void Prison", -70, PRISON_DURATION_TICKS);
        prisonTarget.sendMessage(Component.text("You are trapped in a Void Prison. Your allies must damage the Jailer to break it.", NamedTextColor.RED));
        prisonTarget.playSound(prisonTarget.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.45f);

        Utils.playGlobalSound(prisonCenter, Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 2, 0.6f);

        clearPrisonCast(PRISON_COOLDOWN_TICKS);
    }

    private void tickVoidPrison() {
        if (!isValidTarget(prisonTarget, PRISON_TARGET_RANGE + 12)) {
            endVoidPrison(false);
            return;
        }

        prisonTicksLeft--;

        if (escapePunishCooldownTicks > 0) {
            escapePunishCooldownTicks--;
        }

        if (prisonTicksLeft % 5 == 0) {
            drawPrisonRing(prisonCenter, PRISON_RADIUS, Particle.REVERSE_PORTAL);
            drawPrisonPillars();
        }

        keepTargetInsidePrison();

        if (prisonTicksLeft <= 0) {
            endVoidPrison(false);
        }
    }

    private void keepTargetInsidePrison() {
        if (prisonCenter == null || prisonTarget == null) {
            return;
        }

        if (prisonTarget.getLocation().distanceSquared(prisonCenter) <= PRISON_RADIUS * PRISON_RADIUS) {
            return;
        }

        prisonTarget.getEntity().teleport(prisonCenter);
        prisonTarget.playSound(prisonTarget.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 0.35f);

        if (escapePunishCooldownTicks > 0) {
            return;
        }

        prisonTarget.addInstance(InstanceBuilder
                .damage()
                .cause("Void Prison")
                .source(warlordsNPC)
                .value(ESCAPE_PUNISH_DAMAGE)
        );

        escapePunishCooldownTicks = ESCAPE_PUNISH_COOLDOWN_TICKS;
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        if (self != warlordsNPC) {
            return;
        }
        if (prisonTarget == null) {
            return;
        }
        if (attacker == null) {
            return;
        }
        if (!event.isDamageInstance()) {
            return;
        }

        if (attacker == prisonTarget) {
            event.applyToMinMax(floatModifiable -> floatModifiable.addModifier(
                    FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                    "Jailer's Focus",
                    IMPRISONED_PLAYER_DAMAGE_MULTIPLIER
            ));
        }

        float damageEstimate = (event.getMin().getCalculatedValue() + event.getMax().getCalculatedValue()) / 2f;
        prisonDamageTaken += Math.max(0, damageEstimate);

        if (prisonDamageTaken >= PRISON_BREAK_DAMAGE) {
            endVoidPrison(true);
        }
    }

    private void endVoidPrison(boolean broken) {
        if (prisonTarget != null) {
            if (broken) {
                prisonTarget.sendMessage(Component.text("The Void Prison shatters.", NamedTextColor.GREEN));
                prisonTarget.playSound(prisonTarget.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 0.7f);
            } else {
                prisonTarget.sendMessage(Component.text("The Void Prison fades.", NamedTextColor.GRAY));
                prisonTarget.playSound(prisonTarget.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1, 0.6f);
            }
        }

        if (prisonCenter != null) {
            prisonCenter.getWorld().spawnParticle(Particle.SQUID_INK, prisonCenter.clone().add(0, 1, 0), 32, 1.2, .8, 1.2, .04);
            Utils.playGlobalSound(prisonCenter, broken ? Sound.BLOCK_GLASS_BREAK : Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 2, 0.6f);
        }

        prisonTarget = null;
        prisonCenter = null;
        prisonTicksLeft = 0;
        prisonDamageTaken = 0;
        escapePunishCooldownTicks = 0;
        prisonCooldownTicks = PRISON_COOLDOWN_TICKS;
    }

    private void clearPrisonCast(int cooldownTicks) {
        castTarget = null;
        prisonCastTicks = 0;
        prisonCooldownTicks = cooldownTicks;
        toggleStun(false);
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

    private void drawPrisonRing(Location center, double radius, Particle particle) {
        for (int i = 0; i < 48; i++) {
            double angle = Math.PI * 2 * i / 48;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            Location particleLocation = center.clone().add(x, .15, z);
            center.getWorld().spawnParticle(particle, particleLocation, 1, 0, 0, 0, 0);
        }
    }

    private void drawPrisonPillars() {
        if (prisonCenter == null) {
            return;
        }

        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * 2 * i / 8;
            double x = Math.cos(angle) * PRISON_RADIUS;
            double z = Math.sin(angle) * PRISON_RADIUS;

            for (double y = .25; y <= 2.5; y += .45) {
                Location particleLocation = prisonCenter.clone().add(x, y, z);
                prisonCenter.getWorld().spawnParticle(Particle.SQUID_INK, particleLocation, 1, 0, 0, 0, 0);
            }
        }
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        endVoidPrison(true);
        super.onDeath(killer, deathLocation, option);
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_ENDERMAN_DEATH, 2, 0.5f);
    }

    @Override
    public void cleanup(PveOption pveOption) {
        clearPrisonCast(0);
        endVoidPrison(false);
        super.cleanup(pveOption);
    }

}
