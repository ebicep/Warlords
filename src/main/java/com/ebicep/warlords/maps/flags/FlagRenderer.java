package com.ebicep.warlords.maps.flags;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.player.ArmorManager;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ItemBuilder;
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
                BlockFace dir;
                if (banner.getWorld().getBlockAt(block.getLocation().add(0, 0, -5)).getType() == Material.AIR) {
                    dir = BlockFace.NORTH;
                } else if (banner.getWorld().getBlockAt(block.getLocation().add(0, 0, 5)).getType() == Material.AIR) {
                    dir = BlockFace.SOUTH;
                } else if (banner.getWorld().getBlockAt(block.getLocation().add(-5, 0, 0)).getType() == Material.AIR) {
                    dir = BlockFace.WEST;
                } else if (banner.getWorld().getBlockAt(block.getLocation().add(5, 0, 0)).getType() == Material.AIR) {
                    dir = BlockFace.EAST;
                } else {
                    dir = BlockFace.SOUTH;
                }
                ((Banner) newData).setFacingDirection(dir);
                block.setData(newData.getData());
            }
            ArmorStand stand = this.lastLocation.getLocation().getWorld().spawn(block.getLocation().add(.5, 0, .5), ArmorStand.class);
            renderedArmorStands.add(stand);
            stand.setGravity(false);
            stand.setCanPickupItems(false);
            stand.setCustomName(info.getTeam() == Team.BLUE ? ChatColor.BLUE + "BLU FLAG" : ChatColor.RED + "RED FLAG");
            stand.setCustomNameVisible(true);
            stand.setMetadata("TEAM", new FixedMetadataValue(plugin, info.getTeam()));
            stand.setVisible(false);
        } else if (this.lastLocation instanceof PlayerFlagLocation) {
            PlayerFlagLocation flag = (PlayerFlagLocation) this.lastLocation;
            runningTasksCancel.add(flag.getPlayer().getSpeed().addSpeedModifier("FLAG", -20, 0));
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
