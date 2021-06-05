package com.ebicep.warlords.events;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.maps.FlagManager;
import com.ebicep.warlords.maps.Game;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        //e.setJoinMessage(null);
        Player player = e.getPlayer();
        if (Warlords.game.getState() == Game.State.GAME) {
            //readds player
            //first to warlords players
            for (Player oldPlayer : Warlords.getPlayers().keySet()) {
                if (oldPlayer.getUniqueId().equals(player.getUniqueId())) {
                    Warlords.getPlayers().put(player, Warlords.getPlayer(oldPlayer));
                    Warlords.getPlayer(player).setPlayer(player);
                    Warlords.getPlayer(player).getScoreboard().refreshScoreboard(player);

                    Warlords.getPlayers().remove(oldPlayer);
                    //then to team players
                    if (Warlords.game.getTeamBlueProtected().contains(oldPlayer)) {
                        Warlords.game.getCachedTeamBlue().remove(oldPlayer);
                        Warlords.game.getCachedTeamBlue().add(player);
                    } else if (Warlords.game.getTeamRedProtected().contains(oldPlayer)) {
                        Warlords.game.getCachedTeamRed().remove(oldPlayer);
                        Warlords.game.getCachedTeamRed().add(player);
                    }
                    break;
                }
            }
        } else {
            player.sendMessage(ChatColor.GRAY + "Welcome " + ChatColor.RED + player.getPlayerListName() + ChatColor.GRAY + " to the Warlords comp games server.");
            player.sendMessage(" ");
            player.sendMessage(ChatColor.GRAY + "Developed by " + ChatColor.RED + "sumSmash " + ChatColor.GRAY + "&" + ChatColor.RED + " Plikie");
            player.sendMessage(" ");
            player.sendMessage(ChatColor.GRAY + "/class [ClASS] to choose your class!");
            player.sendMessage(" ");
            player.sendMessage(ChatColor.GRAY + "NOTE: We're still in beta, bugs and/or missing features are still present. Please report any bugs you might find.");
            player.sendMessage(" ");
            player.sendMessage(ChatColor.GRAY + "CURRENT MISSING FEATURES: ");
            player.sendMessage(ChatColor.RED + "- Weapon Skill boosts");
            player.sendMessage(ChatColor.RED + "- Revenant's Orbs of Life being hidden for the enemy team");
            player.sendMessage(ChatColor.RED + "- Being able to swap weapon/armor skins.");
            player.sendMessage(ChatColor.RED + "- Flag damage modifier currently does not carry over to a new flag holder.");
            player.sendMessage(ChatColor.RED + "- Thunderlord/Earthwarden's Totem does not have proc animations!");
        }

    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (Warlords.game.getState() == Game.State.GAME) {
            if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
                Player attacker = (Player) e.getDamager();
                Player victim = (Player) e.getEntity();
                WarlordsPlayer warlordsPlayerAttacker = Warlords.getPlayer(attacker);
                WarlordsPlayer warlordsPlayerVictim = Warlords.getPlayer(victim);
                if (!Warlords.game.onSameTeam(warlordsPlayerAttacker, warlordsPlayerVictim)) {
                    if (attacker.getInventory().getHeldItemSlot() == 0 && warlordsPlayerAttacker.getHitCooldown() == 0) {
                        attacker.playSound(victim.getLocation(), Sound.HURT_FLESH, 1, 1);
                        warlordsPlayerAttacker.setHitCooldown(12);
                        warlordsPlayerAttacker.subtractEnergy(warlordsPlayerAttacker.getSpec().getEnergyOnHit() * -1);
                        warlordsPlayerVictim.addHealth(warlordsPlayerAttacker, "", -132, -179, 25, 200);
                    }

                    if (warlordsPlayerVictim.getIceBarrier() != 0) {
                        if (warlordsPlayerAttacker.getIceBarrierSlowness() == 0) {
                            warlordsPlayerAttacker.setIceBarrierSlowness(2 * 20 - 10);
                        }
                    }
                }

            } else if (e.getEntity() instanceof Horse && e.getDamager() instanceof Player) {
                if (!Warlords.getPlayer((Player) e.getEntity().getPassenger()).getScoreboard().onSameTeam((Player) e.getDamager())) {
                    e.getEntity().remove();
                }
            }
        }
        e.setCancelled(true);
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
            if (player.getInventory().getHeldItemSlot() == 7 && itemHeld.getType() == Material.GOLD_BARDING && player.getVehicle() == null) {
                if (location.getWorld().getBlockAt((int) location.getX(), 2, (int) location.getZ()).getType() == Material.NETHERRACK) { //&& !Utils.tunnelUnder(e.getPlayer())) {
                    player.sendMessage(ChatColor.RED + "You cannot mount here!");
                } else {
                    double distance = player.getLocation().getY() - player.getWorld().getHighestBlockYAt(player.getLocation());
                    if (distance > 2) {
                        player.sendMessage(ChatColor.RED + "You cannot mount in the air");
                    } else if (!player.getMetadata(FlagManager.FLAG_DAMAGE_MULTIPLIER).isEmpty()) {
                        player.sendMessage(ChatColor.RED + "You cannot mount while holding the flag!");
                    } else {
                        player.playSound(player.getLocation(), "mountup", 1, 1);
                        Horse horse = (Horse) player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);
                        horse.setTamed(true);
                        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
                        horse.setOwner(player);
                        horse.setJumpStrength(0);
                        horse.setVariant(Horse.Variant.HORSE);
                        horse.setAdult();
                        ((EntityLiving) ((CraftEntity) horse).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(.308);
                        //((EntityLiving) ((CraftEntity) horse).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(1);
                        horse.setPassenger(player);
                        Warlords.getPlayer(player).setHorseCooldown(15);
                    }
                }
            } else if (itemHeld.getType() == Material.BONE) {
                player.getInventory().remove(Material.BONE);
                Warlords.getPlayer(player).addHealth(Warlords.getPlayer(player), "", -100000, -100000, -1, 100);
                Warlords.getPlayer(player).setUndyingArmyDead(false);
            } else if (itemHeld.getType() == Material.BANNER) {
                if (Warlords.getPlayer(player).getFlagCooldown() > 0) {
                    player.sendMessage("§cYou cannot drop the flag yet, please wait 5 seconds!");
                } else {
                    Warlords.game.getFlags().dropFlag(player);
                    Warlords.getPlayer(player).setFlagCooldown(5);
                }
            }


        } else if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {
            if (action == Action.LEFT_CLICK_AIR) {

            }
        }
    }


    @EventHandler
    public static void onPlayerDismount(EntityDismountEvent e) {
        Entity entity = e.getDismounted();
        if (entity instanceof Horse) {
            entity.remove();
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
        if (Warlords.game.getState() == Game.State.GAME) {
            if (Warlords.getPlayer(e.getPlayer()).isHotKeyMode() && (slot == 1 || slot == 2 || slot == 3 || slot == 4)) {
                Warlords.getPlayer(e.getPlayer()).getSpec().onRightClickHotKey(e.getPlayer(), slot);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onOpenInventory(InventoryOpenEvent e) {
        if (e.getInventory().getHolder().getInventory().getTitle().equals("Horse")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getPlayer().getVehicle() instanceof Horse) {
            Location location = e.getPlayer().getLocation();
            if (location.getWorld().getBlockAt((int) location.getX(), 2, (int) location.getZ()).getType() == Material.NETHERRACK) { // && !Utils.tunnelUnder(e.getPlayer())) {
                e.getPlayer().getVehicle().remove();
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            //HEIGHT - DAMAGE
            //PLAYER
            //9 - 160 - 6
            //15 - 400 - 12
            //30ish - 1040

            //HORSE
            //HEIGHT - DAMAGE
            //18 - 160
            //HEIGHT x 40 - 200
            int damage = (int) e.getDamage();
            if (Warlords.game.getState() == Game.State.GAME) {
                if (e.getEntity() instanceof Player) {
                    if (damage > 5) {
                        WarlordsPlayer warlordsPlayer = Warlords.getPlayer((Player) e.getEntity());
                        warlordsPlayer.addHealth(warlordsPlayer, "Fall", -((damage + 3) * 40 - 200), -((damage + 3) * 40 - 200), -1, 100);
                        warlordsPlayer.setRegenTimer(10);
                    }
                }
            }

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        e.getBlock().getDrops().clear();
        //e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (Warlords.hasPlayer(player)) {
            e.setCancelled(true);
            WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
            if (Warlords.game.isBlueTeam(player)) {
                for (Player bluePlayer : Warlords.game.getTeamBlueProtected()) {
                    bluePlayer.sendMessage(ChatColor.BLUE + "[BLU]" +
                            ChatColor.DARK_GRAY + "[" +
                            ChatColor.GOLD + warlordsPlayer.getSpec().getClassNameShort() +
                            ChatColor.DARK_GRAY + "][" + ChatColor.GOLD + "90" + ChatColor.DARK_GRAY + "] " +
                            ChatColor.AQUA + warlordsPlayer.getName() +
                            ChatColor.WHITE + ": " + e.getMessage()
                    );
                }
            } else if (Warlords.game.isRedTeam(player)) {
                for (Player redPlayer : Warlords.game.getTeamRedProtected()) {
                    redPlayer.sendMessage(ChatColor.RED + "[RED]" +
                            ChatColor.DARK_GRAY + "[" +
                            ChatColor.GOLD + warlordsPlayer.getSpec().getClassNameShort() +
                            ChatColor.DARK_GRAY + "][" + ChatColor.GOLD + "90" + ChatColor.DARK_GRAY + "] " +
                            ChatColor.AQUA + warlordsPlayer.getName() +
                            ChatColor.WHITE + ": " + e.getMessage()
                    );
                }
            }
        }
    }

    @EventHandler
    public void onPlayerVelocity(PlayerVelocityEvent event) {
        Player player = event.getPlayer();
        EntityDamageEvent lastDamage = player.getLastDamageCause();

        if ((!(lastDamage instanceof EntityDamageByEntityEvent))) {
            return;
        }

        if ((((EntityDamageByEntityEvent) lastDamage).getDamager() instanceof Player))
            event.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent change) {
        change.setCancelled(true);
    }

}
