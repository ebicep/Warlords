package com.ebicep.warlords.pve.mobs.bosses.bossabilities;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.minecraft.world.entity.player.Player;
import org.bukkit.*;
import org.bukkit.util.Vector;

import java.util.*;

public class MarkedForDeathAbility {

    private final WarlordsEntity source;

    // Config
    private final int marksToPick;          // how many players to mark (unique)
    private final int telegraphTicks;       // time the marker follows the player
    private final int lockToImpactTicks;    // delay after lock before the strike
    private final double strikeRadius;      // AoE radius on impact
    private final double strikeDamage;      // damage on impact
    private final double markerYOffset;     // Y offset for overhead marker (relative to player)
    private final double ringStep;          // ground ring particle density

    // Visuals
    private final Particle.DustOptions markerDust =
            new Particle.DustOptions(Color.fromRGB(255, 80, 80), 4f);
    private final Particle.DustOptions lockDust =
            new Particle.DustOptions(Color.fromRGB(255, 170, 80), 4f);
    private final Particle.DustOptions strikeDust =
            new Particle.DustOptions(Color.fromRGB(155, 130, 200), 4f);

    // Runtime
    private final List<Mark> marks = new ArrayList<>();
    private GameRunnable task;
    private boolean running = false;

    public MarkedForDeathAbility(
            WarlordsEntity source,
            int marksToPick,
            int telegraphTicks,
            int lockToImpactTicks,
            double strikeRadius,
            double strikeDamage,
            double markerYOffset,
            double ringStep
    ) {
        this.source = source;
        this.marksToPick = Math.max(1, marksToPick);
        this.telegraphTicks = Math.max(1, telegraphTicks);
        this.lockToImpactTicks = Math.max(1, lockToImpactTicks);
        this.strikeRadius = Math.max(0.5, strikeRadius);
        this.strikeDamage = Math.max(0.0, strikeDamage);
        this.markerYOffset = markerYOffset;
        this.ringStep = Math.max(0.25, ringStep);
    }

    /* ================= Public API ================= */

    public void start(Game game) {
        if (running) return;
        running = true;
        marks.clear();

        // Pick up to N unique alive enemy players
        List<WarlordsEntity> candidates = PlayerFilter
                .playingGame(game)
                .aliveEnemiesOf(source)
                .toList();

        if (candidates.isEmpty()) { running = false; return; }

        Collections.shuffle(candidates, new Random());
        int pick = Math.min(marksToPick, candidates.size());
        for (int i = 0; i < pick; i++) {
            WarlordsEntity t = candidates.get(i);
            Location lk = lastKnownGround(t);
            marks.add(new Mark(t, lk, /*lockedIdx*/ -1));
        }

        // Start loop
        task = new GameRunnable(game) {
            int t = 0;
            @Override
            public void run() {
                t++;

                // follow targets, draw overhead marker, update last-known ground
                if (t <= telegraphTicks) {
                    for (Mark m : marks) {
                        if (m.target == null) continue;
                        Location head = headPos(m.target, markerYOffset);
                        head.getWorld().spawnParticle(Particle.DUST, head, 2, 0.02, 0.02, 0.02, 0.0, markerDust);
                        // trailing spark
                        head.getWorld().spawnParticle(Particle.END_ROD, head, 1, 0, 0, 0, 0.0);
                        // keep last-known ground pos fresh
                        if (t == 1 && m.target.getEntity() instanceof Player) {
                            m.target.getEntity().showTitle(Title.title(Component.empty(), Component.text("You have been marked for death!", NamedTextColor.RED)));
                        }
                        m.lastKnown = lastKnownGround(m.target);
                    }

                    if (t == 1) {
                        // global cue
                        Utils.playGlobalSound(source.getLocation(), Sound.ENTITY_WITHER_DEATH, 3, 1.2f);
                    }

                    if (t == telegraphTicks) {
                        // lock moment
                        for (Mark m : marks) {
                            m.lockedAt = m.lastKnown.clone();
                            Utils.playGlobalSound(m.target.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 2, 0.5f);
                        };
                    }
                    return;
                }

                // Post-lock pre-impact: draw ground ring at locked point
                if (t < telegraphTicks + lockToImpactTicks) {
                    for (Mark m : marks) {
                        drawRing(m.lockedAt, strikeRadius, lockDust);
                    }
                    return;
                }

                // Impact (single tick)
                if (t == telegraphTicks + lockToImpactTicks) {
                    for (Mark m : marks) {
                        impactStrike(m.lockedAt);
                        Utils.playGlobalSound(m.lockedAt, Sound.ENTITY_ALLAY_DEATH, 2, 0.5f);
                        Utils.playGlobalSound(m.lockedAt, "warrior.laststand.activation", 500, 0.5f);
                    }
                }

                stop();
                cancel();
            }

            @Override
            public void cancel() {
                super.cancel();
            }
        };
        task.runTaskTimer(0, 1);
    }

    public void stop() {
        running = false;
        if (task != null) {
            GameRunnable r = task;
            task = null;
            r.cancel();
        }
        marks.clear();
    }

    public boolean isRunning() { return running; }

    /* ================= Internals ================= */

    private void impactStrike(Location at) {
        World w = at.getWorld();
        if (w == null) return;

        // VFX
        w.spawnParticle(Particle.EXPLOSION, at, 1, 0, 0, 0, 0.0);
        // “column” sparkle
        for (double y = 0; y <= 2.5; y += 0.35) {
            w.spawnParticle(Particle.DUST, at.getX(), at.getY() + y, at.getZ(), 1, 0, 0, 0, 0.0, strikeDust);
            w.spawnParticle(Particle.SNOWFLAKE, at.getX(), at.getY() + y, at.getZ(), 1, 0, 0, 0, 0.0);
        }

        // Damage enemies in radius
        for (WarlordsEntity enemy : PlayerFilter
                .entitiesAround(at, strikeRadius, 3, strikeRadius)
                .aliveEnemiesOf(source)
        ) {
            enemy.addInstance(InstanceBuilder
                    .damage()
                    .cause("Marked for Death")
                    .value((float) strikeDamage)
                    .source(source)
                    .flag(InstanceFlags.TRUE_DAMAGE, true)
            );
        }
    }

    private void drawRing(Location center, double radius, Particle.DustOptions dust) {
        World w = center.getWorld();
        if (w == null || radius <= 0) return;

        double angStep = Math.max(0.02, ringStep / Math.max(0.1, radius));
        double twoPi = Math.PI * 2.0;
        double y = center.getY() + 0.05; // avoid z-fighting on flat floors
        for (double a = 0; a < twoPi; a += angStep) {
            double x = center.getX() + Math.cos(a) * radius;
            double z = center.getZ() + Math.sin(a) * radius;
            w.spawnParticle(Particle.DUST, x, y, z, 1, 0, 0, 0, 0.0, dust);
        }
    }

    private static Location headPos(WarlordsEntity e, double yOffset) {
        Location l = e.getLocation().clone();
        // If you have an API for height, prefer it; fallback uses +yOffset
        l.add(0, yOffset, 0);
        return l;
    }

    private static Location lastKnownGround(WarlordsEntity e) {
        // For flat arenas this is fine; if you need exact slab hit, raytrace instead.
        Location l = e.getLocation().clone();
        return new Location(l.getWorld(), l.getX(), l.getY(), l.getZ());
    }

    /* ---------------- Data ---------------- */
    private static final class Mark {
        final WarlordsEntity target;
        Location lastKnown;
        Location lockedAt;
        int lockedIdx; // reserved if you later want staggered impacts

        Mark(WarlordsEntity target, Location lastKnown, int lockedIdx) {
            this.target = target;
            this.lastKnown = lastKnown;
            this.lockedIdx = lockedIdx;
        }
    }
}
