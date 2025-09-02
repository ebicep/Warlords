package com.ebicep.warlords.pve.mobs;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class DamagePhaseController {

    private final WarlordsEntity boss;

    private boolean inWindow = false;

    private ItemDisplay halo;
    private TextDisplay holo;
    private GameRunnable followTask;
    private GameRunnable spinTask;

    private double headOffsetY = 3;
    private final Vector3f haloScale = new Vector3f(1.5f, 1.5f, 1.5f);
    private final float spinDegPerTick = 6f;

    public DamagePhaseController(WarlordsEntity boss) {
        this.boss = boss;
    }

    /* ---------- Public API ---------- */

    /** Opens a damage window for 'durationTicks' (e.g., 20*12 = 12s). */
    public void openWindow(int durationTicks) {
        if (inWindow) return;
        inWindow = true;

        spawnIndicator();
        startFollowingIndicator();
        startSpinningHalo();

        // Auto-close after duration
        new GameRunnable(boss.getGame()) {
            int t = 0;
            @Override public void run() {
                if (++t >= durationTicks) {
                    closeWindow();
                    cancel();
                }

                if (t % 20 == 0) {
                    Utils.playGlobalSound(boss.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_HIT, 500, 0.6f);
                    Utils.playGlobalSound(boss.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_HIT, 500, 0.6f);
                }
            }
        }.runTaskTimer(0, 1);
    }

    /** Manually closes the window and cleans up. */
    public void closeWindow() {
        if (!inWindow) return;
        inWindow = false;
        stopFollowingIndicator();
        stopSpinningHalo();
        removeIndicator();
    }

    public boolean isInDamageWindow() {
        return inWindow;
    }

    /* ---------- Visuals ---------- */

    private void spawnIndicator() {
        Location head = boss.getLocation().clone().add(0, headOffsetY, 0);
        World w = head.getWorld();

        // OPTION A: Halo (ItemDisplay) – a flat sword that spins like a crown
        halo = w.spawn(head, ItemDisplay.class, disp -> {
            disp.setBillboard(Display.Billboard.FIXED);
            disp.setInterpolationDuration(0);
            disp.setItemStack(new ItemStack(Material.AMETHYST_CLUSTER));
            disp.setTransformation(new Transformation(
                    new Vector3f(0,0,0),
                    new Quaternionf(),                         // leftRotation (animated yaw)
                    new Vector3f(haloScale),                   // scale up
                    new Quaternionf()
            ));
            disp.setPersistent(true);
        });

        // OPTION B (optional or in addition): floating text
        holo = w.spawn(head.clone().add(0, 2, 0), TextDisplay.class, td -> {
            td.setBillboard(Display.Billboard.CENTER);
            td.setSeeThrough(true);
            td.setBackgroundColor(Color.BLACK);
            td.setText("§d§lVULNERABLE");
            td.setLineWidth(120);
        });
    }

    private void removeIndicator() {
        if (halo != null && !halo.isDead()) halo.remove();
        if (holo != null && !holo.isDead()) holo.remove();
        halo = null;
        holo = null;
    }

    private void startFollowingIndicator() {
        if (followTask != null) return;
        followTask = new GameRunnable(boss.getGame()) {
            @Override public void run() {
                if (!inWindow) { cancel(); return; }
                Location head = boss.getLocation().clone().add(0, headOffsetY, 0);

                if (halo != null && !halo.isDead() && halo.getWorld() == head.getWorld()) {
                    halo.teleport(head);
                }
                if (holo != null && !holo.isDead() && holo.getWorld() == head.getWorld()) {
                    holo.teleport(head.clone().add(0, 0.4, 0));
                }
            }
        };
        followTask.runTaskTimer(0, 1);
    }

    private void stopFollowingIndicator() {
        if (followTask != null) {
            followTask.cancel();
            followTask = null;
        }
    }

    private void startSpinningHalo() {
        if (halo == null) return;
        if (spinTask != null) return;

        spinTask = new GameRunnable(boss.getGame()) {
            int ticks = 0;
            @Override public void run() {
                if (!inWindow || halo == null || halo.isDead()) { cancel(); return; }
                ticks++;
                float yawRad = (float) Math.toRadians((ticks * spinDegPerTick) % 360f);

                // Yaw in leftRotation, flat in rightRotation
                var yaw = new Quaternionf().rotateY(yawRad);
                var cur = halo.getTransformation();
                halo.setTransformation(new Transformation(
                        new Vector3f(0,0,0),
                        yaw,
                        new Vector3f(cur.getScale()),
                        cur.getRightRotation()
                ));
            }
        };
        spinTask.runTaskTimer(0, 1);
    }

    private void stopSpinningHalo() {
        if (spinTask != null) {
            spinTask.cancel();
            spinTask = null;
        }
    }
}
