package com.ebicep.warlords.game.option.pve.darkdescent.objectives;

import com.ebicep.warlords.game.option.pve.darkdescent.DarkDescentOption;
import com.ebicep.warlords.util.bukkit.EntitiesUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LightOrbObjective implements DarkDescentObjective {

    private static final int ORB_COUNT = 3;
    private static final double PICKUP_RADIUS = 1.5;

    private final DarkDescentOption option;
    private final List<ItemDisplay> orbs = new ArrayList<>();

    public LightOrbObjective(DarkDescentOption option) {
        this.option = option;
        for (int i = 0; i < ORB_COUNT; i++) {
            Location location = option.randomArenaLocation(20).add(0, 1, 0);
            orbs.add(location.getWorld().spawn(location, ItemDisplay.class, display -> {
                display.setItemStack(new ItemStack(Material.GLOWSTONE));
                display.setBillboard(Display.Billboard.CENTER);
                display.setBrightness(EntitiesUtils.MAX_BRIGHTNESS);
                display.setGlowing(true);
                display.setPersistent(false);
            }));
        }
    }

    @Override
    public Component getTitle() {
        return Component.text("Collect " + ORB_COUNT + " light orbs", NamedTextColor.YELLOW);
    }

    @Override
    public void tick() {
        orbs.removeIf(orb -> {
            Location orbLocation = orb.getLocation();
            boolean collected = option.getGame()
                                      .warlordsPlayers()
                                      .anyMatch(warlordsPlayer -> warlordsPlayer.isAlive() && warlordsPlayer.getLocation().distanceSquared(orbLocation) <= PICKUP_RADIUS * PICKUP_RADIUS);
            if (collected) {
                orbLocation.getWorld().playSound(orbLocation, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 2, 1.5f);
                orb.remove();
            }
            return collected;
        });
    }

    @Override
    public boolean isComplete() {
        return orbs.isEmpty();
    }

    @Override
    public Component getScoreboardLine() {
        return Component.text("Light Orbs: ")
                        .append(Component.text((ORB_COUNT - orbs.size()) + "/" + ORB_COUNT, isComplete() ? NamedTextColor.GREEN : NamedTextColor.YELLOW));
    }

    @Override
    public void cleanup() {
        orbs.forEach(Entity::remove);
        orbs.clear();
    }
}
