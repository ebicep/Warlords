package com.ebicep.warlords.game.flags;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.block.BlockFace.*;

public class FlagRenderer {

    private final FlagInfo info;
    private final List<Player> affectedPlayers = new ArrayList<>();
    private final List<Entity> renderedArmorStands = new ArrayList<>();
    private final List<Block> renderedBlocks = new ArrayList<>();
    private final List<Runnable> runningTasksCancel = new ArrayList<>();
    private int timer = 0;
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
     *
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
            info.getFlag()
                .getLocation()
                .getWorld()
                .playEffect(info.getFlag().getLocation().clone().add(0, offset, 0),
                        Effect.STEP_SOUND,
                        info.getTeam() == Team.RED ? Material.REDSTONE_BLOCK : Material.LAPIS_BLOCK
                );
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
            for (int i = 0; !block.isEmpty() && !(block.getState() instanceof org.bukkit.block.Banner) && i < 4; i++) {
                block = block.getRelative(0, 1, 0);
            }
            if (block.isEmpty() || block.getState() instanceof org.bukkit.block.Banner) {
                renderedBlocks.add(block);
                block.setType(info.getTeam().getColors().banner);
                org.bukkit.block.Banner banner = (org.bukkit.block.Banner) block.getState();
                banner.addPattern(new Pattern(DyeColor.BLACK, PatternType.SKULL));
                banner.addPattern(new Pattern(DyeColor.BLACK, PatternType.TRIANGLES_TOP));
                banner.update();
                Vector target = this.lastLocation.getLocation().getDirection();
                Vector toTest = new Vector(0, 0, 0);
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
                @NotNull BlockData newData = block.getState().getBlockData();
                if (newData instanceof Rotatable rotatable) {
                    rotatable.setRotation(dir);
                    block.setBlockData(newData);
                }
            }

            ArmorStand flag = Utils.spawnArmorStand(block.getLocation().add(.5, 0, .5), armorStand -> {
                armorStand.customName(Component.text(info.getTeam().getChatTag() + " FLAG",
                        info.getTeam().getTeamColor(),
                        TextDecoration.BOLD
                ));
                armorStand.setCustomNameVisible(true);
                armorStand.setMetadata("INFO", new FixedMetadataValue(plugin, info));
            });
            renderedArmorStands.add(flag);

            ArmorStand flagInteract = Utils.spawnArmorStand(block.getLocation().add(.5, -0.3, .5), armorStand -> {
                armorStand.customName(Component.text("LEFT-CLICK TO STEAL IT", NamedTextColor.WHITE, TextDecoration.BOLD));
                armorStand.setCustomNameVisible(true);
                armorStand.setMetadata("INFO", new FixedMetadataValue(plugin, info));
            });
            renderedArmorStands.add(flagInteract);

        } else if (this.lastLocation instanceof PlayerFlagLocation flag) {
            runningTasksCancel.add(flag.getPlayer().getSpeed().addSpeedModifier(flag.getPlayer(), "FLAG", -20, 0, true));
            Entity entity = ((PlayerFlagLocation) this.lastLocation).getPlayer().getEntity();
            if (entity instanceof Player player) {
                this.affectedPlayers.add(player);
                ItemStack item = new ItemStack(info.getTeam().getColors().banner);
                BannerMeta banner = (BannerMeta) item.getItemMeta();
                banner.addPattern(new Pattern(DyeColor.BLACK, PatternType.SKULL));
                banner.addPattern(new Pattern(DyeColor.BLACK, PatternType.TRIANGLES_TOP));
                item.setItemMeta(banner);
                player.getInventory().setHelmet(item);
                player.getInventory().setItem(6, new ItemBuilder(Material.BLACK_BANNER, 1).name(Component.text("Drop Flag", NamedTextColor.GREEN)).get());
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
            WarlordsEntity wp = Warlords.getPlayer(p);
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
