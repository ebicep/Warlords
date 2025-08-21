package com.ebicep.warlords.pve.mobs.bosses;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.raid.BossAbilityPhase;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class OneOfNine extends AbstractMob implements BossMob {

    private BossAbilityPhase phaseThree;

    public OneOfNine(Location spawnLocation) {
        super(
                spawnLocation,
                "One of Nine",
                10000,
                0.2f,
                20,
                4000,
                6000
        );
    }

    public OneOfNine(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        phaseThree = new BossAbilityPhase(warlordsNPC, 90, () -> {
            record Slot(double theta, int startTick, ItemDisplay display) {}

            List<Slot> ring = new ArrayList<>();
            World world = warlordsNPC.getWorld();
            Location center = new Location(world, 112.5, 14, 62.5);

            double radius = 12;
            int count = 9;
            int delayBetween = 4;     // ticks between swords appearing
            int fallDuration = 8;    // ticks to complete the 90° fall (your old value)

            Utils.playGlobalSound(warlordsNPC.getLocation(),    Sound.AMBIENT_BASALT_DELTAS_LOOP, 10, 0.7f);
            Utils.playGlobalSound(warlordsNPC.getLocation(), "arcanist.beaconshadow.activation", 10, 0.7f);

            // Prepare slots with theta and staggered start ticks
            for (int i = 0; i < count; i++) {
                double theta = 2 * Math.PI * i / count;
                int start = i * delayBetween; // sword i starts later
                ring.add(new Slot(theta, start, null));
            }

            new GameRunnable(warlordsNPC.getGame()) {
                int t = 0;

                @Override public void run() {
                    t++;

                    boolean allDone = true;

                    for (int i = 0; i < ring.size(); i++) {
                        Slot s = ring.get(i);

                        // Spawn at this sword's start tick
                        if (s.display == null && t >= s.startTick()) {
                            double x = center.getX() + radius * Math.cos(s.theta());
                            double z = center.getZ() + radius * Math.sin(s.theta());

                            ItemDisplay d = world.spawn(new Location(world, x, center.getY() + 2, z), ItemDisplay.class, disp -> {
                                disp.setItemStack(new ItemStack(Material.NETHERITE_SWORD));
                                disp.setBillboard(Display.Billboard.FIXED);

                                // Base orientation: face inward + blade upright (adjust +/−90 if your model needs)
                                Quaternionf faceCenter = new Quaternionf().rotateY((float) (s.theta() + Math.PI));
                                Quaternionf upright    = new Quaternionf().rotateX((float) Math.toRadians(+90));
                                Quaternionf baseRight  = new Quaternionf(faceCenter).mul(upright);

                                disp.setTransformation(new Transformation(
                                        new Vector3f(0, 0, 0),         // leftRotation (animated later)
                                        baseRight,             // start with identity
                                        new Vector3f(50f, 50f, 50f),
                                        new Quaternionf()                      // rightRotation = base orientation
                                ));
                            });

                            // store back
                            ring.set(i, new Slot(s.theta(), s.startTick(), d));
                        }

                        // If not yet spawned, we're not done
                        if (ring.get(i).display == null) { allDone = false; continue; }

                        // Animate only after startTick
                        int localTicks = t - s.startTick();
                        float p = Math.min(1f, localTicks / (float) fallDuration);
                        // ease-out
                        p = (float) Math.sin(p * Math.PI * 0.5f);
                        float tilt = (float) Math.toRadians(90) * p;

                        // tangent axis = outward × UP (so the sword falls outward)
                        Vector3f axis = new Vector3f((float) Math.sin(s.theta()), 0f, (float) -Math.cos(s.theta()));
                        Quaternionf left = new Quaternionf().rotateAxis(+tilt, axis); // flip sign if it still goes inward

                        ItemDisplay d = ring.get(i).display;
                        Transformation cur = d.getTransformation();
                        d.setTransformation(new Transformation(
                                new Vector3f(cur.getTranslation()),
                                left,
                                new Vector3f(cur.getScale()),
                                new Quaternionf(cur.getRightRotation())
                        ));

                        // finished?
                        if (p < 1f) allDone = false;
                    }

                    if (allDone) {
                        // (optional) remove all or leave them
                        for (Slot s : ring) {
                            if (s.display != null) s.display.remove();
                        }
                        Utils.playGlobalSound(warlordsNPC.getLocation(), "arcanist.beaconshadow.activation", 10, 0.5f);
                        cancel();
                    }
                }
            }.runTaskTimer(0, 1);
        });
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 20 == 0) {
            EffectUtils.playCrownAnimation(warlordsNPC.getLocation(), Particle.END_ROD);
        }
        phaseThree.initialize(warlordsNPC.getCurrentHealth());
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    @Override
    public TextColor getColor() {
        return NamedTextColor.GRAY;
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.ONE_OF_NINE;
    }

    @Override
    public Component getDescription() {
        return Component.text("Echoes of the Past", NamedTextColor.DARK_PURPLE);
    }
}
