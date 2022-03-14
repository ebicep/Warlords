package com.ebicep.warlords.game.flags;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Material;
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
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.block.BlockFace.*;

public class FlagRenderer {
	
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
        Warlords plugin = Warlords.getInstance();
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

            ArmorStand stand = this.lastLocation.getLocation().getWorld().spawn(block.getLocation().add(.5, 0, .5), ArmorStand.class);
            renderedArmorStands.add(stand);
            stand.setGravity(false);
            stand.setCanPickupItems(false);
            stand.setCustomName(info.getTeam() == Team.BLUE ? ChatColor.BLUE + "" + ChatColor.BOLD + "BLU FLAG" : ChatColor.RED + "" + ChatColor.BOLD + "RED FLAG");
            stand.setCustomNameVisible(true);
            stand.setMetadata("INFO", new FixedMetadataValue(plugin, info));
            stand.setVisible(false);

            ArmorStand stand1 = this.lastLocation.getLocation().getWorld().spawn(block.getLocation().add(.5, -0.3, .5), ArmorStand.class);
            renderedArmorStands.add(stand1);
            stand1.setGravity(false);
            stand1.setCanPickupItems(false);
            stand1.setCustomName(ChatColor.WHITE + "" + ChatColor.BOLD + "LEFT-CLICK TO STEAL IT");
            stand1.setCustomNameVisible(true);
            stand.setMetadata("INFO", new FixedMetadataValue(plugin, info));
            stand1.setVisible(false);

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

    public void reset() {
        this.lastLocation = null;
        for (Block b : renderedBlocks) {
            b.setType(Material.AIR);
        }
        renderedBlocks.clear();
        for (Entity e : renderedArmorStands) {
            e.removeMetadata("INFO", Warlords.getInstance());
            e.remove();
        }
        renderedArmorStands.clear();
        for (Player p : affectedPlayers) {
            WarlordsPlayer wp = Warlords.getPlayer(p);
            if (wp != null) {
                wp.updateArmor();
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
