package com.ebicep.warlords.events;

import com.ebicep.warlords.ChatChannels;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.classes.abilties.IceBarrier;
import com.ebicep.warlords.classes.abilties.Soulbinding;
import com.ebicep.warlords.classes.abilties.UndyingArmy;
import com.ebicep.warlords.classes.shaman.specs.spiritguard.Spiritguard;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.LeaderboardManager;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGame;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.flags.GroundFlagLocation;
import com.ebicep.warlords.maps.flags.PlayerFlagLocation;
import com.ebicep.warlords.maps.flags.SpawnFlagLocation;
import com.ebicep.warlords.maps.flags.WaitingFlagLocation;
import com.ebicep.warlords.maps.state.EndState;
import com.ebicep.warlords.permissions.PermissionHandler;
import com.ebicep.warlords.player.*;
import com.ebicep.warlords.util.ChatUtils;
import com.ebicep.warlords.util.ItemBuilder;
import com.ebicep.warlords.util.PacketUtils;
import com.ebicep.warlords.util.Utils;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.inventory.*;
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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import static com.ebicep.warlords.menu.GameMenu.openMainMenu;
import static com.ebicep.warlords.menu.GameMenu.openTeamMenu;

public class WarlordsEvents implements Listener {

    public static Set<Entity> entityList = new HashSet<>();

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock) {
            if (entityList.remove(event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }

    public static void addEntityUUID(Entity entity) {
        entityList.add(entity);
    }

    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent e) {
        WarlordsPlayer wp = Warlords.getPlayer(e.getPlayer());
        if (wp != null) {
            if (!wp.isDeath()) {
                wp.updatePlayerReference(null);
            }
            e.setQuitMessage(wp.getColoredNameBold() + ChatColor.GOLD + " left the game!");
            new BukkitRunnable() {
                int secondsGone = 0;
                boolean froze = false;

                @Override
                public void run() {
                    secondsGone++;
                    if (e.getPlayer().isOnline()) {
                        if (froze && wp.getGame().isGameFreeze()) {
                            //to make sure no other is disconnected
                            boolean allOnline = true;
                            for (UUID uuid : wp.getGame().getPlayers().keySet()) {
                                if (Bukkit.getPlayer(uuid) == null) {
                                    allOnline = false;
                                    break;
                                }
                            }
                            if (allOnline) {
                                wp.getGame().freeze(true);
                            }
                        }
                        this.cancel();
                        // 15 for precaution
                    } else if (secondsGone >= 15 && !froze) {
                        if (!wp.getGame().isGameFreeze()) {
                            wp.getGame().freeze(true);
                        }
                        froze = true;
                    }
                }
            }.runTaskTimer(Warlords.getInstance(), 1, 20);
        } else {
            e.setQuitMessage(ChatColor.AQUA + e.getPlayer().getName() + ChatColor.GOLD + " left the lobby!");
        }
        if (e.getPlayer().getVehicle() != null) {
            e.getPlayer().getVehicle().remove();
        }
        //removing player position boards
        LeaderboardManager.removePlayerSpecificHolograms(e.getPlayer());

        Bukkit.getOnlinePlayers().forEach(p -> {
            PacketUtils.sendTabHF(p, ChatColor.AQUA + "     Welcome to " + ChatColor.YELLOW + ChatColor.BOLD + "Warlords 2.0     ", ChatColor.GREEN + "Players Online: " + ChatColor.GRAY + (Bukkit.getOnlinePlayers().size() - 1));
        });
    }

    public static void joinInteraction(Player player, boolean fromGame) {
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

            ChatUtils.sendCenteredMessage(player, ChatColor.BLUE + "-----------------------------------------------------");
            ChatUtils.sendCenteredMessage(player, ChatColor.GOLD + "You are now on Warlords 2.0 " + ChatColor.GRAY + "(" + ChatColor.RED + Warlords.VERSION + ChatColor.GRAY + ")");
            ChatUtils.sendCenteredMessage(player, ChatColor.GOLD + "Developed by " + ChatColor.RED + "sumSmash " + ChatColor.GOLD + "&" + ChatColor.RED + " Plikie");
            ChatUtils.sendCenteredMessage(player, ChatColor.GREEN + "/hotkeymode " + ChatColor.GOLD + "to change your hotkey mode.");
            ChatUtils.sendCenteredMessage(player, ChatColor.GOLD + "Click the Nether Star or do " + ChatColor.GREEN + "/menu" + ChatColor.GOLD + " to open the selection menu.");
            ChatUtils.sendCenteredMessage(player, ChatColor.GOLD + "Make sure to join the queue using " + ChatColor.GREEN + "/queue join" + ChatColor.GOLD + " if you'd like to play!");
            ChatUtils.sendCenteredMessage(player, ChatColor.BLUE + "-----------------------------------------------------");

            PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
            Classes selectedClass = playerSettings.getSelectedClass();
            AbstractPlayerClass apc = selectedClass.create.get();

            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
            player.getInventory().setItem(4, new ItemBuilder(Material.NETHER_STAR).name("§aSelection Menu").get());
            player.getInventory().setItem(1, new ItemBuilder(apc.getWeapon().getItem(playerSettings.getWeaponSkins()
                    .getOrDefault(selectedClass, Weapons.FELFLAME_BLADE).item)).name("§aWeapon Skin Preview")
                    .lore("")
                    .get());
            if (player.hasPermission("warlords.game.debug")) {
                player.getInventory().setItem(3, new ItemBuilder(Material.EMERALD).name("§aDebug Menu").get());
            }

            if (fromGame) {
                Warlords.playerScoreboards.get(player.getUniqueId()).giveMainLobbyScoreboard();
                ExperienceManager.giveExperienceBar(player);
            }
        }
        WarlordsPlayer p = Warlords.getPlayer(player);
        if (p != null) {
            player.teleport(p.getLocation());
            p.updatePlayerReference(player);
        } else {
            player.setAllowFlight(true);
        }
    }

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent e) {
        WarlordsPlayer wp = Warlords.getPlayer(e.getPlayer());
        if (wp != null) {
            if (wp.isAlive()) {
                e.getPlayer().setAllowFlight(false);
            }
            e.setJoinMessage(wp.getColoredNameBold() + ChatColor.GOLD + " rejoined the game!");
            if (wp.getGame().isGameFreeze()) {
                wp.getGame().freezePlayer(e.getPlayer(), false);
            }
        } else {
            e.getPlayer().setAllowFlight(true);
            e.setJoinMessage(ChatColor.AQUA + e.getPlayer().getName() + ChatColor.GOLD + " joined the lobby!");
        }
        Player player = e.getPlayer();

        Warlords.newChain()
                .async(() -> {
                    DatabaseManager.loadPlayer(e.getPlayer().getUniqueId(), PlayersCollections.ALL_TIME);
                    Warlords.updateHead(e.getPlayer());
                }).sync(() -> {
                    LeaderboardManager.setLeaderboardHologramVisibility(player);
                    DatabaseGame.setGameHologramVisibility(player);

                    Location rejoinPoint = Warlords.getRejoinPoint(player.getUniqueId());
                    if (Bukkit.getWorlds().get(0).getName().equals(rejoinPoint.getWorld().getName())) {
                        Warlords.playerScoreboards.get(player.getUniqueId()).giveMainLobbyScoreboard();
                        ExperienceManager.giveExperienceBar(player);
                    }
                })
                .execute();

        //scoreboard
        if (!Warlords.playerScoreboards.containsKey(player.getUniqueId()) || Warlords.playerScoreboards.get(player.getUniqueId()) == null) {
            Warlords.playerScoreboards.put(player.getUniqueId(), new CustomScoreboard(player));
        }
        player.setScoreboard(Warlords.playerScoreboards.get(player.getUniqueId()).getScoreboard());

        joinInteraction(player, false);

        Bukkit.getOnlinePlayers().forEach(p -> {
            PacketUtils.sendTabHF(p,
                    ChatColor.AQUA + "     Welcome to " + ChatColor.YELLOW + ChatColor.BOLD + "Warlords 2.0     ",
                    ChatColor.GREEN + "Players Online: " + ChatColor.GRAY + Bukkit.getOnlinePlayers().size());
        });

        //hiding players that arent in the game
        if (!Warlords.hasPlayer(player)) {
            Warlords.getPlayers().forEach(((uuid, warlordsPlayer) -> {
                if (warlordsPlayer.getEntity() instanceof Player) {
                    ((Player) warlordsPlayer.getEntity()).hidePlayer(player);
                }
            }));
        } else {
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (!Warlords.hasPlayer(p)) {
                    player.hidePlayer(p);
                }
            });
        }

    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if ((e.getEntity() instanceof Player || e.getEntity() instanceof Zombie) && e.getDamager() instanceof Player) {
            Player attacker = (Player) e.getDamager();
            WarlordsPlayer warlordsPlayerAttacker = Warlords.getPlayer(attacker);
            WarlordsPlayer warlordsPlayerVictim = Warlords.getPlayer(e.getEntity());
            if (warlordsPlayerAttacker != null && warlordsPlayerAttacker.isEnemyAlive(warlordsPlayerVictim) && !warlordsPlayerAttacker.getGame().isGameFreeze()) {
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
                                            player1.playSound(warlordsPlayerVictim.getLocation(), "shaman.earthlivingweapon.activation", 2, 1);
                                        }
                                    }
                                });
                    }
                    warlordsPlayerVictim.damageHealth(warlordsPlayerAttacker, "", 132, 179, 25, 200, false);
                    warlordsPlayerVictim.updateJimmyHealth();
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
            if (wp != null && wp.isAlive() && !wp.getGame().isGameFreeze()) {
                if (player.getInventory().getHeldItemSlot() == 7 && itemHeld.getType() == Material.GOLD_BARDING && player.getVehicle() == null && wp.getHorseCooldown() <= 0) {
                    if (!Utils.isMountableZone(location) || Utils.blocksInFrontOfLocation(location)) {
                        player.sendMessage(ChatColor.RED + "You can't mount here!");
                    } else {
                        double distance = player.getLocation().getY() - player.getWorld().getHighestBlockYAt(player.getLocation());
                        if (distance > 2) {
                            player.sendMessage(ChatColor.RED + "You can't mount in the air!");
                        } else if (wp.getFlagDamageMultiplier() > 0) {
                            player.sendMessage(ChatColor.RED + "You can't mount while holding the flag!");
                        } else {
                            player.playSound(player.getLocation(), "mountup", 1, 1);
                            Horse horse = (Horse) player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);
                            horse.setTamed(true);
                            horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
                            horse.setOwner(player);
                            horse.setJumpStrength(0);
                            horse.setVariant(Horse.Variant.HORSE);
                            horse.setColor(Horse.Color.BROWN);
                            horse.setStyle(Horse.Style.NONE);
                            horse.setAdult();
                            ((EntityLiving) ((CraftEntity) horse).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(.318);
                            horse.setPassenger(player);
                            wp.setHorseCooldown(15);
                        }
                    }

                } else if (itemHeld.getType() == Material.BONE) {
                    player.getInventory().remove(UndyingArmy.BONE);
                    wp.damageHealth(Warlords.getPlayer(player), "", 100000, 100000, -1, 100, false);
                } else if (itemHeld.getType() == Material.BANNER) {
                    if (wp.getFlagCooldown() > 0) {
                        player.sendMessage("§cYou cannot drop the flag yet, please wait 5 seconds!");
                    } else {
                        wp.getGameState().flags().dropFlag(player);
                        wp.setFlagCooldown(5);
                    }
                } else if (itemHeld.getType() == Material.COMPASS) {
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 2);
                    wp.toggleTeamFlagCompass();
                } else if (player.getInventory().getHeldItemSlot() == 0 || !Warlords.getPlayerSettings(wp.getUuid()).getHotKeyMode()) {
                    wp.getSpec().onRightClick(wp, player);
                }
            } else {
                if (itemHeld.getType() == Material.NETHER_STAR) {
                    //menu
                    openMainMenu(player);
                } else if (itemHeld.getType() == Material.NOTE_BLOCK) {
                    //team selector
                    openTeamMenu(player);
                } else if (itemHeld.getType() == Material.EMERALD) {
                    //wl command
                    Bukkit.getServer().dispatchCommand(player, "wl");
                }
            }
        } else if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {
            if (action == Action.LEFT_CLICK_AIR) {

            }
        }

    }

    @EventHandler
    public void onMount(VehicleEnterEvent e) {
//        if (e.getVehicle() instanceof Horse) {
//            if (!((Horse) e.getVehicle()).getOwner().equals(e.getEntered())) {
//                System.out.println(e.getEntered().getLocation());
//                //e.getVehicle().remove();
//                //e.setCancelled(true);
//            }
//        }
    }

    @EventHandler
    public void onDismount(VehicleExitEvent e) {
        e.getVehicle().remove();
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.UNKNOWN) {
            if (e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
                e.setCancelled(true);
                e.getPlayer().setSpectatorTarget(null);
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
            if (!wp.getGame().isGameFreeze()) {
                if (Warlords.getPlayerSettings(wp.getUuid()).getHotKeyMode() && (slot == 1 || slot == 2 || slot == 3 || slot == 4)) {
                    wp.getSpec().onRightClickHotKey(wp, e.getPlayer(), slot);
                    e.setCancelled(true);
                }
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
            if (e.getInventory().getHolder() != null && e.getInventory().getHolder().getInventory().getTitle().equals("Horse")) {
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
    public void onPlayerDropEvent(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onHorseJump(HorseJumpEvent e) {
        if (Warlords.hasPlayer((OfflinePlayer) e.getEntity().getPassenger())) {
            if (Objects.requireNonNull(Warlords.getPlayer(e.getEntity().getPassenger())).getGame().isGameFreeze()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (Warlords.hasPlayer(e.getPlayer()) && Objects.requireNonNull(Warlords.getPlayer(e.getPlayer())).getGame().isGameFreeze()) {
            if (e.getPlayer().getVehicle() == null) {
                e.setTo(e.getFrom());
            } else {
                e.setCancelled(true);
            }
        }
        if (e.getPlayer().getVehicle() instanceof Horse) {
            Location location = e.getPlayer().getLocation();
            if (!Utils.isMountableZone(location)) {
                e.getPlayer().getVehicle().remove();
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                e.getEntity().teleport(Warlords.getRejoinPoint(e.getEntity().getUniqueId()));
                WarlordsPlayer wp = Warlords.getPlayer(e.getEntity());
                if (wp != null) {
                    if (wp.isDeath()) {
                        wp.getEntity().teleport(wp.getLocation().clone().add(0, 100, 0));
                    } else {
                        wp.damageHealth(wp, "Fall", 1000000, 1000000, -1, 100, false);
                    }
                }
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
                            wp.damageHealth(wp, "Fall", ((damage + 3) * 40 - 200), ((damage + 3) * 40 - 200), -1, 100, false);
                            wp.setRegenTimer(10);
                        }
                    }
                }
            } else if (e.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
                //100 flat
                if (e.getEntity() instanceof Player) {
                    WarlordsPlayer wp = Warlords.getPlayer(e.getEntity());
                    if (wp != null) {
                        wp.damageHealth(wp, "Fall", 100, 100, -1, 100, false);
                        wp.setRegenTimer(10);
                    }
                }
            }
        }
        e.setCancelled(true);
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        try {
            // We need to do this in a callSyncMethod, because we need it to happen in the main thread. or else weird bugs can happen in other threads
            Bukkit.getScheduler().callSyncMethod(Warlords.getInstance(), () -> {

                if (!Warlords.playerChatChannels.containsKey(uuid)) {
                    Warlords.playerChatChannels.put(uuid, ChatChannels.ALL);
                }

                String prefix = "";
                ChatColor prefixColor = ChatColor.WHITE;

                if (PermissionHandler.isDefault(player)) {
                    prefixColor = ChatColor.AQUA;
                    prefix = prefixColor + "[SZN] ";
                } else if (PermissionHandler.isGamestarter(player)) {
                    prefixColor = ChatColor.YELLOW;
                    prefix = prefixColor + "[GS] ";
                } else if (PermissionHandler.isContentCreator(player)) {
                    prefixColor = ChatColor.LIGHT_PURPLE;
                    prefix = prefixColor + "[CT] ";
                } else if (PermissionHandler.isCoordinator(player)) {
                    prefixColor = ChatColor.GOLD;
                    prefix = prefixColor + "[COORD] ";
                } else if (PermissionHandler.isAdmin(player)) {
                    prefixColor = ChatColor.DARK_AQUA;
                    prefix = prefixColor + "[DEV] ";
                } else {
                    System.out.println(ChatColor.RED + "[WARLORDS] Player has invalid rank or permissions have not been set up properly!");
                }

                switch (Warlords.playerChatChannels.getOrDefault(uuid, ChatChannels.ALL)) {
                    case ALL:
                        WarlordsPlayer wp = Warlords.getPlayer(player);
                        PlayerSettings playerSettings = Warlords.getPlayerSettings(uuid);
                        int level = ExperienceManager.getLevelForSpec(uuid, playerSettings.getSelectedClass());

                        if (wp == null) {
                            e.setFormat(ChatColor.DARK_GRAY + "[" +
                                    ChatColor.GOLD + Classes.getClassesGroup(playerSettings.getSelectedClass()).name.toUpperCase().substring(0, 3) +
                                    ChatColor.DARK_GRAY + "][" +
                                    ChatColor.GRAY + (level < 10 ? "0" : "") + level +
                                    ChatColor.DARK_GRAY + "][" +
                                    playerSettings.getSelectedClass().specType.getColoredSymbol() +
                                    ChatColor.DARK_GRAY + "] " +

                                    (prefix) +
                                    (prefixColor) + "%1$s" +

                                    ChatColor.WHITE + ": %2$s"
                            );
                            e.getRecipients().removeIf(Warlords::hasPlayer);
                            return null;
                        }
                        e.setFormat(wp.getTeam().teamColor() + "[" +
                                wp.getTeam().prefix() + "]" +
                                ChatColor.DARK_GRAY + "[" +
                                ChatColor.GOLD + wp.getSpec().getClassNameShort() +
                                ChatColor.DARK_GRAY + "][" +
                                ChatColor.GRAY + (level < 10 ? "0" : "") + level +
                                ChatColor.DARK_GRAY + "][" +
                                playerSettings.getSelectedClass().specType.getColoredSymbol() +
                                ChatColor.DARK_GRAY + "] " +
                                (wp.isDeath() ? ChatColor.GRAY + "[SPECTATOR] " : "") +

                                (prefix) +
                                (prefixColor) + "%1$s" +

                                ChatColor.WHITE + ": %2$s"
                        );
                        if (!(wp.getGame().getState() instanceof EndState)) {
                            e.getRecipients().removeIf(p -> wp.getGame().getPlayerTeamOrNull(p.getUniqueId()) != wp.getTeam());
                        }
                        break;
                    case PARTY:
                        if (Warlords.partyManager.getPartyFromAny(uuid).isPresent()) {
                            e.setFormat(ChatColor.BLUE + "Party" + ChatColor.DARK_GRAY + " > " +
                                    (prefixColor) + "%1$s" +
                                    ChatColor.WHITE + ": %2$s"
                            );
                            e.getRecipients().retainAll(Warlords.partyManager.getPartyFromAny(uuid).get().getAllPartyPeoplePlayerOnline());
                        } else {
                            player.sendMessage(ChatColor.RED + "You are not in a party and were moved to the ALL channel.");
                            Warlords.playerChatChannels.put(uuid, ChatChannels.ALL);
                            e.setCancelled(true);
                            return null;
                        }
                        break;
                }
                return null;
            }).get();
        } catch (InterruptedException | ExecutionException ex) {
            Warlords.getInstance().getLogger().log(Level.SEVERE, null, ex);
            System.out.println("UUID: " + uuid);
            System.out.println("Chat Channels: " + Warlords.playerChatChannels);
            System.out.println("Player Chat Channel: " + Warlords.playerChatChannels.get(uuid));
            System.out.println("Contains UUID: " + Warlords.playerChatChannels.containsKey(uuid));
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
        if (change.getWorld().hasStorm()) {
            change.getWorld().setWeatherDuration(0);
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent change) {
        change.setCancelled(true);
        if (change.getEntity() instanceof Player) {
            ((Player) change.getEntity()).setFoodLevel(20);
        }
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFlagChange(WarlordsFlagUpdatedEvent event) {
        //Bukkit.broadcastMessage(event.getTeam() + " " + event.getOld().getClass().getSimpleName() + " => " + event.getNew().getClass().getSimpleName());
        if (event.getOld() instanceof PlayerFlagLocation) {
            ((PlayerFlagLocation) event.getOld()).getPlayer().setFlagDamageMultiplier(0);
        }

        if (event.getNew() instanceof PlayerFlagLocation) {
            PlayerFlagLocation pfl = (PlayerFlagLocation) event.getNew();
            WarlordsPlayer player = pfl.getPlayer();
            player.setFlagDamageMultiplier(pfl.getComputedMultiplier());
            if (!(event.getOld() instanceof PlayerFlagLocation)) {
                // eg GROUND -> PLAYER
                // or SPAWN -> PLAYER
                ChatColor enemyColor = event.getTeam().enemy().teamColor();
                event.getGame().forEachOnlinePlayer((p, t) -> {
                    p.sendMessage(enemyColor + player.getName() + " §epicked up the " + event.getTeam().coloredPrefix() + " §eflag!");
                    PacketUtils.sendTitle(p, "", enemyColor + player.getName() + " §epicked up the " + event.getTeam().coloredPrefix() + " §eflag!", 0, 60, 0);
                    if (t == event.getTeam()) {
                        p.playSound(player.getLocation(), "ctf.friendlyflagtaken", 500, 1);
                    } else {
                        p.playSound(player.getLocation(), "ctf.enemyflagtaken", 500, 1);
                    }
                });
                event.getGame().getSpectators().forEach(uuid -> {
                    if (Bukkit.getPlayer(uuid) != null) {
                        Player p = Bukkit.getPlayer(uuid);
                        p.sendMessage(enemyColor + player.getName() + " §epicked up the " + event.getTeam().coloredPrefix() + " §eflag!");
                        PacketUtils.sendTitle(p, "", enemyColor + player.getName() + " §epicked up the " + event.getTeam().coloredPrefix() + " §eflag!", 0, 60, 0);
                    }
                });
            } else {
                // PLAYER -> PLAYER only happens if the multiplier gets to a new scale
                if (pfl.getComputedHumanMultiplier() % 10 == 0) {
                    event.getGame().forEachOnlinePlayer((p, t) -> {
                        p.sendMessage("§eThe " + event.getTeam().coloredPrefix() + " §eflag carrier now takes §c" + pfl.getComputedHumanMultiplier() + "% §eincreased damage!");
                    });
                    event.getGame().getSpectators().forEach(uuid -> {
                        if (Bukkit.getPlayer(uuid) != null) {
                            Player p = Bukkit.getPlayer(uuid);
                            p.sendMessage("§eThe " + event.getTeam().coloredPrefix() + " §eflag carrier now takes §c" + pfl.getComputedHumanMultiplier() + "% §eincreased damage!");
                        }
                    });
                }
            }
        } else if (event.getNew() instanceof SpawnFlagLocation) {
            String toucher = ((SpawnFlagLocation) event.getNew()).getLastToucher();
            if (event.getOld() instanceof GroundFlagLocation) {
                if (toucher != null) {
                    Objects.requireNonNull(Warlords.getPlayer(Bukkit.getPlayer(toucher).getUniqueId())).addFlagReturn();
                    event.getGame().forEachOnlinePlayer((p, t) -> {
                        ChatColor color = event.getTeam().teamColor();
                        p.sendMessage(color + toucher + " §ehas returned the " + event.getTeam().coloredPrefix() + " §eflag!");
                        PacketUtils.sendTitle(p, "", color + toucher + " §ehas returned the " + event.getTeam().coloredPrefix() + " §eflag!", 0, 60, 0);
                        if (t == event.getTeam()) {
                            p.playSound(p.getLocation(), "ctf.flagreturned", 500, 1);
                        }
                    });
                    event.getGame().getSpectators().forEach(uuid -> {
                        if (Bukkit.getPlayer(uuid) != null) {
                            Player p = Bukkit.getPlayer(uuid);
                            ChatColor color = event.getTeam().teamColor();
                            p.sendMessage(color + toucher + " §ehas returned the " + event.getTeam().coloredPrefix() + " §eflag!");
                            PacketUtils.sendTitle(p, "", color + toucher + " §ehas returned the " + event.getTeam().coloredPrefix() + " §eflag!", 0, 60, 0);
                        }
                    });
                } else {
                    event.getGame().forEachOnlinePlayer((p, t) -> {
                        p.sendMessage("§eThe " + event.getTeam().coloredPrefix() + " §eflag has returned to its base.");
                    });
                    event.getGame().getSpectators().forEach(uuid -> {
                        if (Bukkit.getPlayer(uuid) != null) {
                            Player p = Bukkit.getPlayer(uuid);
                            p.sendMessage("§eThe " + event.getTeam().coloredPrefix() + " §eflag has returned to its base.");
                        }
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
                event.getGame().getSpectators().forEach(uuid -> {
                    if (Bukkit.getPlayer(uuid) != null) {
                        Player p = Bukkit.getPlayer(uuid);
                        p.sendMessage(playerColor + pfl.getPlayer().getName() + " §ehas dropped the " + flag + " §eflag!");
                        PacketUtils.sendTitle(p, "", playerColor + pfl.getPlayer().getName() + " §ehas dropped the " + flag + " §eflag!", 0, 60, 0);
                    }
                });
            }
        } else if (event.getNew() instanceof WaitingFlagLocation && ((WaitingFlagLocation) event.getNew()).wasWinner()) {
            if (event.getOld() instanceof PlayerFlagLocation) {
                PlayerFlagLocation pfl = (PlayerFlagLocation) event.getOld();
                Team loser = event.getTeam();
                event.getGameState().addCapture(pfl.getPlayer());
                pfl.getPlayer().addFlagCap();
                event.getGame().forEachOnlinePlayer((p, t) -> {
                    String message = pfl.getPlayer().getColoredName() + " §ecaptured the " + loser.coloredPrefix() + " §eflag!";
                    p.sendMessage(message);
                    PacketUtils.sendTitle(p, "", message, 0, 60, 0);

                    if (event.getTeam() == t) {
                        p.playSound(pfl.getLocation(), "ctf.enemycapturedtheflag", 500, 1);
                    } else {
                        p.playSound(pfl.getLocation(), "ctf.enemyflagcaptured", 500, 1);
                    }
                });
                event.getGame().getSpectators().forEach(uuid -> {
                    if (Bukkit.getPlayer(uuid) != null) {
                        Player p = Bukkit.getPlayer(uuid);
                        String message = pfl.getPlayer().getColoredName() + " §ecaptured the " + loser.coloredPrefix() + " §eflag!";
                        p.sendMessage(message);
                        PacketUtils.sendTitle(p, "", message, 0, 60, 0);
                    }
                });
            }
        }
    }
}
