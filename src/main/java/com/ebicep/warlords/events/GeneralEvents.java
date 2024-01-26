package com.ebicep.warlords.events;

import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_20_R2.inventory.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.HashSet;
import java.util.Set;

public class GeneralEvents implements Listener {

    public static final Set<Entity> FALLING_BLOCK_ENTITIES = new HashSet<>();

    public static void addEntityUUID(Entity entity) {
        FALLING_BLOCK_ENTITIES.add(entity);
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock) {
            if (GeneralEvents.FALLING_BLOCK_ENTITIES.remove(event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void regenEvent(EntityRegainHealthEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void pickUpItem(PlayerArmorStandManipulateEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        e.getDrops().clear();
    }

    @EventHandler
    public void onDismount(VehicleExitEvent e) {
        e.getVehicle().remove();
    }

    @EventHandler
    public void onOpenInventory(InventoryOpenEvent e) {
        if (e.getPlayer().getVehicle() != null) {
            if (e.getInventory().getHolder() != null && e.getInventory().getHolder() instanceof Horse) {
                e.setCancelled(true);
            }
        }

        if (e.getInventory() instanceof CraftInventoryAnvil ||
                e.getInventory() instanceof CraftInventoryBeacon ||
                e.getInventory() instanceof CraftInventoryBrewer ||
                e.getInventory() instanceof CraftInventoryCrafting ||
                e.getInventory() instanceof CraftInventoryDoubleChest ||
                e.getInventory() instanceof CraftInventoryFurnace ||
                e.getInventory().getType() == InventoryType.HOPPER ||
                e.getInventory().getType() == InventoryType.DROPPER
        ) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        e.getBlock().getDrops().clear();
        //e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onLeaveDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(event.toWeatherState());
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent change) {
        change.setCancelled(true);
        if (change.getEntity() instanceof Player) {
            change.getEntity().setFoodLevel(20);
        }
    }

    @EventHandler
    public void onPlayerDropEvent(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onSwapHandItems(PlayerSwapHandItemsEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (e.getWhoClicked().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        // prevent wolf eating item
        if (e.getRightClicked() instanceof Wolf) {
            e.setCancelled(true);
        }
    }

}
