//package com.ebicep.warlords;
//
//import org.bukkit.Bukkit;
//import org.bukkit.Location;
//import org.bukkit.Material;
//import org.bukkit.World;
//import org.bukkit.block.BlockState;
//import org.bukkit.entity.Player;
//import org.bukkit.plugin.java.JavaPlugin;
//import org.bukkit.util.Vector;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class BlockWave implements Runnable {
//
//    private final JavaPlugin plugin;
//    private final double radius;
//    private final int height = 3;
//    private List<BlockState> blocks;
//    private Map<Double, List<Location>> sortedBlocks;
//    private double counter = 0;
//    private int taskId;
//
//    public BlockWave(JavaPlugin plugin, int radius) {
//        this.radius = radius + 0.5;
//        this.plugin = plugin;
//    }
//
//    //create a shockwave
//    public void createShockwave(Player player) {
//        this.blocks = getTotalBlocks(player.getLocation());
//        this.sortedBlocks = getSortedBlocks(blocks, player.getLocation());
//        this.taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 1L);
//    }
//
//    @Override
//    public void run() {
//        //go up to radius
//        if (sortedBlocks.containsKey(counter)) {
//            List<Location> locList = sortedBlocks.get(counter);
//
//            for (Location loc : locList) {
//                //delete block
//                loc.getBlock().setType(Material.AIR);
//            }
//        }
//        //check if radius was reached
//        counter++;
//        if (counter >= radius) {
//            //stop it!
//            Bukkit.getScheduler().cancelTask(taskId);
//            //set up rebuild
//            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new BlockRebuilder(blocks), 50L);
//        }
//    }
//
//    //sort blocks by distance to player
//    private Map<Double, List<Location>> getSortedBlocks(List<BlockState> blocks, final Location loc) {
//        //measureLocations's y=player.getLocation().getY()+height
//        Location measureLocation = loc.clone().add(new Vector(0, height, 0));
//        //map where the blocks are stored
//        Map<Double, List<Location>> sortedBlocks = new HashMap<Double, List<Location>>();
//        double disance;
//
//        for (BlockState state : blocks) {
//            disance = Math.floor(state.getLocation().distance(measureLocation));
//            //sort blocks into the map
//            if (sortedBlocks.containsKey(disance)) {
//                sortedBlocks.get(disance).add(state.getLocation());
//            } else {
//                List<Location> locList = new ArrayList<Location>();
//                locList.add(state.getLocation());
//                sortedBlocks.put(disance, locList);
//            }
//        }
//        return sortedBlocks;
//    }
//
//    //gets all blocks in the area
//    private List<BlockState> getTotalBlocks(final Location loc) {
//        final List<BlockState> blocks = new ArrayList<BlockState>();
//        final World world = loc.getWorld();
//        final int lx = loc.getBlockX();
//        final int ly = loc.getBlockY();
//        final int lz = loc.getBlockZ();
//        BlockState bs;
//        Vector blockLocation;
//        final Vector measureVector = loc.toVector();
//
//        for (int y = 0; y <= height; y++) {
//            for (double x = -radius; x < radius; x++) {
//                for (double z = -radius; z < radius; z++) {
//                    blockLocation = new Vector(lx + x, ly, lz + z);
//                    if (measureVector.distance(blockLocation) < radius) {
//                        bs = world.getBlockAt((int) (lx + x), ly + y, (int) (lz + z)).getState();
//                        if (bs.getType() != Material.AIR) {
//                            blocks.add(bs);
//                        }
//                    }
//                }
//            }
//        }
//        return blocks;
//    }
//}