package com.ebicep.warlords.events;

import com.ebicep.warlords.ChatChannels;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.classes.abilties.IceBarrier;
import com.ebicep.warlords.classes.abilties.Soulbinding;
import com.ebicep.warlords.classes.abilties.TimeWarp;
import com.ebicep.warlords.classes.abilties.UndyingArmy;
import com.ebicep.warlords.classes.shaman.specs.spiritguard.Spiritguard;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.LeaderboardManager;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGame;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.game.flags.GroundFlagLocation;
import com.ebicep.warlords.game.flags.PlayerFlagLocation;
import com.ebicep.warlords.game.flags.SpawnFlagLocation;
import com.ebicep.warlords.game.flags.WaitingFlagLocation;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.game.state.PreLobbyState;
import com.ebicep.warlords.party.RegularGamesMenu;
import com.ebicep.warlords.permissions.PermissionHandler;
import com.ebicep.warlords.player.*;
import com.ebicep.warlords.player.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.util.*;
import org.bukkit.*;
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
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
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
    public static void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (DatabaseManager.playerService == null && DatabaseManager.enabled) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Please wait!");
        }
    }

//    @EventHandler
//    public static void onPlayerLogin(PlayerLoginEvent event) {
//        if (DatabaseManager.playerService == null && DatabaseManager.enabled) {
//            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Please wait!");
//        }
//    }

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        WarlordsPlayer wp = Warlords.getPlayer(player);
        if (wp != null) {
            if (wp.isAlive()) {
                e.getPlayer().setAllowFlight(false);
            }
            e.setJoinMessage(wp.getColoredNameBold() + ChatColor.GOLD + " rejoined the game!");
        } else {
            //checking if in game lobby
            e.getPlayer().setAllowFlight(true);
            e.setJoinMessage(ChatColor.AQUA + e.getPlayer().getName() + ChatColor.GOLD + " joined the lobby!");

            Warlords.newChain()
                    .async(() -> {
                        DatabaseManager.loadPlayer(e.getPlayer().getUniqueId(), PlayersCollections.LIFETIME, () -> {
                            Warlords.updateHead(e.getPlayer());

                            Location rejoinPoint = Warlords.getRejoinPoint(player.getUniqueId());
                            if (LeaderboardManager.loaded && Bukkit.getWorlds().get(0).equals(rejoinPoint.getWorld())) {
                                LeaderboardManager.setLeaderboardHologramVisibility(player);
                                DatabaseGame.setGameHologramVisibility(player);
                                Warlords.playerScoreboards.get(player.getUniqueId()).giveMainLobbyScoreboard();
                                ExperienceManager.giveExperienceBar(player);
                            }
                        });
                    })
                    .execute();
        }

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
        Warlords.getGameManager().dropPlayerFromQueueOrGames(e.getPlayer());
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
            player.removePotionEffect(PotionEffectType.BLINDNESS);
            player.removePotionEffect(PotionEffectType.SLOW);
            player.removePotionEffect(PotionEffectType.ABSORPTION);
            player.setGameMode(GameMode.ADVENTURE);

            ChatUtils.sendCenteredMessage(player, ChatColor.BLUE + "-----------------------------------------------------");
            ChatUtils.sendCenteredMessage(player, ChatColor.GOLD + "Welcome to Warlords 2.0 " + ChatColor.GRAY + "(" + ChatColor.RED + Warlords.VERSION + ChatColor.GRAY + ")");
            ChatUtils.sendCenteredMessage(player, ChatColor.GOLD + "Developed by " + ChatColor.RED + "sumSmash " + ChatColor.GOLD + "&" + ChatColor.RED + " Plikie");
            ChatUtils.sendCenteredMessage(player, ChatColor.GREEN + "/hotkeymode " + ChatColor.GOLD + "to change your hotkey mode.");
            ChatUtils.sendCenteredMessage(player, ChatColor.GOLD + "Click the Nether Star or do " + ChatColor.GREEN + "/menu" + ChatColor.GOLD + " to open the selection menu.");
            ChatUtils.sendCenteredMessage(player, "");
            ChatUtils.sendCenteredMessage(player, ChatColor.GOLD + "Make sure to join our discord if you wish to stay up-to-date with our most recent patches, interact with our community and make bug reports or game suggestions at: " + ChatColor.RED + "§ldiscord.gg/GWPAx9sEG7");
            ChatUtils.sendCenteredMessage(player, "");
            ChatUtils.sendCenteredMessage(player, ChatColor.GOLD + "You may download our resource pack at: " + ChatColor.RED + "§lhttps://bit.ly/3285WkL");
            ChatUtils.sendCenteredMessage(player, "");
            ChatUtils.sendCenteredMessage(player, ChatColor.RED + "DISCLAIMER: " + ChatColor.GRAY + "Non-competitive players should take notice that we are currently in BETA for our public queue. This means that the server will regularly be unavailable when we host our private games during weekends!");
            ChatUtils.sendCenteredMessage(player, ChatColor.BLUE + "-----------------------------------------------------");

            PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
            Classes selectedClass = playerSettings.getSelectedClass();
            AbstractPlayerClass apc = selectedClass.create.get();

            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
            player.getInventory().setItem(1, new ItemBuilder(apc.getWeapon().getItem(playerSettings.getWeaponSkins()
                    .getOrDefault(selectedClass, Weapons.FELFLAME_BLADE).item)).name("§aWeapon Skin Preview")
                    .lore("")
                    .get());
            player.getInventory().setItem(4, new ItemBuilder(Material.NETHER_STAR).name("§aSelection Menu").get());

            if (!fromGame) {
                Warlords.partyManager.getPartyFromAny(player.getUniqueId()).ifPresent(party -> {
                    List<RegularGamesMenu.RegularGamePlayer> playerList = party.getRegularGamesMenu().getRegularGamePlayers();
                    if (!playerList.isEmpty()) {
                        playerList.stream()
                                .filter(regularGamePlayer -> regularGamePlayer.getUuid().equals(player.getUniqueId()))
                                .findFirst()
                                .ifPresent(regularGamePlayer -> player.getInventory().setItem(7,
                                                new ItemBuilder(regularGamePlayer.getTeam().item).name("§aTeam Builder")
                                                        .get()
                                        )
                                );
                    }
                });
            }

            if (player.hasPermission("warlords.game.debug")) {
                player.getInventory().setItem(3, new ItemBuilder(Material.EMERALD).name("§aDebug Menu").get());
            }

            if (fromGame) {
                Warlords.playerScoreboards.get(player.getUniqueId()).giveMainLobbyScoreboard();
                ExperienceManager.giveExperienceBar(player);
            }

            player.getActivePotionEffects().clear();
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
    public static void onPlayerQuit(PlayerQuitEvent e) {
        WarlordsPlayer wp = Warlords.getPlayer(e.getPlayer());
        if (wp != null) {
            wp.updatePlayerReference(null);
            e.setQuitMessage(wp.getColoredNameBold() + ChatColor.GOLD + " left the game!");
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

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if ((e.getEntity() instanceof Player || e.getEntity() instanceof Zombie) && e.getDamager() instanceof Player) {
            Player attacker = (Player) e.getDamager();
            WarlordsPlayer wpAttacker = Warlords.getPlayer(attacker);
            WarlordsPlayer wpVictim = Warlords.getPlayer(e.getEntity());
            if (wpAttacker != null && wpAttacker.isEnemyAlive(wpVictim) && !wpAttacker.getGame().isFrozen()) {

                if (attacker.getInventory().getHeldItemSlot() == 0 && wpAttacker.getHitCooldown() == 0) {

                    wpAttacker.setHitCooldown(12);
                    wpAttacker.subtractEnergy(wpAttacker.getSpec().getEnergyOnHit() * -1);

                    if (wpAttacker.getSpec() instanceof Spiritguard && wpAttacker.getCooldownManager().hasCooldown(Soulbinding.class)) {

                        Soulbinding baseSoulbinding = (Soulbinding) wpAttacker.getSpec().getPurple();
                        new CooldownFilter<>(wpAttacker, PersistentCooldown.class)
                            .filter(PersistentCooldown::isShown)
                            .filterCooldownClassAndMapToObjectsOfClass(Soulbinding.class)
                            .forEachOrdered(soulbinding -> {
                                if (soulbinding.hasBoundPlayer(wpVictim)) {
                                    soulbinding.getSoulBindedPlayers().stream()
                                            .filter(p -> p.getBoundPlayer() == wpVictim)
                                            .forEach(boundPlayer -> {
                                                boundPlayer.setHitWithSoul(false);
                                                boundPlayer.setHitWithLink(false);
                                                boundPlayer.setTimeLeft(baseSoulbinding.getBindDuration());
                                            });
                                } else {
                                    wpVictim.sendMessage(ChatColor.RED + "\u00AB " + ChatColor.GRAY + "You have been bound by " + wpAttacker.getName() + "'s " + ChatColor.LIGHT_PURPLE + "Soulbinding Weapon" + ChatColor.GRAY + "!");
                                    wpAttacker.sendMessage(ChatColor.GREEN + "\u00BB " + ChatColor.GRAY + "Your " + ChatColor.LIGHT_PURPLE + "Soulbinding Weapon " + ChatColor.GRAY + "has bound " + wpVictim.getName() + "!");
                                    soulbinding.getSoulBindedPlayers().add(new Soulbinding.SoulBoundPlayer(wpVictim, baseSoulbinding.getBindDuration()));
                                    Utils.playGlobalSound(wpVictim.getLocation(), "shaman.earthlivingweapon.activation", 2, 1);
                                }
                            });
                    }
                    wpVictim.addDamageInstance(wpAttacker, "", 132, 179, 25, 200, false);
                    wpVictim.updateJimmyHealth();
                }

                if (wpVictim.getCooldownManager().hasCooldown(IceBarrier.class)) {
                    wpAttacker.getSpeed().addSpeedModifier("Ice Barrier", -20, 2 * 20);
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
            if (wp != null && wp.isAlive() && !wp.getGame().isFrozen()) {
                if (player.getInventory().getHeldItemSlot() == 7 && itemHeld.getType() == Material.GOLD_BARDING && player.getVehicle() == null && wp.getHorseCooldown() <= 0) {
                    if (!Utils.isMountableZone(location) || Utils.blocksInFrontOfLocation(location)) {
                        player.sendMessage(ChatColor.RED + "You can't mount here!");
                    } else {
                        double distance = player.getLocation().getY() - player.getWorld().getHighestBlockYAt(player.getLocation());
                        if (distance > 2) {
                            player.sendMessage(ChatColor.RED + "You can't mount in the air!");
                        } else if (wp.getCarriedFlag() != null) {
                            player.sendMessage(ChatColor.RED + "You can't mount while holding the flag!");
                        } else {
                            player.playSound(player.getLocation(), "mountup", 1, 1);
                            wp.getHorse().spawn();
                            wp.setHorseCooldown(wp.getHorse().getCooldown());
                        }
                    }

                } else if (itemHeld.getType() == Material.BONE) {
                    player.getInventory().remove(UndyingArmy.BONE);
                    wp.addDamageInstance(Warlords.getPlayer(player), "", 100000, 100000, -1, 100, false);
                } else if (itemHeld.getType() == Material.BANNER) {
                    if (wp.getFlagCooldown() > 0) {
                        player.sendMessage("§cYou cannot drop the flag yet, please wait 5 seconds!");
                    } else if (wp.getCooldownManager().hasCooldown(TimeWarp.class)) {
                        player.sendMessage(ChatColor.RED + "You cannot drop the flag with a Time Warp active!");
                    } else {
                        FlagHolder.dropFlagForPlayer(wp);
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
                } else if (itemHeld.getType() == Material.WOOL) {
                    if (itemHeld.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Team Builder")) {
                        Warlords.partyManager.getPartyFromAny(player.getUniqueId()).ifPresent(party -> {
                            List<RegularGamesMenu.RegularGamePlayer> playerList = party.getRegularGamesMenu().getRegularGamePlayers();
                            if (!playerList.isEmpty()) {
                                party.getRegularGamesMenu().openMenuForPlayer(player);
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        if (player.getOpenInventory().getTopInventory().getName().equals("Team Builder")) {
                                            party.getRegularGamesMenu().openMenuForPlayer(player);
                                        } else {
                                            this.cancel();
                                        }
                                    }
                                }.runTaskTimer(Warlords.getInstance(), 20, 10);
                            }
                        });
                    }
                } else {
                    PreLobbyState state = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).flatMap(g -> g.getState(PreLobbyState.class)).orElse(null);
                    if (state != null) {
                        state.interactEvent(player, player.getInventory().getHeldItemSlot());
                    }
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
            WarlordsPlayer warlordsPlayer = Warlords.getPlayer(e.getPlayer().getUniqueId());
            if (warlordsPlayer == null) {
                return;
            }
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
            if (!wp.getGame().isFrozen()) {
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
            if (Objects.requireNonNull(Warlords.getPlayer(e.getEntity().getPassenger())).getGame().isFrozen()) {
                e.setCancelled(true);
            }
        }
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
        if (e.getEntity() instanceof Player) {
            if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                e.getEntity().teleport(Warlords.getRejoinPoint(e.getEntity().getUniqueId()));
                WarlordsPlayer wp = Warlords.getPlayer(e.getEntity());
                if (wp != null) {
                    if (wp.isDeath()) {
                        wp.getEntity().teleport(wp.getLocation().clone().add(0, 100, 0));
                    } else {
                        wp.addDamageInstance(wp, "Fall", 1000000, 1000000, -1, 100, false);
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
                            wp.addDamageInstance(wp, "Fall", ((damage + 3) * 40 - 200), ((damage + 3) * 40 - 200), -1, 100, false);
                            wp.setRegenTimer(10);
                        }
                    }
                }
            } else if (e.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
                //100 flat
                if (e.getEntity() instanceof Player) {
                    WarlordsPlayer wp = Warlords.getPlayer(e.getEntity());
                    if (wp != null) {
                        wp.addDamageInstance(wp, "Fall", 100, 100, -1, 100, false);
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
                    prefix = prefixColor + "";
                } else if (PermissionHandler.isGameTester(player)) {
                    prefixColor = ChatColor.YELLOW;
                    prefix = prefixColor + "[T] ";
                } else if (PermissionHandler.isGameStarter(player)) {
                    prefixColor = ChatColor.YELLOW;
                    prefix = prefixColor + "[GS] ";
                } else if (PermissionHandler.isContentCreator(player)) {
                    prefixColor = ChatColor.LIGHT_PURPLE;
                    prefix = prefixColor + "[CT] ";
                } else if (PermissionHandler.isCoordinator(player)) {
                    prefixColor = ChatColor.GOLD;
                    prefix = prefixColor + "[HGS] ";
                } else if (PermissionHandler.isAdmin(player)) {
                    prefixColor = ChatColor.DARK_AQUA;
                    prefix = prefixColor + "[ADMIN] ";
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
                            e.getRecipients().removeIf(p -> wp.getGame().getPlayerTeam(p.getUniqueId()) != wp.getTeam());
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
            ((PlayerFlagLocation) event.getOld()).getPlayer().setCarriedFlag(null);
        }

        if (event.getNew() instanceof PlayerFlagLocation) {
            PlayerFlagLocation pfl = (PlayerFlagLocation) event.getNew();
            WarlordsPlayer player = pfl.getPlayer();
            player.setCarriedFlag(event.getInfo());
            if (event.getOld() instanceof PlayerFlagLocation) {
                // PLAYER -> PLAYER only happens if the multiplier gets to a new scale
                if (pfl.getComputedHumanMultiplier() % 10 == 0) {
                    event.getGame().forEachOnlinePlayerWithoutSpectators((p, t) -> {
                        p.sendMessage("§eThe " + event.getTeam().coloredPrefix() + " §eflag carrier now takes §c" + pfl.getComputedHumanMultiplier() + "% §eincreased damage!");
                    });
                    event.getGame().spectators().forEach(uuid -> {
                        if (Bukkit.getPlayer(uuid) != null) {
                            Player p = Bukkit.getPlayer(uuid);
                            p.sendMessage("§eThe " + event.getTeam().coloredPrefix() + " §eflag carrier now takes §c" + pfl.getComputedHumanMultiplier() + "% §eincreased damage!");
                        }
                    });
                }
            } else {
                // eg GROUND -> PLAYER
                // or SPAWN -> PLAYER
                ChatColor enemyColor = event.getTeam().enemy().teamColor();
                event.getGame().forEachOnlinePlayerWithoutSpectators((p, t) -> {
                    p.sendMessage(enemyColor + player.getName() + " §epicked up the " + event.getTeam().coloredPrefix() + " §eflag!");
                    PacketUtils.sendTitle(p, "", enemyColor + player.getName() + " §epicked up the " + event.getTeam().coloredPrefix() + " §eflag!", 0, 60, 0);
                    if (t == event.getTeam()) {
                        p.playSound(player.getLocation(), "ctf.friendlyflagtaken", 500, 1);
                    } else {
                        p.playSound(player.getLocation(), "ctf.enemyflagtaken", 500, 1);
                    }
                });
                event.getGame().spectators().forEach(uuid -> {
                    if (Bukkit.getPlayer(uuid) != null) {
                        Player p = Bukkit.getPlayer(uuid);
                        p.sendMessage(enemyColor + player.getName() + " §epicked up the " + event.getTeam().coloredPrefix() + " §eflag!");
                        PacketUtils.sendTitle(p, "", enemyColor + player.getName() + " §epicked up the " + event.getTeam().coloredPrefix() + " §eflag!", 0, 60, 0);
                    }
                });
            }
        } else if (event.getNew() instanceof SpawnFlagLocation) {
            WarlordsPlayer toucher = ((SpawnFlagLocation) event.getNew()).getFlagReturner();
            if (event.getOld() instanceof GroundFlagLocation) {
                if (toucher != null) {
                    toucher.addFlagReturn();
                    event.getGame().forEachOnlinePlayer((p, t) -> {
                        ChatColor color = event.getTeam().teamColor();
                        p.sendMessage(color + toucher.getName() + " §ehas returned the " + event.getTeam().coloredPrefix() + " §eflag!");
                        PacketUtils.sendTitle(p, "", color + toucher.getName() + " §ehas returned the " + event.getTeam().coloredPrefix() + " §eflag!", 0, 60, 0);
                        if (t == event.getTeam()) {
                            p.playSound(p.getLocation(), "ctf.flagreturned", 500, 1);
                        }
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
        } else if (event.getNew() instanceof WaitingFlagLocation && ((WaitingFlagLocation) event.getNew()).getScorer() != null) {
            WarlordsPlayer player = ((WaitingFlagLocation) event.getNew()).getScorer();
            player.addFlagCap();
            event.getGame().forEachOnlinePlayer((p, t) -> {
                String message = player.getColoredName() + " §ecaptured the " + event.getInfo().getTeam().coloredPrefix() + " §eflag!";
                p.sendMessage(message);
                PacketUtils.sendTitle(p, "", message, 0, 60, 0);

                if (t != null) {
                    if (event.getTeam() == t) {
                        p.playSound(player.getLocation(), "ctf.enemycapturedtheflag", 500, 1);
                    } else {
                        p.playSound(player.getLocation(), "ctf.enemyflagcaptured", 500, 1);
                    }
                }
            });
        }
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event) {
        dropFlag(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDeath(WarlordsDeathEvent event) {
        dropFlag(event.getPlayer());
    }

    public boolean dropFlag(Player player) {
        return dropFlag(Warlords.getPlayer(player));
    }

    public boolean dropFlag(@Nullable WarlordsPlayer player) {
        if (player == null) {
            return false;
        }
        FlagHolder.dropFlagForPlayer(player);
        return true;
    }
}
