package com.ebicep.warlords.pve.mobs;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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

    // Visuals
    private ItemDisplay halo;     // rotating shard/sword ring above head (option A)
    private TextDisplay holo;     // “VULNERABLE” text (option B)
    private GameRunnable followTask;   // keeps visuals attached
    private GameRunnable spinTask;     // spins the halo

    // Tuning
    private final double headOffsetY = 2.6; // how high above boss’ feet to show the indicator
    private final Vector3f haloScale = new Vector3f(5f, 5f, 5f);
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
            disp.setItemStack(new ItemStack(Material.AMETHYST_BLOCK));
            disp.setTransformation(new Transformation(
                    new Vector3f(0,0,0),
                    new Quaternionf(),                         // leftRotation (animated yaw)
                    new Vector3f(haloScale),                   // scale up
                    new Quaternionf().rotateX((float)Math.toRadians(90)) // rightRotation: lay flat
            ));
            disp.setPersistent(true);
        });

        // OPTION B (optional or in addition): floating text
        holo = w.spawn(head, TextDisplay.class, td -> {
            td.setBillboard(Display.Billboard.CENTER);
            td.setSeeThrough(true);
            td.setBackgroundColor(Color.BLACK); // subtle box
            td.setText("§e§lVULNERABLE");               // gold bold
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
