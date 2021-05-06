package com.ebicep.warlords.events;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.abilties.EarthenSpike;
import com.ebicep.warlords.classes.abilties.SeismicWave;
import com.ebicep.warlords.classes.abilties.Slam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.*;

public class WarlordsEvents implements Listener {


    public static List<String> entityList = new ArrayList<>();

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {

        if (event.getEntity() instanceof FallingBlock) {
            if (this.containsBlock(event.getEntity().getUniqueId())) {
                event.setCancelled(true);
                this.removeEntityBlock(event.getEntity().getUniqueId());
            } else {

            }
        }
    }

    public static void addEntityUUID(UUID id) {
        String uuid = id.toString();
        entityList.add(uuid);
    }

    public void removeEntityBlock(UUID id) {
        String uuid = id.toString();
        entityList.remove(uuid);
    }

    public boolean containsBlock(UUID id) {
        String uuid = id.toString();
        return entityList.contains(uuid);
    }

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.sendMessage(ChatColor.AQUA + "Welcome to the server retard");
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent e) {
        //TODO find other fix?
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player attacker = (Player) e.getDamager();
            Player victim = (Player) e.getEntity();
            WarlordsPlayer warlordsPlayerAttacker = Warlords.getPlayer(attacker);
            WarlordsPlayer warlordsPlayerVictim = Warlords.getPlayer(victim);

            if (attacker.getInventory().getHeldItemSlot() == 0 && warlordsPlayerAttacker.getHitCooldown() == 0) {
                victim.damage(0);
                warlordsPlayerAttacker.setHitCooldown(13);
                warlordsPlayerAttacker.subtractEnergy(warlordsPlayerAttacker.getSpec().getEnergyOnHit() * -1);
                warlordsPlayerVictim.addHealth(warlordsPlayerAttacker, "", -132, -179, 25, 200);
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Action action = e.getAction();
        Location location = player.getLocation();

        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
            //Slam test = new Slam(location);
            if (Warlords.hasPlayer(player) && (player.getInventory().getHeldItemSlot() == 0 || !Warlords.getPlayer(player).isHotKeyMode())) {
                Warlords.getPlayer(player).getSpec().onRightClick(player);
            }
            ItemStack itemHeld = player.getItemInHand();
            if (itemHeld.getType() == Material.GOLD_BARDING) {
                double distance = player.getLocation().getY() - player.getWorld().getHighestBlockYAt(player.getLocation());
                if (distance > 2) {
                    player.sendMessage(ChatColor.DARK_RED + "You cannot mount in the air");
                } else {
                    Horse horse = (Horse) player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);
                    horse.setTamed(true);
                    horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
                    horse.setOwner(player);
                    horse.setJumpStrength(0);
                    //TODO change speed
                    horse.setPassenger(player);
                    Warlords.getPlayer(player).setHorseCooldown(15);
                }
            } else if (itemHeld.getType() == Material.DIAMOND_AXE) {
                location.setY(player.getWorld().getHighestBlockYAt(location));

                FallingBlock block = player.getWorld().spawnFallingBlock(location.add(0, 0, 0), location.getWorld().getBlockAt((int) location.getX(), location.getWorld().getHighestBlockYAt(location) - 1, (int) location.getZ()).getType(), (byte) 0);
                block.setVelocity(new Vector(0, .1, 0));
                ArrayList<ArrayList<SeismicWave>> waveList = new ArrayList<>();
                //waveList.add(new SeismicWave(block));
                //Warlords.waveArrays.add(waveList);

                WarlordsEvents.addEntityUUID(block.getUniqueId());
            } else if (itemHeld.getType() == Material.DIAMOND_PICKAXE) {
                Slam slam = new Slam(player.getLocation());
            } else if (itemHeld.getType() == Material.WOOD_AXE) {
                ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
                stand.setHelmet(new ItemStack(Material.LONG_GRASS, 1, (short) 2));
                stand.setGravity(true);
                stand.setVisible(true);
                stand.setHeadPose(new EulerAngle(20,0,0));
            }
        } else if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {
            if (action == Action.LEFT_CLICK_AIR) {

            }
        }
    }

    private static boolean getLookingAt(Player player, Player player1) {
        Location eye = player.getEyeLocation();
        eye.setY(eye.getY() + .5);
        Vector toEntity = player1.getEyeLocation().toVector().subtract(eye.toVector());
        float dot = (float) toEntity.normalize().dot(eye.getDirection());

        return dot > 0.98D;
    }

    @EventHandler
    public static void onPlayerShift(EntityDismountEvent e) {
        Entity entity = e.getDismounted();
        if (entity instanceof Horse) {
            entity.remove();
        }
    }

    @EventHandler
    public void onArmorStandBreak(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        if (entity instanceof ArmorStand) {
            if (entity.getCustomName() != null && entity.getCustomName().contains("FLAG")) {
                entity.remove();
                Player player = (Player) e.getDamager();
                player.getWorld().getBlockAt(entity.getLocation()).setType(Material.AIR);
                ItemStack[] armor = new ItemStack[4];
                armor[0] = new ItemStack(player.getInventory().getArmorContents()[0]);
                armor[1] = new ItemStack(player.getInventory().getArmorContents()[1]);
                armor[2] = new ItemStack(player.getInventory().getArmorContents()[2]);
                armor[3] = new ItemStack(Material.BANNER);

                player.getInventory().setArmorContents(armor);
                player.sendMessage("" + Arrays.toString(armor));
                player.sendMessage("" + Arrays.toString(player.getInventory().getArmorContents()));
                player.sendMessage("" + player.getTargetBlock((Set<Material>) null, 1));
            }

        }
    }

    @EventHandler
    public void onPlayerDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (player.getInventory().getArmorContents()[3].getType() == Material.BANNER) {
                Location blueFlagLocation = player.getLocation();
                Block block = blueFlagLocation.getWorld().getBlockAt(blueFlagLocation);
                block.setType(Material.STANDING_BANNER);

                ArmorStand blueFlag = blueFlagLocation.getWorld().spawn(block.getLocation().add(.5, 0, .5), ArmorStand.class);
                blueFlag.setGravity(false);
                blueFlag.setCanPickupItems(false);
                blueFlag.setCustomName("BLU FLAG");
                blueFlag.setCustomNameVisible(true);
                blueFlag.setVisible(false);
            }
        }
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
    public void switchItemHeld(PlayerItemHeldEvent e) {
        int slot = e.getNewSlot();
        if (Warlords.getPlayer(e.getPlayer()).isHotKeyMode() && (slot == 1 || slot == 2 || slot == 3 || slot == 4)) {
            Warlords.getPlayer(e.getPlayer()).getSpec().onRightClickHotKey(e.getPlayer(), slot);
            e.setCancelled(true);
        }
    }
}
