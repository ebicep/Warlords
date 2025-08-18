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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
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
            record RingSword(ItemDisplay display, double theta) {}

            List<RingSword> ring = new ArrayList<>();
            Location center = warlordsNPC.getLocation();
            World world = warlordsNPC.getWorld();
            double radius = 8;
            int count = 9;

            Utils.playGlobalSound(warlordsNPC.getLocation(), "arcanist.beaconshadow.activation", 10, 0.7f);
            Utils.playGlobalSound(warlordsNPC.getLocation(), "arcanist.beaconshadow.activation", 10, 0.7f);
            for (int i = 0; i < count; i++) {
                double theta = 2 * Math.PI * i / count;                 // 0..2π around center
                double x = center.getX() + radius * Math.cos(theta);
                double z = center.getZ() + radius * Math.sin(theta);

                ItemDisplay d = world.spawn(new Location(world, x, center.clone().add(0, 2, 0).getY(), z), ItemDisplay.class, disp -> {
                    disp.setItemStack(new ItemStack(Material.NETHERITE_SWORD));
                    disp.setBillboard(Display.Billboard.FIXED);

                    // Face inward, blade upright
                    Quaternionf faceCenter = new Quaternionf().rotateY((float) (theta + Math.PI));
                    Quaternionf upright    = new Quaternionf().rotateX((float) Math.toRadians(+90)); // swap to +90 if your model needs it
                    Quaternionf baseRight  = new Quaternionf(faceCenter).mul(upright);

                    disp.setTransformation(new Transformation(
                            new Vector3f(0, 0, 0),              // leftRotation = identity (we animate this)
                            baseRight,                  // <-- leftRotation placeholder
                            new Vector3f(30f, 30f, 30f),
                            new Quaternionf()                           // rightRotation = constant base orientation
                    ));
                });

                ring.add(new RingSword(d, theta));
            }

            new GameRunnable(warlordsNPC.getGame()) {
                int t = 0;
                @Override public void run() {
                    t++;
                    float p = Math.min(1f, t / 20f);                   // 20 ticks = 1s
                    // ease-out (feels like gravity)
                    p = (float) Math.sin(p * Math.PI * 0.5);
                    float tilt = (float) Math.toRadians(90) * p;

                    for (RingSword rs : ring) {
                        // tangent axis = cross(UP, outward(θ)) = (sinθ, 0, −cosθ)
                        Vector3f axis = new Vector3f((float) Math.sin(rs.theta()), 0f, (float) -Math.cos(rs.theta()));
                        Quaternionf left = new Quaternionf().rotateAxis(+tilt, axis); // use +tilt if it goes the wrong way

                        Transformation cur = rs.display().getTransformation();
                        rs.display().setTransformation(new Transformation(
                                new Vector3f(cur.getTranslation()),
                                left,                                    // animate in world space
                                new Vector3f(cur.getScale()),
                                new Quaternionf(cur.getRightRotation()) // keep base orientation
                        ));
                    }

                    if (p >= 1f) {
                        for (RingSword rs : ring) {
                            rs.display.remove();
                            Utils.playGlobalSound(center, "arcanist.beacon.impact", 3f, 0.7f);
                        }
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
