package com.ebicep.warlords.events;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.abilties.IceBarrier;
import com.ebicep.warlords.classes.abilties.Soulbinding;
import com.ebicep.warlords.classes.abilties.UndyingArmy;
import com.ebicep.warlords.classes.shaman.specs.spiritguard.Spiritguard;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.flags.GroundFlagLocation;
import com.ebicep.warlords.maps.flags.PlayerFlagLocation;
import com.ebicep.warlords.maps.flags.SpawnFlagLocation;
import com.ebicep.warlords.maps.flags.WaitingFlagLocation;
import com.ebicep.warlords.player.Cooldown;
import com.ebicep.warlords.player.CustomScoreboard;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ItemBuilder;
import com.ebicep.warlords.util.PacketUtils;
import com.ebicep.warlords.util.Utils;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import static com.ebicep.warlords.menu.GameMenu.*;

public class WarlordsEvents implements Listener {

    public static Set<UUID> entityList = new HashSet<>();

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock) {
            if (entityList.remove(event.getEntity().getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    public static void addEntityUUID(UUID id) {
        entityList.add(id);
    }

    public void removeEntityBlock(UUID id) {
        entityList.remove(id);
    }

    public boolean containsBlock(UUID id) {
        return entityList.contains(id);
    }

    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent e) {
        WarlordsPlayer player = Warlords.getPlayer(e.getPlayer());
        if (player != null) {
            if (!player.isDeath()) {
                player.updatePlayerReference(null);
            }
        }
    }

    public static void joinInteraction(Player player) {
        Location rejoinPoint = Warlords.getRejoinPoint(player.getUniqueId());
        boolean isSpawnWorld = Bukkit.getWorlds().get(0).getName().equals(rejoinPoint.getWorld().getName());
        boolean playerIsInWrongWorld = !player.getWorld().getName().equals(rejoinPoint.getWorld().getName());
        if (isSpawnWorld || playerIsInWrongWorld) {
            player.teleport(rejoinPoint);
        }
        if (playerIsInWrongWorld && isSpawnWorld) {
            player.sendMessage(ChatColor.RED + "The game you were previously playing is no longer running!");
        }
        if (playerIsInWrongWorld && !isSpawnWorld) {
            player.sendMessage(ChatColor.RED + "The game started without you, but we still love you enough and you were warped into the game");
        }
        if (isSpawnWorld) {
            player.setGameMode(GameMode.ADVENTURE);
            player.sendMessage(ChatColor.GRAY + "You are now on Warlords 2.0");
            player.sendMessage(" ");
            player.sendMessage(ChatColor.GRAY + "Developed by " + ChatColor.RED + "sumSmash " + ChatColor.GRAY + "&" + ChatColor.RED + " Plikie");
            player.sendMessage(" ");
            player.sendMessage(ChatColor.GRAY + "/hotkeymode to change your hotkey mode.");
            player.sendMessage(" ");
            player.sendMessage(ChatColor.GRAY + "Click the Nether Star or do /menu to open the selection menu.");
            player.sendMessage(" ");
            player.sendMessage(ChatColor.GRAY + "BUILD: " + ChatColor.RED + Warlords.VERSION);
            if (player.isOp()) {
                player.sendMessage(" ");
                player.sendMessage(ChatColor.GRAY + "Skip Timer: /wl timer skip/reset");
                player.sendMessage(ChatColor.GRAY + "Infinite Energy: /wl energy disable/enable");
                player.sendMessage(ChatColor.GRAY + "Endless Cooldown: /wl cooldown disable/enable");
                player.sendMessage(ChatColor.GRAY + "Disable Damage: /wl damage disable/enable");
                player.sendMessage(" ");
                player.sendMessage(ChatColor.GRAY + "Cooldown Mode: /wl cooldownmode disable/enable");
            }

            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
            player.getInventory().setItem(4, new ItemBuilder(Material.NETHER_STAR).name("§aSelection Menu").get());

            CustomScoreboard.giveMainLobbyScoreboard(player);
        }
        WarlordsPlayer p = Warlords.getPlayer(player);
        if (p != null) {
            player.teleport(p.getLocation());
            p.updatePlayerReference(player);
        }
    }

    @EventHandler
    public void onDismount(VehicleExitEvent evt) {
        evt.getVehicle().remove();
    }

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Warlords.databaseManager.loadPlayer(e.getPlayer());
            }
        }.runTaskAsynchronously(Warlords.getInstance());
        //e.setJoinMessage(null);
        Player player = e.getPlayer();
        joinInteraction(player);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if ((e.getEntity() instanceof Player || e.getEntity() instanceof Zombie) && e.getDamager() instanceof Player) {
            Player attacker = (Player) e.getDamager();
            WarlordsPlayer warlordsPlayerAttacker = Warlords.getPlayer(attacker);
            WarlordsPlayer warlordsPlayerVictim = Warlords.getPlayer(e.getEntity());
            if (warlordsPlayerAttacker != null && warlordsPlayerAttacker.isEnemyAlive(warlordsPlayerVictim)) {
                if (attacker.getInventory().getHeldItemSlot() == 0 && warlordsPlayerAttacker.getHitCooldown() == 0) {
                    warlordsPlayerAttacker.setHitCooldown(12);
                    warlordsPlayerAttacker.subtractEnergy(warlordsPlayerAttacker.getSpec().getEnergyOnHit() * -1);

                    if (warlordsPlayerAttacker.getSpec() instanceof Spiritguard && !warlordsPlayerAttacker.getCooldownManager().getCooldown(Soulbinding.class).isEmpty()) {
                        warlordsPlayerAttacker.getCooldownManager().getCooldown(Soulbinding.class).stream()
                                .map(Cooldown::getCooldownObject)
                                .map(Soulbinding.class::cast)
                                .forEach(soulbinding -> {
                                    if (soulbinding.hasBoundPlayer(warlordsPlayerVictim)) {
                                        soulbinding.getSoulBindedPlayers().stream()
                                                .filter(p -> p.getBoundPlayer() == warlordsPlayerVictim)
                                                .forEach(boundPlayer -> {
                                                    boundPlayer.setHitWithSoul(false);
                                                    boundPlayer.setHitWithLink(false);
                                                    boundPlayer.setTimeLeft(3);
                                                });
                                    } else {
                                        warlordsPlayerVictim.sendMessage(ChatColor.RED + "\u00AB " + ChatColor.GRAY + "You have been bound by " + warlordsPlayerAttacker.getName() + "'s " + ChatColor.LIGHT_PURPLE + "Soulbinding Weapon" + ChatColor.GRAY + "!");
                                        warlordsPlayerAttacker.sendMessage(ChatColor.GREEN + "\u00BB " + ChatColor.GRAY + "Your " + ChatColor.LIGHT_PURPLE + "Soulbinding Weapon " + ChatColor.GRAY + "has bound " + warlordsPlayerVictim.getName() + "!");
                                        soulbinding.getSoulBindedPlayers().add(new Soulbinding.SoulBoundPlayer(warlordsPlayerVictim, 3));
                                        for (Player player1 : warlordsPlayerVictim.getWorld().getPlayers()) {
                                            player1.playSound(warlordsPlayerVictim.getLocation(), "shaman.earthliving.activation", 2, 1);
                                        }
                                    }
                                });
                    }
                    warlordsPlayerVictim.addHealth(warlordsPlayerAttacker, "", -132, -179, 25, 200);

                    if (warlordsPlayerVictim.getEntity() instanceof Zombie) {
                        if (warlordsPlayerVictim.isDeath()) {
                            warlordsPlayerVictim.getEntity().setCustomName("");
                        } else {
                            String oldName = warlordsPlayerVictim.getEntity().getCustomName();
                            String newName = oldName.substring(0, oldName.lastIndexOf(" ") + 1) + ChatColor.RED + warlordsPlayerVictim.getHealth() + "❤";
                            warlordsPlayerVictim.getEntity().setCustomName(newName);
                        }
                    }
                }

                if (!warlordsPlayerVictim.getCooldownManager().getCooldown(IceBarrier.class).isEmpty()) {
                    warlordsPlayerAttacker.getSpeed().addSpeedModifier("Ice Barrier", -20, 2 * 20);
                }
            }
        /*} else if (e.getEntity() instanceof Horse && e.getDamager() instanceof Player) {
            if (!Warlords.game.onSameTeam((Player) e.getEntity().getPassenger(), (Player) e.getDamager())) {
                e.getEntity().remove();
            }
        }*/
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Action action = e.getAction();
        Location location = player.getLocation();
        WarlordsPlayer wp = Warlords.getPlayer(player);

        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
            ItemStack itemHeld = player.getItemInHand();
            if (wp != null) {
                if (player.getInventory().getHeldItemSlot() == 7 && itemHeld.getType() == Material.GOLD_BARDING && player.getVehicle() == null) {
                    if (!Utils.isMountableZone(location)) {
                        player.sendMessage(ChatColor.RED + "You can't mount here!");
                    } else {
                        double distance = player.getLocation().getY() - player.getWorld().getHighestBlockYAt(player.getLocation());
                        if (distance > 2) {
                            player.sendMessage(ChatColor.RED + "You can't mount in the air");
                        } else if (wp.getFlagDamageMultipler() > 0) {
                            player.sendMessage(ChatColor.RED + "You can't mount while holding the flag!");
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
                            wp.setHorseCooldown(15);
                        }
                    }

                } else if (itemHeld.getType() == Material.BONE) {
                    player.getInventory().remove(UndyingArmy.BONE);
                    wp.addHealth(Warlords.getPlayer(player), "", -100000, -100000, -1, 100);
                } else if (itemHeld.getType() == Material.BANNER) {
                    if (wp.getFlagCooldown() > 0) {
                        player.sendMessage("§cYou cannot drop the flag yet, please wait 5 seconds!");
                    } else {
                        wp.getGameState().flags().dropFlag(player);
                        wp.setFlagCooldown(5);
                    }
                } else if (itemHeld.getType() == Material.FIREWORK_CHARGE) {
                    openSkillTreeMenu(player);
                } else if (itemHeld.getType() == Material.COMPASS) {
                    wp.toggleTeamFlagCompass();
                } else if (player.getInventory().getHeldItemSlot() == 0 || !wp.isHotKeyMode()) {
                    wp.getSpec().onRightClick(wp, player);
                }
            } else {
                if (itemHeld.getType() == Material.NETHER_STAR) {
                    //menu
                    openMainMenu(player);
                } else if (itemHeld.getType() == Material.NOTE_BLOCK) {
                    //team selector
                    openTeamMenu(player);
                }
            }
        } else if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {
            if (action == Action.LEFT_CLICK_AIR) {

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
        WarlordsPlayer wp = Warlords.getPlayer(e.getPlayer());
        if (wp != null) {
            if (wp.isHotKeyMode() && (slot == 1 || slot == 2 || slot == 3 || slot == 4)) {
                wp.getSpec().onRightClickHotKey(wp, e.getPlayer(), slot);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (e.getSlot() == 0) {
            Player player = (Player) e.getWhoClicked();
            WarlordsPlayer wp = Warlords.getPlayer(player);
            if (wp != null) {
                if (e.isLeftClick()) {
                    wp.weaponLeftClick(player);
                } else if (e.isRightClick()) {
                    wp.weaponRightClick(player);
                }
            }
        }
        if (e.getWhoClicked().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onOpenInventory(InventoryOpenEvent e) {
        if (e.getPlayer().getVehicle() != null) {
            if (e.getInventory().getHolder().getInventory().getTitle().equals("Horse")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropEvent(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getPlayer().getVehicle() instanceof Horse) {
            Location location = e.getPlayer().getLocation();
            if (!Utils.isMountableZone(location)) {
                e.getPlayer().getVehicle().remove();
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player || e.getEntity() instanceof Horse) {
            if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                e.getEntity().teleport(Warlords.getRejoinPoint(e.getEntity().getUniqueId()));
                WarlordsPlayer wp = Warlords.getPlayer(e.getEntity());
                if (wp != null) {
                    wp.addHealth(wp, "Fall", -1000000, -1000000, -1, 100);
                }
                e.setCancelled(true);
            } else if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                //HEIGHT - DAMAGE
                //PLAYER
                //9 - 160 - 6
                //15 - 400 - 12
                //30ish - 1040

                //HORSE
                //HEIGHT - DAMAGE
                //18 - 160
                //HEIGHT x 40 - 200
                if (e.getEntity() instanceof Player) {
                    WarlordsPlayer wp = Warlords.getPlayer(e.getEntity());
                    if (wp != null) {
                        int damage = (int) e.getDamage();
                        if (damage > 5) {
                            wp.addHealth(wp, "Fall", -((damage + 3) * 40 - 200), -((damage + 3) * 40 - 200), -1, 100);
                            wp.setRegenTimer(10);
                        }
                    }
                }

                e.setCancelled(true);
            } else if (e.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        e.getDrops().clear();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        e.getBlock().getDrops().clear();
        //e.setCancelled(true);
    }

    @EventHandler
            (priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        try {
            // We need to do this in a callSyncMethod, because we need it to happen in the main thread. or else weird bugs can happen in other threads
            Bukkit.getScheduler().callSyncMethod(Warlords.getInstance(), () -> {
                WarlordsPlayer wp = Warlords.getPlayer(player);
                if (wp == null) {
                    return null;
                }
                e.setFormat(
                        ChatColor.DARK_GRAY + "[" +
                                wp.getTeam().coloredPrefix() +
                                ChatColor.DARK_GRAY + "][" +
                                ChatColor.GOLD + wp.getSpec().getClassNameShort() +
                                ChatColor.DARK_GRAY + "][" +
                                ChatColor.GOLD + "90" +
                                ChatColor.DARK_GRAY + "] " +
                                ChatColor.AQUA + "%1$s" +
                                ChatColor.WHITE + ": %2$s"
                );
                e.getRecipients().removeIf(p -> wp.getGame().getPlayerTeamOrNull(p.getUniqueId()) != wp.getTeam());
                return null;
            }).get();
        } catch (InterruptedException | ExecutionException ex) {
            Warlords.getInstance().getLogger().log(Level.SEVERE, null, ex);
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

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFlagChange(WarlordsFlagUpdatedEvent event) {
        //Bukkit.broadcastMessage(event.getTeam() + " " + event.getOld().getClass().getSimpleName() + " => " + event.getNew().getClass().getSimpleName());
        if (event.getOld() instanceof PlayerFlagLocation) {
            ((PlayerFlagLocation) event.getOld()).getPlayer().setFlagDamageMultipler(0);
        }

        if (event.getNew() instanceof PlayerFlagLocation) {
            PlayerFlagLocation pfl = (PlayerFlagLocation) event.getNew();
            WarlordsPlayer player = pfl.getPlayer();
            player.setFlagDamageMultipler(pfl.getComputedMultiplier());
            if (!(event.getOld() instanceof PlayerFlagLocation)) {
                // eg GROUND -> PLAYER
                // or SPAWN -> PLAYER
                ChatColor enemyColor = event.getTeam().enemy().teamColor();
                event.getGame().forEachOnlinePlayer((p, t) -> {
                    p.sendMessage(enemyColor + player.getName() + " §ehas picked up the " + event.getTeam().coloredPrefix() + " §eflag!");
                    PacketUtils.sendTitle(p, "", enemyColor + player.getName() + " §ehas picked up the " + event.getTeam().coloredPrefix() + " §eflag!", 0, 60, 0);
                    if (t == event.getTeam()) {
                        p.playSound(player.getLocation(), "ctf.friendlyflagtaken", 500, 1);
                    } else {
                        p.playSound(player.getLocation(), "ctf.enemyflagtaken", 500, 1);
                    }
                });
            } else {
                // PLAYER -> PLAYER only happens if the multiplier gets to a new scale
                if (pfl.getComputedHumanMultiplier() % 10 == 0) {
                    event.getGame().forEachOnlinePlayer((p, t) -> {
                        p.sendMessage("§eThe " + event.getTeam().coloredPrefix() + " §eflag carrier now takes §c" + pfl.getComputedHumanMultiplier() + "% §eincreased damage!");
                    });
                }
            }
        } else if (event.getNew() instanceof SpawnFlagLocation) {
            String toucher = ((SpawnFlagLocation) event.getNew()).getLastToucher();
            if (event.getOld() instanceof GroundFlagLocation) {
                if (toucher != null) {
                    event.getGame().forEachOnlinePlayer((p, t) -> {
                        ChatColor color = event.getTeam().teamColor();
                        p.sendMessage(color + toucher + " §ehas returned the " + event.getTeam().coloredPrefix() + " §eflag!");
                        PacketUtils.sendTitle(p, "", color + toucher + " §ehas returned the " + event.getTeam().coloredPrefix() + " §eflag!", 0, 60, 0);
                        p.playSound(event.getNew().getLocation(), "ctf.flagreturned", 500, 1);
                    });
                } else {
                    event.getGame().forEachOnlinePlayer((p, t) -> {
                        p.sendMessage("§eThe " + event.getTeam().coloredPrefix() + " §eflag has returned to its base.");
                    });
                }
            }
        } else if (event.getNew() instanceof GroundFlagLocation) {
            if (event.getOld() instanceof PlayerFlagLocation) {
                PlayerFlagLocation pfl = (PlayerFlagLocation) event.getOld();
                String flag = event.getTeam().coloredPrefix();
                ChatColor playerColor = event.getTeam().enemy().teamColor();
                event.getGame().forEachOnlinePlayer((p, t) -> {
                    PacketUtils.sendTitle(p, "", playerColor + pfl.getPlayer().getName() + " §ehas dropped the " + flag + " §eflag!", 0, 60, 0);
                    p.sendMessage(playerColor + pfl.getPlayer().getName() + " §ehas dropped the " + flag + " §eflag!");
                });
            }
        } else if (event.getNew() instanceof WaitingFlagLocation && ((WaitingFlagLocation) event.getNew()).wasWinner()) {
            if (event.getOld() instanceof PlayerFlagLocation) {
                PlayerFlagLocation pfl = (PlayerFlagLocation) event.getOld();
                Team loser = event.getTeam();
                event.getGameState().addCapture(pfl.getPlayer());
                event.getGame().forEachOnlinePlayer((p, t) -> {
                    String message = pfl.getPlayer().getColoredName() + " §ehas captured the " + loser.coloredPrefix() + " §eflag!";
                    p.sendMessage(message);
                    PacketUtils.sendTitle(p, "", message, 0, 60, 0);

                    if (event.getTeam() == t) {
                        p.playSound(pfl.getLocation(), "ctf.enemycapturedtheflag", 500, 1);
                    } else {
                        p.playSound(pfl.getLocation(), "ctf.enemyflagcaptured", 500, 1);
                    }
                });
            }
        }
    }
}
