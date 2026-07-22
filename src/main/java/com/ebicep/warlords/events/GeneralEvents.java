package com.ebicep.warlords.events;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.player.SpecPrestigeEvent;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.pve.Currencies;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.GameRules;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.inventory.*;
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
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.HashSet;
import java.util.Set;

public class GeneralEvents implements Listener {

    public static final Set<Entity> FALLING_BLOCK_ENTITIES = new HashSet<>();

    public static void addEntityUUID(Entity entity) {
        FALLING_BLOCK_ENTITIES.add(entity);
    }

    @EventHandler
    public void onSpecPrestige(SpecPrestigeEvent event) {
        Player player = org.bukkit.Bukkit.getPlayer(event.getUUID());
        if (player == null) {
            return;
        }

        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(event.getUUID());
        Currencies.PRESTIGE_ORB.addToPlayer(databasePlayer, 2L);
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

        player.sendMessage(Component.text("Prestige reward: ", NamedTextColor.GREEN)
                .append(Currencies.PRESTIGE_ORB.getCostColoredName(2L))
                .append(Component.text(" for reaching Prestige " + event.getPrestige() + " in " + event.getSpec().name + "!", NamedTextColor.GREEN)));
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
    public void onBlockSpread(BlockSpreadEvent e) {
        e.setCancelled(true);
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

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (Permissions.isAdmin(player) && event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof Sign sign) {
            player.openSign(sign, sign.getInteractableSideFor(player));
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        event.getWorld().setGameRule(GameRules.SHOW_ADVANCEMENT_MESSAGES, false);
        event.getWorld().setGameRule(GameRules.SPECTATORS_GENERATE_CHUNKS, false);
        event.getWorld().setGameRule(GameRules.MOB_GRIEFING, false);
        event.getWorld().setGameRule(GameRules.RANDOM_TICK_SPEED, 0);
        event.getWorld().setGameRule(GameRules.ADVANCE_TIME, false);
        event.getWorld().setTime(4000);
    }

}
