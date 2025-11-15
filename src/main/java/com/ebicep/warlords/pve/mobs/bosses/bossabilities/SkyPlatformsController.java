package com.ebicep.warlords.pve.mobs.bosses.bossabilities;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.Particle;

import java.util.*;
import java.util.function.Supplier;

public class SkyPlatformsController {

    private final WarlordsEntity source;                 // boss (not strictly required, used for sounds/team filters if needed)
    private final Supplier<Location> centerSupplier;  // () -> arena center XZ (we override Y per layer)

    // Config
    private final List<Integer> layerYs;             // e.g., List.of(150, 135, 120) (top -> bottom)
    private final List<Double> radii;                // same size as layerYs, circle radius per layer
    private final Material platformMaterial;         // e.g., LIGHT_BLUE_STAINED_GLASS or BARRIER
    private final boolean showEdgeParticles;         // outline each tick
    private final double ringStep;                   // particle density for edge
    private final int crumbleTicks;                  // duration of crumble animation in ticks
    private final int chunksPerTick;                 // how many small patches to delete per crumble tick
    private final boolean spawnDebris;               // falling glass block debris for flair

    // Visual
    private final Particle.DustOptions edgeDust =
            new Particle.DustOptions(Color.fromRGB(150, 200, 255), 1.4f);

    // Runtime
    private final List<Layer> layers = new ArrayList<>();
    private int currentLayer = -1;                   // index of the active/solid layer
    private GameRunnable loop;
    private boolean running = false;
    private final Random rng = new Random();

    public SkyPlatformsController(
            WarlordsEntity source,
            Supplier<Location> centerSupplier,
            List<Integer> layerYs,
            List<Double> radii,
            Material platformMaterial,
            boolean showEdgeParticles,
            double ringStep,
            int crumbleTicks,
            int chunksPerTick,
            boolean spawnDebris
    ) {
        if (layerYs.size() != radii.size()) {
            throw new IllegalArgumentException("layerYs and radii must have the same size");
        }
        this.source = source;
        this.centerSupplier = centerSupplier;
        this.layerYs = new ArrayList<>(layerYs);
        this.radii = new ArrayList<>(radii);
        this.platformMaterial = platformMaterial;
        this.showEdgeParticles = showEdgeParticles;
        this.ringStep = Math.max(0.25, ringStep);
        this.crumbleTicks = Math.max(10, crumbleTicks);
        this.chunksPerTick = Math.max(1, chunksPerTick);
        this.spawnDebris = spawnDebris;
    }

    /* ================= Public API ================= */

    public void start(Game game) {
        if (running) return;
        running = true;
        layers.clear();
        currentLayer = -1;

        Location center = safeCenter();
        World w = center.getWorld();
        if (w == null) { stop(); return; }

        // Build all layer descriptors (we only place blocks when activating a layer)
        for (int i = 0; i < layerYs.size(); i++) {
            int y = layerYs.get(i);
            double r = radii.get(i);
            layers.add(new Layer(y, r));
        }

        // Activate the first (top) layer
        if (!layers.isEmpty()) {
            currentLayer = 0;
            placeLayer(w, layers.getFirst(), center);
            Utils.playGlobalSound(new Location(w, center.getX(), layers.getFirst().y, center.getZ()),
                    Sound.ENTITY_ELDER_GUARDIAN_CURSE, 500, 0.5f);
        }

        // Tiny loop for edge particles
        loop = new GameRunnable(game) {
            @Override
            public void run() {
                if (!showEdgeParticles) return;
                if (currentLayer < 0 || currentLayer >= layers.size()) return;

                Layer L = layers.get(currentLayer);
                drawEdgeRing(w, center.getX(), L.y + 0.05, center.getZ(), L.radius, edgeDust);
            }
        };
        loop.runTaskTimer(0, 1);
    }

    public void stop() {
        running = false;
        if (loop != null) { loop.cancel(); loop = null; }

        for (Layer L : layers) {
            for (BlockPos bp : L.placedBlocks) {
                Block b = bp.toBlock();
                if (b != null) b.setType(Material.AIR, false);
            }
            L.placedBlocks.clear();
            L.crumbling = false;
        }
        layers.clear();
        currentLayer = -1;
    }

    public boolean isRunning() { return running; }

    /**
     * Call this when your event completes (e.g., Crystal Conduits success).
     * The current layer will crumble over 'crumbleTicks', causing players to fall to the next layer.
     */
    public void triggerNextDrop(Game game) {
        if (!running) return;
        if (currentLayer < 0 || currentLayer >= layers.size()) return;

        Location center = safeCenter();
        World w = center.getWorld();
        if (w == null) return;

        Layer L = layers.get(currentLayer);
        if (L.crumbling) return; // already crumbling

        L.crumbling = true;
        Utils.playGlobalSound(center, Sound.ENTITY_WARDEN_EMERGE, 500, 0.5f);

        List<List<BlockPos>> chunks = chunkify(L.placedBlocks, 5);
        Collections.shuffle(chunks, rng);

        final int perTick = Math.max(1, (int) Math.ceil(chunks.size() / (double) crumbleTicks));

        new GameRunnable(game) {
            int chunkIndex = 0;

            @Override
            public void run() {
                for (int c = 0; c < perTick && chunkIndex < chunks.size(); c++, chunkIndex++) {
                    for (BlockPos bp : chunks.get(chunkIndex)) {
                        Block b = bp.toBlock();
                        if (b == null) continue;

                        if (spawnDebris) spawnDebrisOnce(w, b);
                        b.setType(Material.AIR, false); // hard clear
                    }
                }

                if (chunkIndex >= chunks.size()) {
                    // absolutely no leftovers
                    L.placedBlocks.clear();
                    L.crumbling = false;

                    currentLayer++;
                    if (currentLayer < layers.size()) {
                        placeLayer(w, layers.get(currentLayer), center);
                    }
                    cancel();
                }
            }
        }.runTaskTimer(0, 1);
    }

    /* ================= Internals ================= */

    private void placeLayer(World w, Layer L, Location center) {
        // Fill a disc at (center.x, L.y, center.z) of radius L.radius with platformMaterial
        int y = L.y;
        double r = L.radius;
        int minX = (int)Math.floor(center.getX() - r);
        int maxX = (int)Math.ceil(center.getX() + r);
        int minZ = (int)Math.floor(center.getZ() - r);
        int maxZ = (int)Math.ceil(center.getZ() + r);

        BlockData data = platformMaterial.createBlockData();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                double dx = x + 0.5 - center.getX();
                double dz = z + 0.5 - center.getZ();
                if (dx*dx + dz*dz <= r*r) {
                    Block b = w.getBlockAt(x, y, z);
                    if (b.getType() == Material.AIR || b.getType() == platformMaterial) {
                        b.setBlockData(data, false);
                        L.placedBlocks.add(new BlockPos(w, x, y, z));
                    }
                }
            }
        }
    }

    private void drawEdgeRing(World w, double cx, double y, double cz, double radius, Particle.DustOptions dust) {
        double twoPi = Math.PI * 2.0;
        double step = Math.max(0.02, ringStep / Math.max(0.1, radius));
        for (double a = 0; a < twoPi; a += step) {
            double x = cx + Math.cos(a) * radius;
            double z = cz + Math.sin(a) * radius;
            w.spawnParticle(Particle.DUST, x, y, z, 1, 0, 0, 0, 0.0, dust);
        }
    }

    private void spawnDebrisOnce(World w, Block b) {
        try {
            FallingBlock fb = w.spawnFallingBlock(b.getLocation().add(0.5, 0.0, 0.5), b.getBlockData());
            fb.setDropItem(false);
            fb.setHurtEntities(false);
            fb.setGravity(true);
            fb.setCancelDrop(true);
        } catch (Exception ignored) {}
    }

    private List<List<BlockPos>> chunkify(List<BlockPos> blocks, int approxChunkSize) {
        // Simple spatial bucketing by coarse grid to make chunks look “patchy”
        Map<Long, List<BlockPos>> buckets = new HashMap<>();
        int cell = Math.max(2, (int)Math.round(Math.sqrt(approxChunkSize))); // ~2x2 or 3x3
        for (BlockPos bp : blocks) {
            long key = (((long)(bp.x / cell)) & 0xFFFFFFFFL) << 32 | (((long)(bp.z / cell)) & 0xFFFFFFFFL);
            buckets.computeIfAbsent(key, k -> new ArrayList<>()).add(bp);
        }
        List<List<BlockPos>> chunks = new ArrayList<>(buckets.values());
        // Also split too-large buckets
        List<List<BlockPos>> result = new ArrayList<>();
        for (List<BlockPos> ch : chunks) {
            if (ch.size() <= approxChunkSize * 2) {
                result.add(ch);
            } else {
                // split evenly
                for (int i = 0; i < ch.size(); i += approxChunkSize) {
                    result.add(ch.subList(i, Math.min(i + approxChunkSize, ch.size())));
                }
            }
        }
        return result;
    }

    private Location safeCenter() {
        Location c = centerSupplier.get();
        return (c == null) ? new Location(Bukkit.getWorlds().getFirst(), 0, 100, 0) : c.clone();
    }

    /* ---------------- Data ---------------- */

    private static final class Layer {
        final int y;
        final double radius;
        final List<BlockPos> placedBlocks = new ArrayList<>();
        boolean crumbling = false;

        Layer(int y, double radius) {
            this.y = y;
            this.radius = radius;
        }
    }

    private static final class BlockPos {
        final World world;
        final int x, y, z;

        BlockPos(World world, int x, int y, int z) {
            this.world = world;
            this.x = x; this.y = y; this.z = z;
        }

        Block toBlock() {
            if (world == null) return null;
            return world.getBlockAt(x, y, z);
        }

        Location center() {
            return new Location(world, x + 0.5, y + 0.5, z + 0.5);
        }
    }
}
