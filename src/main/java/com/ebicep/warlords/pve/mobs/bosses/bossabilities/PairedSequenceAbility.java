package com.ebicep.warlords.pve.mobs.bosses.bossabilities;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Veilkeeper - Paired Color Sequence (Simon-style)
 * - Each round: pick 2 new players.
 * - Announce a growing sequence of colors (zones around arena).
 * - The pair must, step-by-step, BOTH stand inside the announced color zone
 *   within a time limit, and hold for a few ticks.
 * - Fail any step -> both take heavy damage + boss is healed. Continue to next round.
 * - Complete all steps -> round success (no damage), then next round (sequence grows by 1).
 */
public class PairedSequenceAbility {

    /* ---------------- Types ---------------- */

    public enum SanctumColor {
        RED(Color.fromRGB(255, 90, 90)),
        BLUE(Color.fromRGB(90, 160, 255)),
        GREEN(Color.fromRGB(110, 230, 110)),
        YELLOW(Color.fromRGB(255, 220, 90)),
        PURPLE(Color.fromRGB(200, 110, 255)),
        CYAN(Color.fromRGB(90, 240, 240));

        public final Particle.DustOptions dust;
        SanctumColor(Color c) { this.dust = new Particle.DustOptions(c, 1.6f); }
    }

    private enum Phase { ANNOUNCE, STEP_WAIT, BETWEEN_ROUNDS, DONE }

    /* ---------------- Config ---------------- */

    private final WarlordsEntity source;                 // Veilkeeper
    private final Supplier<Location> centerSupplier;  // arena center

    private final List<SanctumColor> palette; // colors in play (e.g., 4–6)
    private final int totalRounds;            // number of rounds (sequence length will end at totalRounds)
    private final double arenaRadius;         // where to place sanctum zones
    private final double zoneRadius;          // sanctum circle radius

    private final int announceTickPerColor;   // how long each color flashes during announce
    private final int stepTimeLimit;          // max ticks to complete a single step
    private final int stepHoldTicks;          // continuous ticks both must stand in the zone to count the step
    private final int betweenRoundsTicks;     // small pause between rounds

    private final double ringStep;            // particle density
    private final double failDamage;          // damage applied to both on any failure
    private final double bossHealOnFail;      // amount to heal boss (via callback)
    private final Consumer<Double> onBossHeal; // your hook to actually heal the boss

    /* ---------------- Runtime ---------------- */

    private final Random rng = new Random();
    private final Map<SanctumColor, Location> zoneByColor = new LinkedHashMap<>();
    private final List<SanctumColor> sequence = new ArrayList<>();

    private WarlordsEntity pA, pB;           // current pair
    private int roundIndex = 0;               // 0-based; also equals current sequence length after appending
    private int announceCursor = 0;           // which index of sequence we are announcing
    private int stepCursor = 0;               // which sequence step the players are executing
    private int stepAge = 0;                  // ticks elapsed in current step
    private int stepHold = 0;                 // ticks both have been inside current color
    private int betweenAge = 0;               // ticks in BETWEEN_ROUNDS

    private Particle.DustOptions pairMarker = new Particle.DustOptions(Color.fromRGB(255, 255, 255), 1.6f);

    private Phase phase = Phase.ANNOUNCE;
    private GameRunnable loop;
    private boolean running = false;

    /* ---------------- Ctor ---------------- */

    public PairedSequenceAbility(
            WarlordsEntity source,
            Supplier<Location> centerSupplier,
            List<SanctumColor> palette,
            int totalRounds,
            double arenaRadius,
            double zoneRadius,
            int announceTickPerColor,
            int stepTimeLimit,
            int stepHoldTicks,
            int betweenRoundsTicks,
            double ringStep,
            double failDamage,
            double bossHealOnFail,
            Consumer<Double> onBossHeal
    ) {
        this.source = source;
        this.centerSupplier = centerSupplier;

        this.palette = new ArrayList<>(palette);
        this.totalRounds = Math.max(1, totalRounds);
        this.arenaRadius = Math.max(2.0, arenaRadius);
        this.zoneRadius = Math.max(0.5, zoneRadius);

        this.announceTickPerColor = Math.max(5, announceTickPerColor);
        this.stepTimeLimit = Math.max(10, stepTimeLimit);
        this.stepHoldTicks = Math.max(1, stepHoldTicks);
        this.betweenRoundsTicks = Math.max(5, betweenRoundsTicks);

        this.ringStep = Math.max(0.25, ringStep);
        this.failDamage = Math.max(0.0, failDamage);
        this.bossHealOnFail = Math.max(0.0, bossHealOnFail);
        this.onBossHeal = (onBossHeal != null) ? onBossHeal : (amt -> {});
    }

    /* ---------------- Public API ---------------- */

    public void start(Game game) {
        if (running) return;
        running = true;

        // Place color zones evenly around the arena
        placeZones();

        // Seed sequence with 1 random color and pick first pair
        sequence.clear();
        sequence.add(randomColor());
        roundIndex = 1;
        pickPair(game);

        phase = Phase.ANNOUNCE;
        announceCursor = 0;
        stepCursor = 0;
        stepAge = 0;
        stepHold = 0;

        Utils.playGlobalSound(safeCenter(), Sound.ENTITY_ALLAY_AMBIENT_WITH_ITEM, 500, 1.5f);

        loop = new GameRunnable(game) {
            int tick = 0;
            @Override public void run() {
                tick++;

                switch (phase) {
                    case ANNOUNCE -> doAnnounce();
                    case STEP_WAIT -> doStep();
                    case BETWEEN_ROUNDS -> doBetweenRounds(game);
                    case DONE -> { stop(); cancel(); }
                }

                // draw static zone rings every tick
                drawZones();
                // overhead white marker dots for the current pair
                drawPairMarkers();
            }
        };
        loop.runTaskTimer(0, 1);
    }

    public void stop() {
        running = false;
        if (loop != null) {
            GameRunnable r = loop;
            loop = null;
            r.cancel();
        }
        pA = pB = null;
        sequence.clear();
        zoneByColor.clear();
    }

    public boolean isRunning() { return running; }

    /* ---------------- Core Phases ---------------- */

    private void doAnnounce() {
        // Flash sequence colors one-by-one
        if (announceCursor < sequence.size()) {
            SanctumColor col = sequence.get(announceCursor);
            flashZone(col);
            if ((announceCursor == 0)) {
                Utils.playGlobalSound(safeCenter(), Sound.BLOCK_BEACON_ACTIVATE, 500, 0.5f);
            }
            announceCursor++;
        } else {
            // move to first step
            phase = Phase.STEP_WAIT;
            stepCursor = 0;
            stepAge = 0;
            stepHold = 0;
            Utils.playGlobalSound(safeCenter(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 500, 0.5f);
        }
    }

    private void doStep() {
        if (stepCursor >= sequence.size()) {
            // ROUND SUCCESS
            Utils.playGlobalSound(safeCenter(), Sound.ENTITY_PLAYER_LEVELUP, 500, 1.2f);
            // Prepare next round or finish
            if (roundIndex >= totalRounds) {
                Utils.playGlobalSound(safeCenter(), Sound.BLOCK_BEACON_ACTIVATE, 500, 0.5f);
                phase = Phase.DONE;
                return;
            }
            phase = Phase.BETWEEN_ROUNDS;
            betweenAge = 0;
            return;
        }

        // Current target color
        SanctumColor target = sequence.get(stepCursor);
        Location zone = zoneByColor.get(target);

        // Highlight current step zone each tick
        drawRing(zone, zoneRadius + 0.15, target.dust);
        zone.getWorld().spawnParticle(Particle.END_ROD, zone.clone().add(0, 0.5, 0), 2, 0, 0, 0, 0);

        // Check both players standing inside the zone
        boolean aIn = inZone(pA, zone, zoneRadius);
        boolean bIn = inZone(pB, zone, zoneRadius);

        if (aIn && bIn) {
            stepHold++;
            if (stepHold >= stepHoldTicks) {
                // Step completed
                Utils.playGlobalSound(zone, Sound.BLOCK_SCULK_CHARGE, 2, 0.5f);
                stepCursor++;
                stepAge = 0;
                stepHold = 0;
            }
        } else {
            // reset hold if anyone steps out
            if (stepHold > 0) stepHold = 0;
        }

        // Time limit per step
        stepAge++;
        if (stepAge > stepTimeLimit) {
            // FAIL: damage both + heal boss, then go to next round
            applyFail(pA, pB, zone);
            if (roundIndex >= totalRounds) {
                phase = Phase.DONE;
            } else {
                phase = Phase.BETWEEN_ROUNDS;
                betweenAge = 0;
            }
        }
    }

    private void doBetweenRounds(Game game) {
        betweenAge++;
        if (betweenAge == 1) {
            // Extend sequence by one new random color (can repeat)
            sequence.add(randomColor());
            roundIndex++;
            // Pick a fresh pair
            pickPair(game);
            Utils.playGlobalSound(safeCenter(), "warrior.laststand.activation", 500, 0.5f);
        }
        if (betweenAge >= betweenRoundsTicks) {
            // Announce again for new pair
            phase = Phase.ANNOUNCE;
            announceCursor = 0;
        }
    }

    /* ---------------- Helpers ---------------- */

    private void placeZones() {
        zoneByColor.clear();
        Location center = safeCenter();
        World w = center.getWorld();
        if (w == null) return;

        int n = palette.size();
        for (int i = 0; i < n; i++) {
            SanctumColor c = palette.get(i);
            double ang = 2 * Math.PI * i / n;
            double r = arenaRadius * 0.78;
            Location at = new Location(w,
                    center.getX() + Math.cos(ang) * r,
                    center.getY() + 0.05,
                    center.getZ() + Math.sin(ang) * r
            );
            zoneByColor.put(c, at);
        }
    }

    private void drawZones() {
        for (Map.Entry<SanctumColor, Location> e : zoneByColor.entrySet()) {
            drawRing(e.getValue(), zoneRadius, e.getKey().dust);
        }
    }

    private void flashZone(SanctumColor col) {
        Location at = zoneByColor.get(col);
        if (at == null) return;
        World w = at.getWorld();
        if (w == null) return;

        // flash for announceTickPerColor ticks by drawing thicker ring + extra particles
        for (int i = 0; i < announceTickPerColor; i++) {
            drawRing(at, zoneRadius + 0.2, col.dust);
        }
        Utils.playGlobalSound(at, Sound.ENTITY_ENDER_EYE_DEATH, 2, 0.5f);
        w.spawnParticle(Particle.TOTEM_OF_UNDYING, at, 8, zoneRadius * 0.4, 0.3, zoneRadius * 0.4, 0.02);
    }

    private void applyFail(WarlordsEntity a, WarlordsEntity b, Location where) {
        Utils.playGlobalSound(where, Sound.ENTITY_WITHER_DEATH, 500, 0.2f);
        World w = where.getWorld();
        if (w != null) {
            w.spawnParticle(Particle.EXPLOSION, where, 1, 0, 0, 0, 0);
        }
        if (a != null) {
            a.addInstance(InstanceBuilder.damage().cause("Veilkeeper Trials").value((float) failDamage).source(source));
        }
        if (b != null) {
            b.addInstance(InstanceBuilder.damage().cause("Veilkeeper Trials").value((float) failDamage).source(source));
        }
        if (bossHealOnFail > 0) {
            onBossHeal.accept(bossHealOnFail); // you heal the boss here
        }
        // prepare next round
        stepCursor = sequence.size(); // treat as round ended
    }

    private void drawPairMarkers() {
        drawMarker(pA);
        drawMarker(pB);
    }

    private void drawMarker(WarlordsEntity e) {
        if (e == null) {
            return;
        } else {
            e.getLocation();
        }
        Location head = e.getLocation().clone().add(0, 1.8, 0);
        World w = head.getWorld();
        if (w != null) {
            w.spawnParticle(Particle.DUST, head, 2, 0.02, 0.02, 0.02, 0.0, pairMarker);
        }
    }

    private boolean inZone(WarlordsEntity e, Location zone, double radius) {
        if (e == null || e.isDead()) return false;
        Location l = e.getLocation();
        if (l.getWorld() != zone.getWorld()) return false;
        double dx = l.getX() - zone.getX();
        double dz = l.getZ() - zone.getZ();
        return (dx*dx + dz*dz) <= radius*radius;
    }

    private SanctumColor randomColor() {
        return palette.get(rng.nextInt(palette.size()));
    }

    private void pickPair(Game game) {
        List<WarlordsEntity> candidates = PlayerFilter
                .playingGame(game)
                .aliveEnemiesOf(source)
                .toList();
        Collections.shuffle(candidates, rng);
        pA = (!candidates.isEmpty()) ? candidates.get(0) : null;
        pB = (candidates.size() > 1) ? candidates.get(1) : null;
        Utils.playGlobalSound(safeCenter(), Sound.BLOCK_ANVIL_USE, 500, 0.5f);
    }

    private void drawRing(Location center, double radius, Particle.DustOptions dust) {
        World w = center.getWorld();
        if (w == null || radius <= 0) return;
        double angStep = Math.max(0.02, ringStep / Math.max(0.1, radius));
        double twoPi = Math.PI * 2.0;
        double y = center.getY();
        for (double a = 0; a < twoPi; a += angStep) {
            double x = center.getX() + Math.cos(a) * radius;
            double z = center.getZ() + Math.sin(a) * radius;
            w.spawnParticle(Particle.DUST, x, y, z, 1, 0, 0, 0, 0.0, dust);
        }
    }

    private Location safeCenter() {
        Location c = centerSupplier.get();
        return (c == null) ? new Location(org.bukkit.Bukkit.getWorlds().getFirst(), 0, 64, 0) : c.clone();
    }
}
