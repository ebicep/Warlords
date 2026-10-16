package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Floor 20 boss. Dodged meteors, a telegraphed pull, and two eclipses that are only safe inside a circle of light.
 */
public class AbyssDescendant extends DescendantBoss {

    private static final int METEOR_WINDUP_TICKS = 2 * 20;
    private static final int GRASP_WINDUP_TICKS = 30;
    private static final int ECLIPSE_TICKS = 6 * 20;
    private static final double METEOR_RADIUS = 3;
    private static final double GRASP_RANGE = 12;
    private static final double ECLIPSE_SAFE_RADIUS = 3.5;

    private int cooldownTicks = 5 * 20;
    private boolean castMeteors = true;
    private boolean eclipsedAtSixty;
    private boolean eclipsedAtThirty;
    private final List<Location> meteors = new ArrayList<>();
    private int meteorTicks;
    private int graspTicks;
    private int eclipseTicks;
    private int eclipseLockout;
    private Location eclipseCenter;

    public AbyssDescendant(Location spawnLocation) {
        this(spawnLocation, "Abyss Descendant", 36000, 0.28f, 18, 750, 1000);
    }

    public AbyssDescendant(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.ABYSS_DESCENDANT;
    }

    @Override
    public Component getDescription() {
        return Component.text("The last to descend", NamedTextColor.DARK_PURPLE);
    }

    @Override
    public double getMobScale() {
        return 1.35;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_WARDEN_ROAR, 1.5f, 0.6f);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (warlordsNPC == null || warlordsNPC.isDead()) {
            return;
        }
        if (ticksElapsed % 10 == 0) {
            tickAura();
        }
        if (eclipseLockout > 0) {
            eclipseLockout--;
        } else if (tryStartEclipse()) {
            return;
        }
        if (eclipseTicks > 0) {
            tickEclipse();
            return;
        }
        if (meteorTicks > 0) {
            tickMeteors();
            return;
        }
        if (graspTicks > 0) {
            tickGrasp();
            return;
        }
        if (cooldownTicks > 0) {
            cooldownTicks--;
            return;
        }
        if (castMeteors) {
            startMeteors();
        } else {
            startGrasp();
        }
        castMeteors = !castMeteors;
    }

    private void startMeteors() {
        meteors.clear();
        List<WarlordsEntity> targets = enemies(40);
        if (!targets.isEmpty()) {
            meteors.add(targets.getFirst().getLocation().clone());
        }
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Location origin = warlordsNPC.getLocation();
        while (meteors.size() < 3) {
            meteors.add(origin.clone().add(random.nextDouble(-7, 7), 0, random.nextDouble(-7, 7)));
        }
        meteorTicks = METEOR_WINDUP_TICKS;
        announce("Darkness falls. Leave the marked ground.");
        Utils.playGlobalSound(origin, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 2, 0.4f);
    }

    private void tickMeteors() {
        meteorTicks--;
        if (meteorTicks % 5 == 0) {
            for (Location meteor : meteors) {
                drawRing(meteor, METEOR_RADIUS, Particle.WITCH);
                EffectUtils.displayParticle(Particle.PORTAL, meteor.clone().add(0, 2.5, 0), 6, 0.2, 0.4, 0.2, 0.01);
            }
        }
        if (meteorTicks > 0) {
            return;
        }
        for (Location meteor : meteors) {
            damageAround(meteor, METEOR_RADIUS, 1300f, "Falling Dark");
            EffectUtils.playSphereAnimation(meteor, METEOR_RADIUS, Particle.WITCH, 1);
        }
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.4f, 0.5f);
        meteors.clear();
        cooldownTicks = 6 * 20;
    }

    private void startGrasp() {
        if (enemies(GRASP_RANGE + 2).isEmpty()) {
            cooldownTicks = 20;
            return;
        }
        graspTicks = GRASP_WINDUP_TICKS;
        toggleStun(true);
        announce("The Abyss Descendant reaches for you. Get away.");
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 2, 0.4f);
    }

    private void tickGrasp() {
        graspTicks--;
        if (graspTicks % 5 == 0) {
            drawRing(warlordsNPC.getLocation(), GRASP_RANGE, Particle.PORTAL);
        }
        if (graspTicks > 0) {
            return;
        }
        toggleStun(false);
        for (WarlordsEntity enemy : enemies(GRASP_RANGE)) {
            Utils.addKnockback("Abyssal Grasp", warlordsNPC.getLocation(), enemy, 1.8, 0.1);
            enemy.addSpeedModifier(warlordsNPC, "Abyssal Grasp", -50, 40);
        }
        damageAround(warlordsNPC.getLocation(), GRASP_RANGE, 1500f, "Abyssal Grasp");
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.5f, 0.5f);
        cooldownTicks = 7 * 20;
    }

    private boolean tryStartEclipse() {
        if (eclipseTicks > 0) {
            return false;
        }
        float health = warlordsNPC.getCurrentHealth() / warlordsNPC.getMaxHealth();
        boolean start = (!eclipsedAtSixty && health <= 0.6f) || (!eclipsedAtThirty && health <= 0.3f);
        if (!start) {
            return false;
        }
        if (!eclipsedAtSixty) {
            eclipsedAtSixty = true;
        } else {
            eclipsedAtThirty = true;
        }
        double angle = ThreadLocalRandom.current().nextDouble(Math.PI * 2);
        eclipseCenter = warlordsNPC.getLocation().clone().add(Math.cos(angle) * 6, 0, Math.sin(angle) * 6);
        eclipseTicks = ECLIPSE_TICKS;
        meteorTicks = 0;
        graspTicks = 0;
        toggleStun(false);
        announce("Eclipse. Stand in the fading light.");
        Utils.playGlobalSound(eclipseCenter, Sound.BLOCK_BEACON_ACTIVATE, 1.5f, 0.5f);
        return true;
    }

    private void tickEclipse() {
        eclipseTicks--;
        if (eclipseTicks % 5 == 0) {
            drawRing(eclipseCenter, ECLIPSE_SAFE_RADIUS, Particle.END_ROD);
        }
        if (eclipseTicks % 20 == 0) {
            enemiesOutsideEclipse().forEach(enemy -> enemy.addInstance(InstanceBuilder
                    .damage()
                    .cause("Eclipse")
                    .source(warlordsNPC)
                    .value(450)
            ));
        }
        if (eclipseTicks > 0) {
            return;
        }
        announce("The light returns.");
        eclipseLockout = 4 * 20;
        cooldownTicks = 4 * 20;
    }

    private List<WarlordsEntity> enemiesOutsideEclipse() {
        double safeRadiusSquared = ECLIPSE_SAFE_RADIUS * ECLIPSE_SAFE_RADIUS;
        return enemies(40).stream()
                          .filter(enemy -> enemy.getLocation().distanceSquared(eclipseCenter) > safeRadiusSquared)
                          .toList();
    }
}
