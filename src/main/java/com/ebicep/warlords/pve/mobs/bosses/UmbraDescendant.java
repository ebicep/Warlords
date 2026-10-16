package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.List;

/**
 * Floor 10 boss. Alternates a widening dusk ring with a marked strike, both faster below half health.
 */
public class UmbraDescendant extends DescendantBoss {

    private static final int RING_WINDUP_TICKS = 2 * 20;
    private static final int MARK_WINDUP_TICKS = 2 * 20 + 10;
    private static final double RING_RADIUS = 5;
    private static final double ENRAGED_RING_RADIUS = 7;
    private static final double MARK_RADIUS = 2.5;

    private int cooldownTicks = 4 * 20;
    private boolean castRing = true;
    private boolean enraged;
    private Location telegraphCenter;
    private int telegraphTicks;
    private boolean ringActive;

    public UmbraDescendant(Location spawnLocation) {
        this(spawnLocation, "Umbra Descendant", 20000, 0.3f, 12, 500, 700);
    }

    public UmbraDescendant(
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
        return Mob.UMBRA_DESCENDANT;
    }

    @Override
    public Component getDescription() {
        return Component.text("Heir of the dusk", NamedTextColor.LIGHT_PURPLE);
    }

    @Override
    public double getMobScale() {
        return 1.2;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 2, 0.6f);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (warlordsNPC == null || warlordsNPC.isDead()) {
            return;
        }
        if (!enraged && warlordsNPC.getCurrentHealth() <= warlordsNPC.getMaxHealth() * 0.5f) {
            enraged = true;
            announce("The Umbra Descendant sinks deeper. Its rings grow wider.");
            Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 2, 0.5f);
        }
        if (ticksElapsed % 10 == 0) {
            tickAura();
        }
        if (telegraphTicks > 0) {
            tickTelegraph();
            return;
        }
        if (cooldownTicks > 0) {
            cooldownTicks--;
            return;
        }
        if (castRing) {
            startRing();
        } else {
            startMark();
        }
        castRing = !castRing;
    }

    private void startRing() {
        telegraphCenter = warlordsNPC.getLocation().clone();
        telegraphTicks = RING_WINDUP_TICKS;
        ringActive = true;
        toggleStun(true);
        announce("A dusk ring forms. Step out of it.");
        Utils.playGlobalSound(telegraphCenter, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 2, 0.6f);
    }

    private void startMark() {
        List<WarlordsEntity> targets = enemies(30);
        if (targets.isEmpty()) {
            cooldownTicks = 20;
            return;
        }
        telegraphCenter = targets.getFirst().getLocation().clone();
        telegraphTicks = MARK_WINDUP_TICKS;
        ringActive = false;
        announce("The dark marks where you stand. Move.");
        Utils.playGlobalSound(telegraphCenter, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.5f, 0.7f);
    }

    private void tickTelegraph() {
        telegraphTicks--;
        double radius = ringActive ? (enraged ? ENRAGED_RING_RADIUS : RING_RADIUS) : MARK_RADIUS;
        if (telegraphTicks % 5 == 0) {
            drawRing(telegraphCenter, radius, Particle.WITCH);
            if (!ringActive) {
                EffectUtils.displayParticle(Particle.PORTAL, telegraphCenter.clone().add(0, 1, 0), 12, 0.2, 0.8, 0.2, 0.02);
            }
        }
        if (telegraphTicks > 0) {
            return;
        }
        if (ringActive) {
            damageAround(telegraphCenter, radius, enraged ? 1200f : 900f, "Dusk Ring");
            toggleStun(false);
        } else {
            damageAround(telegraphCenter, radius, 1100f, "Descent Mark");
        }
        Utils.playGlobalSound(telegraphCenter, Sound.ENTITY_GENERIC_EXPLODE, 1.2f, 0.7f);
        EffectUtils.playSphereAnimation(telegraphCenter, radius, Particle.WITCH, 2);
        cooldownTicks = enraged ? 4 * 20 : 6 * 20;
    }
}
