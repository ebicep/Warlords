package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.citizensnpcs.api.ai.BehaviorController;
import net.citizensnpcs.trait.ArmorStandTrait;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;

import java.util.ArrayList;
import java.util.List;

public class AnomalyRelic extends AbstractMob implements BossMinionMob {

    private static final int ORBITING_FRAGMENT_COUNT = 4;

    private final int objectiveIndex;
    private final List<Entity> visualEntities = new ArrayList<>();
    private final List<ItemDisplay> orbitingFragments = new ArrayList<>();
    private Location visualCenter;

    public AnomalyRelic(Location location, int objectiveIndex, int maxHealth) {
        super(location, getRelicName(objectiveIndex), maxHealth, 0, 0, 0, 0);
        this.objectiveIndex = objectiveIndex;
    }

    private static String getRelicName(int objectiveIndex) {
        return switch (objectiveIndex) {
            case 0 -> "Verdant Relic";
            case 1 -> "Obsidian Relic";
            default -> "Nether Relic";
        };
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.MITHRA_EGG_SAC;
    }

    @Override
    public void updateEquipment() {
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        BehaviorController goalController = npc.getDefaultBehaviorController();
        goalController.clear();
        ArmorStandTrait armorStandTrait = warlordsNPC.getNpc().getOrAddTrait(ArmorStandTrait.class);
        armorStandTrait.setVisible(false);
        armorStandTrait.setGravity(false);
        spawnRelicVisuals();
        new GameRunnable(option.getGame()) {
            private int animationTicks;

            @Override
            public void run() {
                if (visualCenter == null || warlordsNPC == null || warlordsNPC.isDead()) {
                    cancel();
                    return;
                }
                animateRelic(animationTicks++);
            }
        }.runTaskTimer(0, 1);
    }

    private void spawnRelicVisuals() {
        cleanupVisuals();
        visualCenter = spawnLocation.clone().add(0, .35, 0);

        ItemDisplay core = spawnDisplay(getCoreMaterial(), visualCenter, 1.65f);
        core.setGlowing(true);

        ItemDisplay crown = spawnDisplay(Material.NETHER_STAR, visualCenter.clone().add(0, 1.15, 0), .8f);
        crown.setGlowing(true);

        for (int i = 0; i < ORBITING_FRAGMENT_COUNT; i++) {
            double angle = Math.PI * 2 * i / ORBITING_FRAGMENT_COUNT;
            Location location = visualCenter.clone().add(Math.cos(angle) * 1.35, .25, Math.sin(angle) * 1.35);
            ItemDisplay fragment = spawnDisplay(getFragmentMaterial(), location, .65f);
            fragment.setGlowing(true);
            orbitingFragments.add(fragment);
        }
    }

    private ItemDisplay spawnDisplay(Material material, Location location, float scale) {
        ItemDisplay display = location.getWorld().spawn(location, ItemDisplay.class);
        display.setItemStack(new ItemStack(material));
        display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
        display.setBillboard(Display.Billboard.CENTER);
        display.setBrightness(new Display.Brightness(15, 15));
        display.setGravity(false);
        display.setInvulnerable(true);
        display.setPersistent(false);
        Transformation transformation = display.getTransformation();
        transformation.getScale().set(scale);
        display.setTransformation(transformation);
        visualEntities.add(display);
        return display;
    }

    private void animateRelic(int ticksElapsed) {
        double rotation = ticksElapsed * .045;
        for (int i = 0; i < orbitingFragments.size(); i++) {
            double angle = rotation + Math.PI * 2 * i / orbitingFragments.size();
            double y = .25 + Math.sin(rotation * 1.5 + i) * .2;
            orbitingFragments.get(i).teleport(visualCenter.clone().add(Math.cos(angle) * 1.35, y, Math.sin(angle) * 1.35));
        }
        if (ticksElapsed % 5 == 0) {
            visualCenter.getWorld().spawnParticle(getThemeParticle(), visualCenter, 8, .8, .8, .8, .02);
            visualCenter.getWorld().spawnParticle(Particle.ENCHANT, visualCenter, 6, 1, 1.2, 1, .03);
        }
    }

    private Material getCoreMaterial() {
        return switch (objectiveIndex) {
            case 0 -> Material.EMERALD_BLOCK;
            case 1 -> Material.CRYING_OBSIDIAN;
            default -> Material.NETHERITE_BLOCK;
        };
    }

    private Material getFragmentMaterial() {
        return switch (objectiveIndex) {
            case 0 -> Material.SLIME_BALL;
            case 1 -> Material.MAGMA_CREAM;
            default -> Material.BLAZE_POWDER;
        };
    }

    private Particle getThemeParticle() {
        return switch (objectiveIndex) {
            case 0 -> Particle.HAPPY_VILLAGER;
            case 1 -> Particle.LAVA;
            default -> Particle.SOUL_FIRE_FLAME;
        };
    }

    private void cleanupVisuals() {
        visualEntities.forEach(Entity::remove);
        visualEntities.clear();
        orbitingFragments.clear();
        visualCenter = null;
    }

    @Override
    public void cleanup(PveOption pveOption) {
        cleanupVisuals();
        super.cleanup(pveOption);
    }

    public int getObjectiveIndex() {
        return objectiveIndex;
    }
}
