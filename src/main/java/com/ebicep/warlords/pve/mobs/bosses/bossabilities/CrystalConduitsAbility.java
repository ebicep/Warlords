package com.ebicep.warlords.pve.mobs.bosses.bossabilities;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.SkyCrystal;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CrystalConduitsAbility {

    private final WarlordsEntity source;
    private final Supplier<Location> centerSupplier; // boss/arena center (provides Y)
    private final int crystalCount;
    private final double radius;
    private final double ringStep;
    private final double beaconYOffset;
    private final double failDamage;
    private final PveOption option;

    private final Particle.DustOptions ringDust =
            new Particle.DustOptions(Color.fromRGB(120, 200, 255), 1.2f);
    private final Particle.DustOptions linkDust =
            new Particle.DustOptions(Color.fromRGB(200, 230, 255), 1.2f);

    // Runtime
    private final List<Node> nodes = new ArrayList<>();
    private final List<Integer> order = new ArrayList<>();
    private int expectIndex = 0;
    private GameRunnable loop;
    private boolean running = false;
    private Listener listener;
    private boolean completed;
    private boolean failed;

    private boolean followBoss = false;      // off by default
    private double followLerp = 1.0;         // 1.0 = hard teleport; 0.15 = smooth follow

    public void setFollowBoss(boolean follow) { this.followBoss = follow; }
    public void setFollowLerp(double lerp) { this.followLerp = Math.max(0.0, Math.min(1.0, lerp)); }

    public CrystalConduitsAbility(
            WarlordsEntity source,
            Supplier<Location> centerSupplier,
            int crystalCount,
            double radius,
            double ringStep,
            double beaconYOffset,
            double failDamage, PveOption option
    ) {
        this.source = source;
        this.centerSupplier = centerSupplier;
        this.crystalCount = Math.max(1, crystalCount);
        this.radius = Math.max(1.0, radius);
        this.ringStep = Math.max(0.25, ringStep);
        this.beaconYOffset = beaconYOffset;
        this.failDamage = Math.max(0.0, failDamage);
        this.option = option;
    }

    /* ================= Public API ================= */

    public void start(Game game) {
        if (running) return;
        running = true;
        nodes.clear();
        order.clear();
        expectIndex = 0;

        Location center = safeCenter();
        World w = center.getWorld();
        if (w == null) { stop(); return; }

        double y = center.getY();
        for (int i = 0; i < crystalCount; i++) {
            double ang = 2 * Math.PI * i / crystalCount;
            double x = center.getX() + Math.cos(ang) * radius;
            double z = center.getZ() + Math.sin(ang) * radius;
            Location at = new Location(w, x, y, z);

            order.add(i);

            SkyCrystal skyCrystal = new SkyCrystal(at, labelFor(order.get(i)));
            option.spawnNewMob(skyCrystal);

            nodes.add(new Node(i, ang, radius, at, skyCrystal));
        }

        Collections.shuffle(order, new Random());

        announceSequence();

        // 4) Listen for deaths – validate sequence
        listener = new Listener() {
            @EventHandler(ignoreCancelled = true)
            private void onDeath(WarlordsDeathEvent event) {
                if (!running) return;
                WarlordsEntity dead = event.getWarlordsEntity();
                if (dead.getUuid() == null) return;

                Node hit = null;
                for (Node n : nodes) {
                    if (n.alive() && n.idMatches(dead.getUuid())) {
                        hit = n; break;
                    }
                }

                if (hit == null) return;

                int expectedNodeIndex = order.get(expectIndex);
                Node expected = nodes.get(expectedNodeIndex);

                if (!hit.equals(expected)) {
                    // fail
                    failAll(center);
                    failed = true;
                    nodes.forEach(node -> {
                        if (node.crystal.getWarlordsNPC().isDead()) {
                            return;
                        }
                        node.crystal.getWarlordsNPC().addInstance(InstanceBuilder
                                .damage()
                                .cause("Conduit Massacre")
                                .value(500000)
                                .source(source)
                                .flags(InstanceFlags.TRUE_DAMAGE)
                        );
                    });
                    stop();
                } else {
                    // correct
                    hit.markDead();
                    expectIndex++;

                    // Small cue at that crystal
                    World ww = hit.loc.getWorld();
                    if (ww != null) {
                        ww.spawnParticle(Particle.TOTEM_OF_UNDYING, hit.loc, 10, 0.4, 0.4, 0.4, 0.02);
                        Utils.playGlobalSound(hit.loc, Sound.ENTITY_PLAYER_LEVELUP, 500, 0.5f);
                    }

                    if (expectIndex >= crystalCount) {
                        // Sequence complete → success
                        completed = true;
                        successBurst(center);
                        stop();
                    }
                }
            }
        };
        // Register your event listener via your Game wrapper
        game.registerEvents(listener);

        // 5) Visual loop: draw rings + boss links while running
        loop = new GameRunnable(game) {
            int t = 0;
            @Override
            public void run() {
                t++;
                if (followBoss) {
                    Location cNow = safeCenter();
                    double y = cNow.getY();

                    for (Node n : nodes) {
                        if (!n.alive()) continue;

                        double targetX = cNow.getX() + Math.cos(n.angle) * n.radius;
                        double targetZ = cNow.getZ() + Math.sin(n.angle) * n.radius;
                        Location target = new Location(cNow.getWorld(), targetX, y, targetZ);

                        Location cur = n.crystal.getWarlordsNPC().getLocation();
                        double t = followLerp;
                        double newX = cur.getX() + (targetX - cur.getX()) * t;
                        double newY = cur.getY() + (y       - cur.getY()) * t;
                        double newZ = cur.getZ() + (targetZ - cur.getZ()) * t;
                        Location lerped = new Location(target.getWorld(), newX, newY, newZ);

                        var bukkitEnt = n.crystal.getWarlordsNPC().getEntity();
                        bukkitEnt.teleport(lerped);

                        n.loc = lerped;
                    }
                }

                Location c = safeCenter();
                World world = c.getWorld();
                if (world == null) { stop(); cancel(); return; }

                // Draw number ring + link for each still-alive crystal
                for (Node n : nodes) {
                    if (!n.alive()) continue;

                    drawRing(n.loc.clone().add(0, beaconYOffset, 0), 1.1, ringDust);
                    drawLink(world, c, n.loc, linkDust);
                    world.spawnParticle(Particle.END_ROD, n.loc.clone().add(0, 0.35, 0), 1, 0, 0, 0, 0);

                    // Optional: highlight the currently expected target a bit more
                    int expectedIdx = order.get(expectIndex);
                    if (n.index == expectedIdx) {
                        drawRing(n.loc.clone().add(0, beaconYOffset + 0.1, 0), 1.35, ringDust);
                    }
                }

                if (t % 100 == 0) {
                    Utils.playGlobalSound(center, Sound.ENTITY_WARDEN_EMERGE, 500, 0.5f);
                }
            }
        };
        loop.runTaskTimer(0, 1);

        // Spawn sound
        Utils.playGlobalSound(center, Sound.ENTITY_WARDEN_EMERGE, 500, 0.5f);
    }

    public void stop() {
        if (!running) return;
        running = false;

        if (loop != null) {
            GameRunnable r = loop;
            loop = null;
            r.cancel();
        }

        if (listener != null) {
            HandlerList.unregisterAll(listener);
            listener = null;
        }

        // No forced despawn: your crystal mobs should die via players.
        nodes.clear();
        order.clear();
        expectIndex = 0;
    }

    public boolean isRunning() { return running; }

    /* ================= Internals ================= */

    private void announceSequence() {
        // e.g. "Order: B → A → C"
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < order.size(); i++) {
            if (i > 0) sb.append(" \u2192 "); // arrow
            sb.append(labelFor(order.get(i)));
        }
        ChatUtils.sendTitleToGamePlayers(
                source.getGame(),
                Component.text("CRYSTAL ORDER", NamedTextColor.RED),
                Component.text(sb.toString()),
                20, 120, 20
        );
        Utils.playGlobalSound(centerSupplier.get(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 500, 0.5f);
    }

    private String labelFor(int idx) {
        // A/B/C labels (or 1/2/3 if you prefer)
        return switch (idx) {
            case 0 -> "A";
            case 1 -> "B";
            case 2 -> "C";
            case 3 -> "D";
            case 4 -> "E";
            default -> "#" + (idx + 1);
        };
    }

    private void successBurst(Location center) {
        World w = center.getWorld();
        if (w != null) {
            ChatUtils.sendTitleToGamePlayers(
                    source.getGame(),
                    Component.empty(),
                    Component.text("SEQUENCE COMPLETE", NamedTextColor.GREEN),
                    20, 30, 20
            );
            Utils.playGlobalSound(center, Sound.BLOCK_BEACON_DEACTIVATE, 500, 0.5f);
            EffectUtils.playFirework(center, FireworkEffect.builder()
                    .withColor(Color.GRAY)
                    .with(FireworkEffect.Type.BALL_LARGE)
                    .withTrail()
                    .build());
        }
    }

    private void failAll(Location center) {
        World w = center.getWorld();
        Utils.playGlobalSound(center, Sound.ENTITY_WARDEN_AGITATED, 500, 0.5f);
        ChatUtils.sendTitleToGamePlayers(
                source.getGame(),
                Component.empty(),
                Component.text("SEQUENCE FAILED", NamedTextColor.RED),
                20, 30, 20
        );
        for (WarlordsEntity enemy : PlayerFilter
                .entitiesAround(center, radius + 32, 16, radius + 32)
                .aliveEnemiesOf(source)
        ) {
            enemy.addInstance(InstanceBuilder
                    .damage()
                    .cause("Conduit Massacre")
                    .value((float) failDamage)
                    .source(source)
            );
        }
        if (w != null) {
            w.spawnParticle(Particle.EXPLOSION, center, 1, 0, 0, 0, 0);
        }
    }

    private void drawRing(Location center, double rad, Particle.DustOptions dust) {
        World w = center.getWorld();
        if (w == null || rad <= 0) return;
        double angStep = Math.max(0.02, ringStep / Math.max(0.1, rad));
        double twoPi = Math.PI * 2.0;
        double y = center.getY();
        for (double a = 0; a < twoPi; a += angStep) {
            double x = center.getX() + Math.cos(a) * rad;
            double z = center.getZ() + Math.sin(a) * rad;
            w.spawnParticle(Particle.DUST, x, y, z, 1, 0, 0, 0, 0.0, dust);
        }
    }

    private void drawLink(World w, Location from, Location to, Particle.DustOptions dust) {
        var a = from.clone(); a.setY(to.getY()); // draw at crystal Y
        var b = to.clone();
        var dir = b.toVector().subtract(a.toVector());
        double len = dir.length();
        if (len < 1e-6) return;
        dir.normalize().multiply(0.35); // spacing
        int samples = Math.max(1, (int) Math.ceil(len / 0.35));
        Location p = a.clone();
        for (int i = 0; i <= samples; i++) {
            w.spawnParticle(Particle.DUST, p, 1, 0, 0, 0, 0.0, dust);
            if ((i % 6) == 0) w.spawnParticle(Particle.ELECTRIC_SPARK, p, 1, 0, 0, 0, 0.0);
            p.add(dir);
        }
    }

    private Location safeCenter() {
        Location c = centerSupplier.get();
        return (c == null) ? new Location(org.bukkit.Bukkit.getWorlds().getFirst(), 0, 64, 0) : c.clone();
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean failed() {
        return failed;
    }

    /* ---------------- Data ---------------- */

    private static final class Node {
        final int index;               // 0..N-1
        final double angle;            // radians from center at spawn
        final double radius;           // distance from center at spawn
        final AbstractMob crystal;     // keep reference to move it
        final UUID uuid;               // convenience
        Location loc;                  // last known (for particles)
        private boolean alive = true;

        Node(int index, double angle, double radius, Location loc, AbstractMob crystal) {
            this.index = index;
            this.angle = angle;
            this.radius = radius;
            this.loc = loc.clone();
            this.crystal = crystal;
            this.uuid = crystal.getWarlordsNPC().getUuid();
        }

        boolean alive() { return alive && !crystal.getWarlordsNPC().isDead(); }
        void markDead() { alive = false; }
        boolean idMatches(UUID u) { return uuid != null && uuid.equals(u); }
    }
}
