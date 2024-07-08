package com.ebicep.warlords.game.option;

import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsRespawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Spawns a cute memorial grave an a player death event
 */
public class GraveOption implements Option, Listener {

    public static final Material DEFAULT_GRAVE_MATERIAL = Material.DARK_OAK_SAPLING;
    public static final byte DEFAULT_GRAVE_MATERIAL_DATA = (byte) 5;
    public static final Function<WarlordsEntity, Component> DEFAULT_GRAVE_TEXT = wp ->
            Component.textOfChildren(
                    Component.text(wp.getName(), wp.getTeam().getTeamColor()),
                    Component.text(" - ", NamedTextColor.GRAY),
                    Component.text("DEAD", NamedTextColor.YELLOW)
            );

    private final List<Grave> graves = new LinkedList<>();
    private Material material;
    private Function<WarlordsEntity, Component> graveName;
    private boolean activated = true;

    public GraveOption() {
        this(DEFAULT_GRAVE_MATERIAL, DEFAULT_GRAVE_TEXT);
    }

    public GraveOption(Material material, Function<WarlordsEntity, Component> graveName) {
        this.material = Objects.requireNonNull(material, "material");
        this.graveName = Objects.requireNonNull(graveName, "graveName");
    }

    public GraveOption(Function<WarlordsEntity, Component> graveName) {
        this(DEFAULT_GRAVE_MATERIAL, graveName);
    }

    public GraveOption(Material material) {
        this(material, DEFAULT_GRAVE_TEXT);
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = Objects.requireNonNull(material, "material");
    }

    public Function<WarlordsEntity, Component> getGraveName() {
        return graveName;
    }

    public void setGraveName(Function<WarlordsEntity, Component> graveName) {
        this.graveName = Objects.requireNonNull(graveName, "graveName");
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(this);
    }

    @Override
    public void onGameCleanup(@Nonnull Game game) {
        this.activated = false;
        for (Grave grave : graves) {
            grave.remove();
        }
        graves.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(WarlordsDeathEvent event) {
        if (event.getWarlordsEntity().shouldSpawnGrave()) {
            addGrave(event.getWarlordsEntity());
        }
    }

    public void addGrave(WarlordsEntity player) {
        if (!this.activated) {
            return;
        }
        Location deathLocation = player.getLocation();
        Block bestGraveCandidate = null;
        boolean isFlagCarrier = player.getCarriedFlag() != null;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (isFlagCarrier && x == 0 && z == 0) {
                    // This player is a flag carrier, prevent placing the grave at the direct location of the player
                    continue;
                }

                Location toTest = deathLocation.clone().add(x, 2, z);
                Block lastBlock = toTest.getBlock();

                if (lastBlock.getType() == Material.AIR) {
                    toTest.subtract(0, 1, 0);
                    for (; toTest.getY() > 0; toTest.subtract(0, 1, 0)) {
                        Block underTest = toTest.getBlock();
                        if (underTest.getType() != Material.AIR) {
                            if (underTest.getType()
                                         .isTransparent() || underTest.getBlockData() instanceof Banner || underTest.getType() == Material.BLACK_BANNER) {
                                // We have hit a sappling, fence, torch or other non-solid
                                break;
                            }
                            // We have hit a solid block. Go back 1 tile
                            toTest.add(0, 1, 0);
                            // Check if we found a better tile for the grave
                            if (bestGraveCandidate != null) {
                                double newDistance = toTest.distanceSquared(deathLocation);
                                double existingDistance = bestGraveCandidate.getLocation(toTest).distanceSquared(deathLocation);
                                if (newDistance >= existingDistance) {
                                    // Our new candidate is not closer, skip
                                    break;
                                }
                            }
                            bestGraveCandidate = lastBlock;
                            //
                            break;
                        }
                        lastBlock = underTest;
                    }
                }
            }
        }

        if (bestGraveCandidate != null) {
            //spawn grave
            bestGraveCandidate.setType(material);
            ArmorStand deathStand = Utils.spawnArmorStand(bestGraveCandidate.getLocation().add(.5, -1.5, .5), armorStand -> {
                Component name = this.graveName.apply(player);
                if (name != null) {
                    armorStand.customName(name);
                    armorStand.setCustomNameVisible(true);
                }
            });
            this.graves.add(new Grave(player, deathStand, bestGraveCandidate, material));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEvent(WarlordsRespawnEvent event) {
        for (Iterator<Grave> it = this.graves.iterator(); it.hasNext(); ) {
            Grave grave = it.next();
            if (grave.getOwner() == event.getWarlordsEntity()) {
                grave.remove();
                it.remove();
            }
        }
    }

    private static class Grave {

        private final WarlordsEntity owner;
        private final ArmorStand armorStand;
        private final Block block;
        private final Material material;

        public Grave(WarlordsEntity owner, ArmorStand armorStand, Block location, Material material) {
            this.owner = owner;
            this.armorStand = armorStand;
            this.block = location;
            this.material = material;
        }

        public WarlordsEntity getOwner() {
            return owner;
        }

        public void remove() {
            Block deathBlock = block;
            if (deathBlock.getType() == material) {
                deathBlock.setType(Material.AIR);
            }
            armorStand.remove();
        }
    }

}
