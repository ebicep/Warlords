package com.ebicep.warlords.pve.mobs.bosses.bossabilities;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.core.particles.ParticleTypes;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EmpoweringRelicsAbility {

    private final WarlordsEntity source;
    private final Supplier<Location> centerSupplier; // usually () -> arenaCenter.clone()

    // Relic config
    private final int relicCount;            // how many relics to spawn
    private final double arenaRadius;        // random spawn radius from center
    private final double pickupRadius;       // distance to pick up a relic
    private final boolean oneUseRelics;      // if true, relic disappears after pickup
    private final double displayScale;       // relic display scale on the ground

    // Buff / aura config
    private final int buffDurationTicks;     // how long carriers emit the aura
    private final double allyBuffRadius;     // radius around the carrier for ally buff
    private final double damageMultiplier;   // outgoing damage multiplier vs. boss (use via hasBuff() in your pipeline)

    // Halo config
    private final ItemStack haloItem;        // item shown above carrier (e.g., NETHER_STAR)
    private final double haloHeight;         // vertical offset above carrier's feet (≈ head + a bit)
    private final float haloScale;           // scale for the halo item
    private final float haloSpinRadPerTick;  // spin speed (radians per tick)

    // Visuals
    private final double ringStep;           // circle particle density
    private final Particle.DustOptions relicDust =
            new Particle.DustOptions(Color.fromRGB(255, 210, 90), 1.6f);
    private final Particle.DustOptions auraDust =
            new Particle.DustOptions(Color.fromRGB(100, 220, 255), 1.6f);

    // Hooks (optional)
    private final Consumer<WarlordsEntity> onPickup;
    private final Consumer<WarlordsEntity> onExpire;

    // Runtime
    private final List<Relic> relics = new ArrayList<>();
    private final Map<UUID, Integer> carrierExpireTick = new HashMap<>(); // carrier UUID -> tick expiry
    private final Map<UUID, ItemDisplay> halos = new HashMap<>();
    private int internalTick = 0;
    private GameRunnable loop;
    private boolean running = false;

    public EmpoweringRelicsAbility(
            WarlordsEntity source,
            Supplier<Location> centerSupplier,
            // relics
            int relicCount,
            double arenaRadius,
            double pickupRadius,
            boolean oneUseRelics,
            double displayScale,
            // buff / aura
            int buffDurationTicks,
            double allyBuffRadius,
            double damageMultiplier,
            // visuals
            double ringStep,
            // halo
            ItemStack haloItem,
            double haloHeight,
            float haloScale,
            float haloSpinRadPerTick,
            // hooks
            Consumer<WarlordsEntity> onPickup,
            Consumer<WarlordsEntity> onExpire
    ) {
        this.source = source;
        this.centerSupplier = centerSupplier;

        this.relicCount = Math.max(1, relicCount);
        this.arenaRadius = Math.max(1.0, arenaRadius);
        this.pickupRadius = Math.max(0.5, pickupRadius);
        this.oneUseRelics = oneUseRelics;
        this.displayScale = Math.max(0.25, displayScale);

        this.buffDurationTicks = Math.max(10, buffDurationTicks);
        this.allyBuffRadius = Math.max(1.0, allyBuffRadius);
        this.damageMultiplier = Math.max(1.0, damageMultiplier);

        this.ringStep = Math.max(0.25, ringStep);

        this.haloItem = (haloItem != null) ? haloItem.clone() : new ItemStack(Material.NETHER_STAR);
        this.haloHeight = haloHeight;
        this.haloScale = Math.max(0.1f, haloScale);
        this.haloSpinRadPerTick = haloSpinRadPerTick;

        this.onPickup = (onPickup != null) ? onPickup : (p -> {});
        this.onExpire = (onExpire != null) ? onExpire : (p -> {});
    }

    /* ================= Public API ================= */

    public void start(Game game) {
        if (running) return;
        running = true;
        relics.clear();
        carrierExpireTick.clear();
        clearHalos();
        internalTick = 0;

        final Location center = safeCenter();
        final World w = center.getWorld();
        if (w == null) { stop(); return; }

        // Spawn relics at random positions inside the arena disc
        Random rng = new Random();
        for (int i = 0; i < relicCount; i++) {
            double a = rng.nextDouble() * Math.PI * 2;
            double r = Math.sqrt(rng.nextDouble()) * (arenaRadius - 0.75);
            double x = center.getX() + Math.cos(a) * r;
            double z = center.getZ() + Math.sin(a) * r;
            double y = center.getY() + 0.05;

            Location at = new Location(w, x, y, z);

            // Ground relic visual
            ItemDisplay disp = w.spawn(at.clone().add(0, 2, 0), ItemDisplay.class, d -> {
                d.setItemStack(new ItemStack(Material.GOLDEN_APPLE));
                d.setBillboard(Display.Billboard.FIXED);
                d.setViewRange(32f);
                d.setShadowRadius(0f);
                d.setInterpolationDuration(1);
                d.setTransformation(new Transformation(
                        new Vector3f(0, 0, 0),
                        new Quaternionf(),
                        new Vector3f((float) displayScale, (float) displayScale, (float) displayScale),
                        new Quaternionf()
                ));
            });

            relics.add(new Relic(at, disp, false));
            EffectUtils.playParticleLinkAnimation(at, source.getLocation(), Particle.SCULK_SOUL);
        }

        Utils.playGlobalSound(center, Sound.BLOCK_CONDUIT_ACTIVATE, 10, 1.1f);
        EffectUtils.playFirework(source.getLocation(), FireworkEffect.builder()
                .withColor(Color.AQUA)
                .with(FireworkEffect.Type.BALL_LARGE)
                .withTrail()
                .build());

        loop = new GameRunnable(game) {
            @Override
            public void run() {
                internalTick++;

                // 1) Draw relics and handle pickups
                for (Relic r : new ArrayList<>(relics)) {
                    if (r.claimed) continue;

                    drawRing(r.pos, pickupRadius, relicDust);
                    r.pos.getWorld().spawnParticle(Particle.END_ROD, r.pos.clone().add(0, 0.6, 0), 1, 0, 0, 0, 0.0);

                    Optional<WarlordsEntity> maybe = PlayerFilter
                            .entitiesAround(r.pos, pickupRadius, 2, pickupRadius)
                            .aliveEnemiesOf(source)
                            .stream()
                            .findFirst();

                    if (maybe.isPresent()) {
                        WarlordsEntity carrier = maybe.get();
                        r.claimed = true;
                        if (r.display != null && r.display.isValid()) r.display.remove();

                        int expireAt = internalTick + buffDurationTicks;
                        carrierExpireTick.put(carrier.getUuid(), expireAt);
                        onPickup.accept(carrier);

                        // Spawn halo
                        spawnOrUpdateHalo(carrier);

                        Utils.playGlobalSound(r.pos, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 2, 0.5f);
                        r.pos.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, r.pos, 10, 0.35, 0.35, 0.35, 0.05);

                        if (oneUseRelics) relics.remove(r);
                    }
                }

                // 2) Aura visuals + halo updates + expiration
                for (Iterator<Map.Entry<UUID, Integer>> it = carrierExpireTick.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<UUID, Integer> e = it.next();
                    UUID carrierId = e.getKey();
                    int expireAt = e.getValue();

                    WarlordsEntity carrier = findPlayerByUUID(game, carrierId);
                    if (carrier == null) {
                        // carrier left/died: cleanup
                        it.remove();
                        removeHalo(carrierId);
                        continue;
                    }

                    // Aura particles around carrier (for allies to see)
                    // NEW (clear, ground-hugging ring at the buff radius)
                    Location ringCenter = carrier.getLocation().clone();
                    drawRing(ringCenter, allyBuffRadius, auraDust);

                    ringCenter.getWorld().spawnParticle(Particle.DUST, ringCenter, 2, 0.15, 0.1, 0.15, 0.0, auraDust);

                    // Update halo position + rotation
                    updateHalo(carrier);

                    if (internalTick >= expireAt) {
                        it.remove();
                        onExpire.accept(carrier);
                        Utils.playGlobalSound(carrier.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 10, 1.2f);
                        ringCenter.getWorld().spawnParticle(Particle.END_ROD, ringCenter, 10, 0.25, 0.25, 0.25, 0.01);
                        removeHalo(carrierId);
                    }
                }

                // 3) End condition for one-use mode
                if (oneUseRelics && relics.stream().allMatch(r -> r.claimed)) {
                    stop();
                    cancel();
                }
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
        for (Relic relic : relics) {
            if (relic.display != null && relic.display.isValid()) {
                relic.display.remove();
            }
        }
        relics.clear();
        carrierExpireTick.clear();
        clearHalos();
    }

    public boolean isRunning() { return running; }

    /* ================= Damage/buff checks ================= */

    /**
     * Allies near a carrier get the buff (carrier themselves do NOT).
     * Use this in your damage pipeline to decide whether to amplify damage.
     */
    public boolean hasBuff(WarlordsEntity attacker) {
        if (attacker == null) return false;

        for (Map.Entry<UUID, Integer> e : carrierExpireTick.entrySet()) {
            UUID carrierId = e.getKey();
            int expireAt = e.getValue();
            if (internalTick > expireAt) continue;

            WarlordsEntity carrier = findPlayerByUUID(attacker.getGame(), carrierId);
            if (carrier == null) continue;

            if (carrier.getUuid().equals(attacker.getUuid())) {
                continue; // carrier does not get the buff themselves
            }

            if (carrier.getLocation().distanceSquared(attacker.getLocation()) <= allyBuffRadius * allyBuffRadius) {
                return true;
            }
        }
        return false;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    /* ================= Internals ================= */

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

    private void spawnOrUpdateHalo(WarlordsEntity carrier) {
        if (carrier == null) return;
        UUID id = carrier.getUuid();
        ItemDisplay halo = halos.get(id);
        Location head = carrier.getLocation().clone().add(0, haloHeight, 0);

        if (halo == null || !halo.isValid()) {
            // Create new halo
            halo = head.getWorld().spawn(head, ItemDisplay.class, d -> {
                d.setItemStack(haloItem);
                d.setBillboard(Display.Billboard.FIXED);
                d.setViewRange(48f);
                d.setShadowRadius(0f);
                d.setInterpolationDuration(1);
                d.setTransformation(new Transformation(
                        new Vector3f(0, 0, 0),
                        new Quaternionf(),
                        new Vector3f(haloScale, haloScale, haloScale),
                        new Quaternionf() // we'll animate rotation each tick
                ));
            });
            halos.put(id, halo);
        } else {
            halo.teleport(head);
        }
        // Initial rotation set this tick too
        applyHaloRotation(halo);
    }

    private void updateHalo(WarlordsEntity carrier) {
        UUID id = carrier.getUuid();
        ItemDisplay halo = halos.get(id);
        Location head = carrier.getLocation().clone().add(0, haloHeight, 0);

        if (halo == null || !halo.isValid()) {
            spawnOrUpdateHalo(carrier);
            return;
        }
        halo.teleport(head);
        applyHaloRotation(halo);
    }

    private void applyHaloRotation(ItemDisplay halo) {
        // Spin around Y; keep scale constant
        Transformation t = halo.getTransformation();
        Vector3f scale = t.getScale() != null ? new Vector3f(t.getScale()) : new Vector3f(haloScale, haloScale, haloScale);
        Quaternionf rot = new Quaternionf().rotateY(haloSpinRadPerTick * internalTick);
        halo.setTransformation(new Transformation(
                new Vector3f(0, 0, 0),
                rot,
                scale,
                new Quaternionf()
        ));
    }

    private void removeHalo(UUID carrierId) {
        ItemDisplay d = halos.remove(carrierId);
        if (d != null && d.isValid()) d.remove();
    }

    private void clearHalos() {
        for (ItemDisplay d : halos.values()) {
            if (d != null && d.isValid()) d.remove();
        }
        halos.clear();
    }

    private Location safeCenter() {
        Location c = centerSupplier.get();
        return (c == null) ? new Location(org.bukkit.Bukkit.getWorlds().get(0), 0, 64, 0) : c.clone();
    }

    private WarlordsEntity findPlayerByUUID(Game game, UUID id) {
        for (WarlordsEntity we : PlayerFilter.playingGame(game).toList()) {
            if (id.equals(we.getUuid())) return we;
        }
        return null;
    }

    /* ---------------- Data ---------------- */
    private static final class Relic {
        final Location pos;
        final ItemDisplay display;
        boolean claimed;

        Relic(Location pos, ItemDisplay display, boolean claimed) {
            this.pos = pos;
            this.display = display;
            this.claimed = claimed;
        }
    }
}
