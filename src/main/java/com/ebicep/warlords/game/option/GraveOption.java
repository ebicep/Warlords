package com.ebicep.warlords.game.option;

import com.ebicep.warlords.events.WarlordsDeathEvent;
import com.ebicep.warlords.events.WarlordsRespawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Spawns a cute memorial grave an a player death event
 */
public class GraveOption implements Option, Listener {

    public static final Material DEFAULT_GRAVE_MATERIAL = Material.SAPLING;
    public static final byte DEFAULT_GRAVE_MATERIAL_DATA = (byte) 5;
    public static final Function<WarlordsPlayer, String> DEFAULT_GRAVE_TEXT = wp
            -> wp.getTeam().teamColor() + wp.getName() + ChatColor.GRAY + " - " + ChatColor.YELLOW + "DEAD";

    private final List<Grave> graves = new LinkedList<>();
    private Material material;
    private byte data;
    private Function<WarlordsPlayer, String> graveName;
    private boolean activated = true;

    public GraveOption() {
        this(DEFAULT_GRAVE_MATERIAL, DEFAULT_GRAVE_MATERIAL_DATA, DEFAULT_GRAVE_TEXT);
    }

    public GraveOption(Function<WarlordsPlayer, String> graveName) {
        this(DEFAULT_GRAVE_MATERIAL, DEFAULT_GRAVE_MATERIAL_DATA, graveName);
    }

    public GraveOption(Material material) {
        this(material, (byte) 0, DEFAULT_GRAVE_TEXT);
    }

    public GraveOption(Material material, byte data) {
        this(material, data, DEFAULT_GRAVE_TEXT);
    }

    public GraveOption(Material material, byte data, Function<WarlordsPlayer, String> graveName) {
        this.material = Objects.requireNonNull(material, "material");
        this.data = data;
        this.graveName = Objects.requireNonNull(graveName, "graveName");
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = Objects.requireNonNull(material, "material");
    }

    public byte getData() {
        return data;
    }

    public void setData(byte data) {
        this.data = data;
    }

    public Function<WarlordsPlayer, String> getGraveName() {
        return graveName;
    }

    public void setGraveName(Function<WarlordsPlayer, String> graveName) {
        this.graveName = Objects.requireNonNull(graveName, "graveName");
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    @Override
    public void register(Game game) {
        game.registerEvents(this);
    }

    @Override
    public void onGameCleanup(Game game) {
        this.activated = false;
        for (Grave grave : graves) {
            grave.remove();
        }
        graves.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEvent(WarlordsDeathEvent event) {
        addGrave(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEvent(WarlordsRespawnEvent event) {
        for (Iterator<Grave> it = this.graves.iterator(); it.hasNext();) {
            Grave grave = it.next();
            if (grave.getOwner() == event.getPlayer()) {
                grave.remove();
                it.remove();
            }
        }
    }

    public void addGrave(WarlordsPlayer player) {
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
                            if (underTest.getType().isTransparent() || underTest.getType() == Material.STANDING_BANNER || underTest.getType() == Material.BANNER) {
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
            bestGraveCandidate.setData(data);
            ArmorStand deathStand = (ArmorStand) player.getWorld().spawnEntity(bestGraveCandidate.getLocation().add(.5, -1.5, .5), EntityType.ARMOR_STAND);
            String name = this.graveName.apply(player);
            if (name != null) {
                deathStand.setCustomName(name);
                deathStand.setCustomNameVisible(true);
            }
            deathStand.setGravity(false);
            deathStand.setVisible(false);
            this.graves.add(new Grave(player, deathStand, bestGraveCandidate, material, data));
        }
    }

    private static class Grave {

        private final WarlordsPlayer owner;
        private final ArmorStand armorStand;
        private final Block block;
        private final Material material;
        private final byte data;

        public Grave(WarlordsPlayer owner, ArmorStand armorStand, Block location, Material material, byte data) {
            this.owner = owner;
            this.armorStand = armorStand;
            this.block = location;
            this.material = material;
            this.data = data;
        }

        public WarlordsPlayer getOwner() {
            return owner;
        }

        public void remove() {
            Block deathBlock = block;
            if (deathBlock.getType() == material && deathBlock.getData() == data) {
                deathBlock.setType(Material.AIR);
            }
            armorStand.remove();
        }
    }

}
