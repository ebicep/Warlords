package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class EarthenSpike extends AbstractAbility {

    private List<ArrayList<EarthenSpikeBlock>> spikeArrays = new ArrayList<>();

    public List<ArrayList<EarthenSpikeBlock>> getSpikeArrays() {
        return spikeArrays;
    }

    private List<EarthenSpike> spikes = new ArrayList<>();
    private List<ArmorStand> spikeArmorStands = new ArrayList<>(new ArrayList<>());

    public EarthenSpike() {
        super("Earthen Spike", -476, -662, 0, 120, 15, 175,
                "§7Send forth an underground earth spike\n" +
                        "§7that locks onto a targeted enemy player.\n" +
                        "§7When the spike reaches its target it\n" +
                        "§7emerges from the ground, dealing §c%dynamic.value% §7-\n" +
                        "§c%dynamic.value §7damage to any nearby enemies and\n" +
                        "§7launches them up into the air.");
    }

    @Override
    public void onActivate(Player player) {
        Location location = player.getLocation();
        List<Entity> near = player.getNearbyEntities(7.0D, 6.0D, 7.0D);
        near = Utils.filterOutTeammates(near, player);
        for (Entity entity : near) {
            if (entity instanceof Player) {
                Player nearPlayer = (Player) entity;
                if (nearPlayer.getGameMode() != GameMode.SPECTATOR && Utils.getLookingAt(player, nearPlayer)) {
                    FallingBlock block = player.getWorld().spawnFallingBlock(location.clone(), location.getWorld().getBlockAt((int) location.getX(), (int) location.getY(), (int) location.getZ()).getType(), (byte) 0);
                    block.setVelocity(new Vector(0, .2, 0));
                    ArrayList<EarthenSpikeBlock> spikeList = new ArrayList<>();
                    spikeList.add(new EarthenSpikeBlock(block, nearPlayer, Warlords.getPlayer(player)));
                    EarthenSpike spike = new EarthenSpike();
                    spike.getSpikeArrays().add(spikeList);

                    Warlords.getPlayer(player).subtractEnergy(energyCost);
                    spikes.add(spike);
                    WarlordsEvents.addEntityUUID(block.getUniqueId());
                    break;
                }
            }
        }

        new BukkitRunnable() {

            @Override
            public void run() {

                //TODO redo this, remove all arrays
                if (spikes.size() == 0 && spikeArmorStands.size() == 0) {
                    this.cancel();
                }
                //all earthen spikes +1 when right click
                for (int i = 0; i < spikes.size(); i++) {
                    //earthen spike BLOCk array
                    List<ArrayList<EarthenSpikeBlock>> tempSpikes = spikes.get(i).getSpikeArrays();
                    //block
                    if (tempSpikes.size() != 0) {
                        ArrayList<EarthenSpikeBlock> spike = tempSpikes.get(0);
                        FallingBlock block = spike.get(spike.size() - 1).getBlock();
                        Player player = spike.get(spike.size() - 1).getPlayer();
                        WarlordsPlayer user = spike.get(spike.size() - 1).getUser();
                        if (Math.abs(player.getLocation().getX() - block.getLocation().getX()) + Math.abs(player.getLocation().getZ() - block.getLocation().getZ()) > 1) {
                            Location location = block.getLocation();
                            //moving diagonally
                            if (Math.abs(player.getLocation().getX() - location.getX()) >= Math.abs(player.getLocation().getZ() - location.getZ())) {
                                if (player.getLocation().getX() < location.getX()) {
                                    location.add(-1, 0, 0);
                                } else {
                                    location.add(1, 0, 0);
                                }
                            } else {
                                if (player.getLocation().getZ() < location.getZ()) {
                                    location.add(0, 0, -1);
                                } else {
                                    location.add(0, 0, 1);
                                }
                            }
                            //moving vertically
                            if (player.getLocation().getY() < location.getY()) {
                                for (int j = 0; j < 10; j++) {
                                    if (location.getWorld().getBlockAt(location).getType() != Material.AIR) {
                                        location.add(0, -1, 0);
                                    } else {
                                        break;
                                    }
                                }
                            } else {
                                for (int j = 0; j < 10; j++) {
                                    if (location.getWorld().getBlockAt(location).getType() != Material.AIR) {
                                        location.add(0, 1, 0);
                                    } else {
                                        break;
                                    }
                                }
                            }
                            FallingBlock newBlock = player.getWorld().spawnFallingBlock(location, location.getWorld().getBlockAt((int) location.getX(), location.getWorld().getHighestBlockYAt(location) - 1, (int) location.getZ()).getType(), location.getWorld().getBlockAt((int) location.getX(), location.getWorld().getHighestBlockYAt(location) - 1, (int) location.getZ()).getData());
                            newBlock.setVelocity(new Vector(0, .2, 0));
                            newBlock.setDropItem(false);
                            spike.add(new EarthenSpikeBlock(newBlock, player, user));
                            WarlordsEvents.addEntityUUID(newBlock.getUniqueId());
                        } else if (i <= tempSpikes.size() && tempSpikes.get(i).size() > 30) {
                            spikes.remove(i);
                            i--;
                        } else {
                            Location location = player.getLocation();
                            List<Entity> onSameBlock = (List<Entity>) location.getWorld().getNearbyEntities(location, .6, 1.5, .6);
                            onSameBlock = Utils.filterOutTeammates(onSameBlock, user.getPlayer());
                            for (Entity entity : onSameBlock) {
                                if (entity instanceof Player && ((Player) entity).getGameMode() != GameMode.SPECTATOR)
                                    Warlords.getPlayer((Player) entity).addHealth(user, spikes.get(i).getName(), spikes.get(i).getMinDamageHeal(), spikes.get(i).getMaxDamageHeal(), spikes.get(i).getCritChance(), spikes.get(i).getCritMultiplier());
                            }

                            location.setYaw(0);
                            location.setY(player.getWorld().getHighestBlockYAt(location));
                            ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location.add(0, -.6, 0), EntityType.ARMOR_STAND);
                            stand.setHelmet(new ItemStack(Material.BROWN_MUSHROOM));
                            stand.setGravity(false);
                            stand.setVisible(false);
                            stand.setMarker(true);

                            spikeArmorStands.add(stand);
                            if (spikeArmorStands.size() == 1) {
                                player.setVelocity(new Vector(0, .6, 0));
                            }

                            spikes.remove(i);
                            i--;
                        }
                        if (player.getGameMode() == GameMode.SPECTATOR) {
                            spikes.remove(i);
                            i--;
                        }
                    }
                }
                if (spikeArmorStands.size() != 0) {
                    for (int i = 0; i < spikeArmorStands.size(); i++) {
                        ArmorStand armorStand = spikeArmorStands.get(i);
                        if (armorStand.getTicksLived() > 10) {
                            armorStand.remove();
                            spikeArmorStands.remove(i);
                            i--;
                        }
                    }
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 2);

    }

    public static class EarthenSpikeBlock {

        FallingBlock block;
        Player player;
        WarlordsPlayer user;

        public EarthenSpikeBlock(FallingBlock block, Player player, WarlordsPlayer user) {
            this.block = block;
            this.player = player;
            this.user = user;
        }

        public FallingBlock getBlock() {
            return block;
        }

        public void setBlock(FallingBlock block) {
            this.block = block;
        }

        public Player getPlayer() {
            return player;
        }

        public void setPlayer(Player player) {
            this.player = player;
        }

        public WarlordsPlayer getUser() {
            return user;
        }

        public void setUser(WarlordsPlayer user) {
            this.user = user;
        }
    }
}
