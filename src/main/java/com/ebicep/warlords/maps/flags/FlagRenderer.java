package com.ebicep.warlords.maps.flags;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.player.ArmorManager;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ItemBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.material.Banner;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

import static org.bukkit.block.BlockFace.*;

import java.util.ArrayList;
import java.util.List;

class FlagRenderer {
	
    private final FlagInfo info;
    private int timer = 0;
    private final List<Player> affectedPlayers = new ArrayList<>();
    private final List<Entity> renderedArmorStands = new ArrayList<>();
    private final List<Block> renderedBlocks = new ArrayList<>();
    private final List<Runnable> runningTasksCancel = new ArrayList<>();
    private FlagLocation lastLocation;

    public FlagRenderer(FlagInfo info) {
        this.info = info;
    }

    public FlagInfo getInfo() {
        return info;
    }

    public List<Entity> getRenderedArmorStands() {
        return renderedArmorStands;
    }

    /**
     * Returns the last state rendered, could be null
     * @return
     */
    @Nullable
    public FlagLocation getLastFlagState() {
        return lastLocation;
    }

    public void checkRender() {
        if (this.lastLocation != info.getFlag()) {
            this.render();
        }
        if (timer <= 0 && !(info.getFlag() instanceof WaitingFlagLocation)) {
            timer = 20;
            float offset = info.getFlag() instanceof PlayerFlagLocation ? 1.5F : 0.5F;
            info.getFlag().getLocation().getWorld().playEffect(info.getFlag().getLocation().clone().add(0, offset, 0), Effect.STEP_SOUND, info.getTeam() == Team.RED ? Material.REDSTONE_BLOCK.getId() : Material.LAPIS_BLOCK.getId());
        }
        timer--;
    }

    public void render() {
        if (this.lastLocation != null) {
            this.reset();
        }
        this.lastLocation = info.getFlag();
        final Warlords plugin = Warlords.getInstance();
        if (this.lastLocation instanceof GroundFlagLocation || this.lastLocation instanceof SpawnFlagLocation) {
            Block block = this.lastLocation.getLocation().getBlock();
            for (int i = 0; !block.isEmpty() && block.getType() != Material.STANDING_BANNER && i < 4; i++) {
                block = block.getRelative(0, 1, 0);
            }
            if (block.isEmpty() || block.getType() == Material.STANDING_BANNER) {
                renderedBlocks.add(block);
                block.setType(Material.STANDING_BANNER);
                org.bukkit.block.Banner banner = (org.bukkit.block.Banner) block.getState();
                banner.setBaseColor(info.getTeam() == Team.BLUE ? DyeColor.BLUE : DyeColor.RED);
                banner.addPattern(new Pattern(DyeColor.BLACK, PatternType.SKULL));
                banner.addPattern(new Pattern(DyeColor.BLACK, PatternType.TRIANGLES_TOP));
                banner.update();
                MaterialData newData = block.getState().getData();
                Vector target = this.lastLocation.getLocation().getDirection();
                Vector toTest = new Vector(0,0,0);
                BlockFace dir = SOUTH;
                double distance = Double.MAX_VALUE;
                for (BlockFace face : new BlockFace[]{
                        SOUTH,
                        SOUTH_SOUTH_WEST,
                        SOUTH_WEST,
                        WEST_SOUTH_WEST,
                        WEST,
                        WEST_NORTH_WEST,
                        NORTH_WEST,
                        NORTH_NORTH_WEST,
                        NORTH,
                        NORTH_NORTH_EAST,
                        NORTH_EAST,
                        EAST_NORTH_EAST,
                        EAST,
                        EAST_SOUTH_EAST,
                        SOUTH_SOUTH_EAST,
                        SOUTH_EAST,
                }) {
                    toTest.setX(face.getModX());
                    toTest.setZ(face.getModZ());
                    toTest.normalize();
                    double newDistance = toTest.distanceSquared(target);
                    if (newDistance < distance) {
                        dir = face;
                        distance = newDistance;
                    }
                }
                ((Banner) newData).setFacingDirection(dir);
                block.setData(newData.getData());
            }

            spawnArmorStand(
                    block.getLocation().add(.5, 0, .5),
                    info.getTeam().boldColoredPrefix() + " FLAG",
                    info.getTeam()
            );
            spawnArmorStand(
                    block.getLocation().add(.5, -0.3, .5),
                    ChatColor.WHITE + "" + ChatColor.BOLD + "LEFT-CLICK TO STEAL IT" + info.getTeam().teamColor(),
                    info.getTeam()
            );

        } else if (this.lastLocation instanceof PlayerFlagLocation) {
            PlayerFlagLocation flag = (PlayerFlagLocation) this.lastLocation;
            runningTasksCancel.add(flag.getPlayer().getSpeed().addSpeedModifier("FLAG", -20, 0, true));
            LivingEntity entity = ((PlayerFlagLocation) this.lastLocation).getPlayer().getEntity();
            if (entity instanceof Player) {
                Player player = (Player)entity;
                this.affectedPlayers.add(player);
                ItemStack item = new ItemStack(Material.BANNER);
                BannerMeta banner = (BannerMeta) item.getItemMeta();
                banner.setBaseColor(info.getTeam() == Team.BLUE ? DyeColor.BLUE : DyeColor.RED);
                banner.addPattern(new Pattern(DyeColor.BLACK, PatternType.SKULL));
                banner.addPattern(new Pattern(DyeColor.BLACK, PatternType.TRIANGLES_TOP));
                item.setItemMeta(banner);
                player.getInventory().setHelmet(item);
                player.getInventory().setItem(6, new ItemBuilder(Material.BANNER, 1).name("§aDrop Flag").get());
            }
        }
    }

    private void spawnArmorStand(Location loc, String name, Team team) {
        boolean hasOldFlag = false;
        for (Entity entity : this.lastLocation.getLocation().getWorld().getEntities()) {
            if (entity.getLocation().distanceSquared(loc) < 0.25 && entity instanceof ArmorStand && entity.getCustomName().equals(name)) {
                hasOldFlag = true;
                renderedArmorStands.add(entity);
                ((ArmorStand) entity).setGravity(false);
                ((ArmorStand) entity).setCanPickupItems(false);
                entity.setCustomName(name);
                entity.setCustomNameVisible(true);
                entity.removeMetadata("TEAM", Warlords.getInstance());
                entity.setMetadata("TEAM", new FixedMetadataValue(Warlords.getInstance(), info.getTeam()));
            }
        }
        if (!hasOldFlag) {
            ArmorStand stand = this.lastLocation.getLocation().getWorld().spawn(loc, ArmorStand.class);
            renderedArmorStands.add(stand);
            stand.setGravity(false);
            stand.setCanPickupItems(false);
            stand.setCustomName(name);
            stand.setCustomNameVisible(true);
            stand.setMetadata("TEAM", new FixedMetadataValue(Warlords.getInstance(), info.getTeam()));
            stand.setVisible(false);
        }
    }

    public void reset() {
        this.lastLocation = null;
        for (Block b : renderedBlocks) {
            b.setType(Material.AIR);
        }
        renderedBlocks.clear();
        for (Entity e : renderedArmorStands) {
            e.remove();
        }
        renderedArmorStands.clear();
        for (Player p : affectedPlayers) {
            WarlordsPlayer wp = Warlords.getPlayer(p);
            if (wp != null) {
                ArmorManager.resetArmor(p, wp.getSpecClass(), wp.getTeam());
            }
            p.getInventory().setItem(6, null);
        }
        affectedPlayers.clear();
        for (Runnable t : runningTasksCancel) {
            t.run();
        }
        runningTasksCancel.clear();
    }
}
