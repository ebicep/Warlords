package com.ebicep.warlords.events;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.AbstractTimeWarpBase;
import com.ebicep.warlords.classes.shaman.specs.Spiritguard;
import com.ebicep.warlords.commands.debugcommands.misc.MuteCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.FutureMessage;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.game.WarlordsFlagUpdatedEvent;
import com.ebicep.warlords.events.player.DatabasePlayerFirstLoadEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameManager;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.flags.*;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.game.state.PreLobbyState;
import com.ebicep.warlords.menu.PlayerHotBarItemListener;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.player.general.*;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.Utils;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R2.inventory.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class WarlordsEvents implements Listener {

    public static Set<Entity> entityList = new HashSet<>();

    public static void addEntityUUID(Entity entity) {
        entityList.add(entity);
    }

    @EventHandler
    public static void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (Bukkit.hasWhitelist() && Bukkit.getWhitelistedPlayers().stream().noneMatch(p -> p.getUniqueId().equals(event.getUniqueId()))) {
            return;
        }
        if (DatabaseManager.playerService == null && DatabaseManager.enabled) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Component.text("Please wait!"));
        } else {
            if (!DatabaseManager.enabled) {
                return;
            }
            UUID uuid = event.getUniqueId();
            for (PlayersCollections activeCollection : PlayersCollections.ACTIVE_COLLECTIONS) {
                DatabaseManager.loadPlayer(uuid, activeCollection, (databasePlayer) -> {
                    if (!Objects.equals(databasePlayer.getName(), event.getName())) {
                        databasePlayer.setName(event.getName());
                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer, activeCollection);
                    }
                });
            }
        }
    }

    @EventHandler
    public static void onPlayerLogin(PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return;
        }
        if (!DatabaseManager.enabled || DatabaseManager.playerService == null) {
            return;
        }
        if (!DatabaseManager.inCache(event.getPlayer().getUniqueId(), PlayersCollections.LIFETIME)) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Component.text("Unable to load player data. Report this if this issue persists."));
        }
    }

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        WarlordsEntity wp = Warlords.getPlayer(player);
        if (wp != null) {
            if (wp.isAlive()) {
                e.getPlayer().setAllowFlight(false);
            }
            e.joinMessage(wp.getColoredNameBold().append(Component.text(" rejoined the game!", NamedTextColor.YELLOW)));
        } else {
            player.setAllowFlight(true);
            e.joinMessage(Permissions.getPrefixWithColor(player)
                                     .append(Component.text(player.getName()))
                                     .append(Component.text(" joined the lobby!", NamedTextColor.GOLD))
            );
        }

        CustomScoreboard customScoreboard = CustomScoreboard.getPlayerScoreboard(player);
        player.setScoreboard(customScoreboard.getScoreboard());
        joinInteraction(player, false);

        Bukkit.getOnlinePlayers().forEach(p -> {
            p.showTitle(Title.title(
                    Component.text("Welcome to ", NamedTextColor.AQUA)
                             .append(Component.text("Warlords 2.0", NamedTextColor.YELLOW, TextDecoration.BOLD)),
                    Component.text("Players Online: ", NamedTextColor.GREEN)
                             .append(Component.text(Bukkit.getOnlinePlayers().size(), NamedTextColor.GRAY))
            ));
        });
        Warlords.getGameManager().dropPlayerFromQueueOrGames(e.getPlayer());
    }

    public static void joinInteraction(Player player, boolean fromGame) {
        UUID uuid = player.getUniqueId();
        Location rejoinPoint = Warlords.getRejoinPoint(uuid);
        boolean isSpawnWorld = Bukkit.getWorlds().get(0).getName().equals(rejoinPoint.getWorld().getName());
        boolean playerIsInWrongWorld = !player.getWorld().getName().equals(rejoinPoint.getWorld().getName());
        if (isSpawnWorld || playerIsInWrongWorld) {
            player.teleport(rejoinPoint);
        }
        if (playerIsInWrongWorld && isSpawnWorld) {
            player.sendMessage(Component.text("The game you were previously playing is no longer running!", NamedTextColor.RED));
        }
        if (playerIsInWrongWorld && !isSpawnWorld) {
            player.sendMessage(Component.text("The game started without you, but we still love you enough and you were warped into the game", NamedTextColor.RED));
        }
        if (isSpawnWorld) {
            player.removePotionEffect(PotionEffectType.BLINDNESS);
            player.removePotionEffect(PotionEffectType.SLOW);
            player.removePotionEffect(PotionEffectType.ABSORPTION);
            player.setGameMode(GameMode.ADVENTURE);
            player.setMaxHealth(20);
            player.setHealth(20);

            ChatUtils.sendCenteredMessage(player, Component.text("-----------------------------------------------------", NamedTextColor.GRAY));
            ChatUtils.sendCenteredMessage(player, Component.textOfChildren(
                    Component.text("Welcome to Warlords 2.0 ", NamedTextColor.GOLD, TextDecoration.BOLD),
                    Component.text("(", NamedTextColor.GRAY),
                    Component.text(Warlords.VERSION, NamedTextColor.RED),
                    Component.text(")", NamedTextColor.GRAY)
            ));

            ChatUtils.sendCenteredMessage(player,
                    Component.text("Developed by ", NamedTextColor.GOLD)
                             .append(Component.text("sumSmash", NamedTextColor.RED))
                             .append(Component.text(" & "))
                             .append(Component.text("Plikie", NamedTextColor.RED))
            );
            ChatUtils.sendCenteredMessage(player, Component.empty());
            ChatUtils.sendCenteredMessage(player, Component.text("More Information: ", NamedTextColor.GOLD));
            ChatUtils.sendCenteredMessage(player, Component.text("§lhttps://docs.flairy.me/index.html", NamedTextColor.RED));
            ChatUtils.sendCenteredMessage(player, Component.text("§lhttps://ojagerl.nl/", NamedTextColor.RED));
            ChatUtils.sendCenteredMessage(player, Component.empty());
            ChatUtils.sendCenteredMessage(player,
                    Component.text("Discord: ", NamedTextColor.GOLD).append(Component.text("discord.gg/GWPAx9sEG7", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD))
            );
            ChatUtils.sendCenteredMessage(player,
                    Component.text("Resource Pack: ", NamedTextColor.GOLD).append(Component.text("https://bit.ly/3J1lGGn", NamedTextColor.GREEN, TextDecoration.BOLD))
            );
            ChatUtils.sendCenteredMessage(player, Component.text("-----------------------------------------------------", NamedTextColor.GRAY));

            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
            PlayerHotBarItemListener.giveLobbyHotBar(player, fromGame);

            DatabaseManager.getPlayer(uuid, databasePlayer -> {
                if (fromGame) {
                    //check all spec prestige
                    for (Specializations value : Specializations.VALUES) {
                        int level = ExperienceManager.getLevelForSpec(uuid, value);
                        if (level < ExperienceManager.LEVEL_TO_PRESTIGE) {
                            continue;
                        }
                        databasePlayer.getSpec(value).addPrestige();
                        int prestige = databasePlayer.getSpec(value).getPrestige();
                        FireWorkEffectPlayer.playFirework(player.getLocation(), FireworkEffect.builder()
                                                                                              .with(FireworkEffect.Type.BALL)
                                                                                              .withColor(Color.fromRGB(ExperienceManager.PRESTIGE_COLORS.get(prestige).value()))
                                                                                              .build()
                        );
                        player.showTitle(Title.title(
                                Component.text("###", NamedTextColor.WHITE, TextDecoration.OBFUSCATED)
                                         .append(Component.text(" Prestige " + value.name + " ", NamedTextColor.GOLD, TextDecoration.BOLD))
                                         .append(Component.text("###", NamedTextColor.WHITE, TextDecoration.OBFUSCATED)),
                                Component.text(prestige - 1, ExperienceManager.PRESTIGE_COLORS.get(prestige - 1))
                                         .append(Component.text(" > ", NamedTextColor.GRAY))
                                         .append(Component.text(prestige, ExperienceManager.PRESTIGE_COLORS.get(prestige))),
                                Title.Times.times(Ticks.duration(20), Ticks.duration(140), Ticks.duration(20))
                        ));
                        //sumSmash is now prestige level 5 in Pyromancer!
                        Bukkit.broadcast(Permissions.getPrefixWithColor(player)
                                                    .append(Component.text(player.getName()))
                                                    .append(Component.text(" is now prestige level ", NamedTextColor.GRAY))
                                                    .append(Component.text(prestige, ExperienceManager.PRESTIGE_COLORS.get(prestige)))
                                                    .append(Component.text(" in ", NamedTextColor.GRAY))
                                                    .append(Component.text(value.name, NamedTextColor.GOLD)));

                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                    }
                } else {
                    databasePlayer.setLastLogin(Instant.now());
                    HeadUtils.updateHead(player);
                    //future messages
                    Warlords.newChain()
                            .delay(20)
                            .async(() -> {
                                List<FutureMessage> futureMessages = databasePlayer.getFutureMessages();
                                if (!futureMessages.isEmpty()) {
                                    futureMessages.forEach(futureMessage -> futureMessage.sendToPlayer(player));
                                    futureMessages.clear();
                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                }
                            }).execute();
                    List<String> permissions = player.getEffectivePermissions()
                                                     .stream()
                                                     .map(PermissionAttachmentInfo::getPermission)
                                                     .collect(Collectors.toList());
                    permissions.remove("group.default");
                    for (PlayersCollections activeCollection : PlayersCollections.ACTIVE_COLLECTIONS) {
                        DatabaseManager.updatePlayer(uuid, activeCollection, dp -> dp.setPermissions(permissions));
                    }
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                    Bukkit.getPluginManager().callEvent(new DatabasePlayerFirstLoadEvent(player, databasePlayer));
                }
                CustomScoreboard.updateLobbyPlayerNames();
                ExperienceManager.giveExperienceBar(player);
                if (StatsLeaderboardManager.loaded) {
                    StatsLeaderboardManager.setLeaderboardHologramVisibility(player);
                    DatabaseGameBase.setGameHologramVisibility(player);
                }
            }, () -> {
                if (!fromGame) {
                    player.kick(Component.text("Unable to load player data. Report this if this issue persists.*"));
                }
            });
            CustomScoreboard.getPlayerScoreboard(player).giveMainLobbyScoreboard();
        }

        WarlordsEntity wp1 = Warlords.getPlayer(player);
        WarlordsPlayer p = wp1 instanceof WarlordsPlayer ? (WarlordsPlayer) wp1 : null;
        if (p != null) {
            player.teleport(p.getLocation());
            p.updatePlayerReference(player);
        } else {
            player.setAllowFlight(true);
        }

        Warlords.getInstance().hideAndUnhidePeople(player);
    }

    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent e) {
        WarlordsEntity wp1 = Warlords.getPlayer(e.getPlayer());
        WarlordsPlayer wp = wp1 instanceof WarlordsPlayer ? (WarlordsPlayer) wp1 : null;
        if (wp != null) {
            wp.updatePlayerReference(null);
            e.quitMessage(wp.getColoredNameBold().append(Component.text(" left the game!", NamedTextColor.GOLD)));
        } else {
            e.quitMessage(Permissions.getPrefixWithColor(e.getPlayer())
                                     .append(Component.text(e.getPlayer().getName()))
                                     .append(Component.text(" left the lobby!", NamedTextColor.GOLD))
            );
        }
        if (e.getPlayer().getVehicle() != null) {
            e.getPlayer().getVehicle().remove();
        }
        //removing player position boards
        StatsLeaderboardManager.removePlayerSpecificHolograms(e.getPlayer());

        Bukkit.getOnlinePlayers().forEach(p -> {
            p.showTitle(Title.title(
                    Component.text("     Welcome to ", NamedTextColor.AQUA)
                             .append(Component.text("Warlords 2.0     ", NamedTextColor.YELLOW, TextDecoration.BOLD)),
                    Component.text("Players Online: ", NamedTextColor.GREEN)
                             .append(Component.text(Bukkit.getOnlinePlayers().size() - 1, NamedTextColor.GRAY))
            ));
        });

        for (GameManager.GameHolder holder : Warlords.getGameManager().getGames()) {
            if (
                    holder.getGame() != null
                            && holder.getGame().hasPlayer(e.getPlayer().getUniqueId())
                            && holder.getGame().getPlayerTeam(e.getPlayer().getUniqueId()) == null
            ) {
                holder.getGame().removePlayer(e.getPlayer().getUniqueId());
            }
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock) {
            if (entityList.remove(event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        Entity attacker = e.getDamager();
        WarlordsEntity wpAttacker = Warlords.getPlayer(attacker);
        WarlordsEntity wpVictim = Warlords.getPlayer(e.getEntity());
        e.setCancelled(true);
        if (wpAttacker == null || wpVictim == null || !wpAttacker.isEnemyAlive(wpVictim) || wpAttacker.getGame().isFrozen()) {
            return;
        }
        if ((attacker instanceof Player && ((Player) attacker).getInventory().getHeldItemSlot() != 0) || wpAttacker.getHitCooldown() != 0) {
            return;
        }

        wpAttacker.setHitCooldown(12);
        wpAttacker.subtractEnergy(-wpAttacker.getSpec().getEnergyPerHit(), false);
        wpAttacker.getMinuteStats().addMeleeHits();

        if (wpAttacker.getSpec() instanceof Spiritguard && wpAttacker.getCooldownManager().hasCooldown(Soulbinding.class)) {
            Soulbinding baseSoulBinding = (Soulbinding) wpAttacker.getPurpleAbility();
            new CooldownFilter<>(wpAttacker, PersistentCooldown.class)
                    .filter(PersistentCooldown::isShown)
                    .filterCooldownClassAndMapToObjectsOfClass(Soulbinding.class)
                    .forEachOrdered(soulbinding -> {
                        wpAttacker.doOnStaticAbility(Soulbinding.class, Soulbinding::addPlayersBinded);
                        if (soulbinding.hasBoundPlayer(wpVictim)) {
                            soulbinding.getSoulBindedPlayers().stream()
                                       .filter(p -> p.getBoundPlayer() == wpVictim)
                                       .forEach(boundPlayer -> {
                                           boundPlayer.setHitWithSoul(false);
                                           boundPlayer.setHitWithLink(false);
                                           boundPlayer.setTimeLeft(baseSoulBinding.getBindDuration());
                                       });
                        } else {
                            wpVictim.sendMessage(WarlordsEntity.RECEIVE_ARROW_RED
                                    .append(Component.text("You have been bound by " + wpAttacker.getName() + "'s ", NamedTextColor.GRAY))
                                    .append(Component.text("Soulbinding Weapon", NamedTextColor.LIGHT_PURPLE))
                                    .append(Component.text("!", NamedTextColor.GRAY))
                            );
                            wpAttacker.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                                    .append(Component.text("Your ", NamedTextColor.GRAY))
                                    .append(Component.text("Soulbinding Weapon", NamedTextColor.LIGHT_PURPLE))
                                    .append(Component.text(" has bound " + wpVictim.getName() + "!", NamedTextColor.GRAY))
                            );
                            soulbinding.getSoulBindedPlayers().add(new Soulbinding.SoulBoundPlayer(wpVictim, baseSoulBinding.getBindDuration()));
                            Utils.playGlobalSound(wpVictim.getLocation(), "shaman.earthlivingweapon.activation", 2, 1);
                        }
                    });
        }

        if (wpAttacker instanceof WarlordsNPC warlordsNPC) {
            if (!warlordsNPC.getCooldownManager().hasCooldown(SoulShackle.class)) {
                if (!(warlordsNPC.getMinMeleeDamage() == 0)) {
                    wpVictim.addDamageInstance(
                            wpAttacker,
                            "",
                            warlordsNPC.getMinMeleeDamage(),
                            warlordsNPC.getMaxMeleeDamage(),
                            0,
                            100,
                            false
                    );
                }
                wpAttacker.setHitCooldown(20);
            }
        } else {
            if (wpAttacker instanceof WarlordsPlayer && ((WarlordsPlayer) wpAttacker).getWeapon() != null) {
                AbstractWeapon weapon = ((WarlordsPlayer) wpAttacker).getWeapon();
                wpVictim.addDamageInstance(
                        wpAttacker,
                        "",
                        weapon.getMeleeDamageMin(),
                        weapon.getMeleeDamageMax(),
                        weapon.getCritChance(),
                        weapon.getCritMultiplier(),
                        false
                );
            } else {
                wpVictim.addDamageInstance(
                        wpAttacker,
                        "",
                        132,
                        179,
                        25,
                        200,
                        false
                );
            }
        }
        wpVictim.updateHealth();

        if (wpVictim.getCooldownManager().hasCooldown(IceBarrier.class)) {
            wpAttacker.addSpeedModifier(wpVictim, "Ice Barrier", -20, 2 * 20);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Action action = e.getAction();
        WarlordsEntity wp = Warlords.getPlayer(player);

        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
            ItemStack itemHeld = player.getEquipment().getItemInMainHand();
            int heldItemSlot = player.getInventory().getHeldItemSlot();
            if (wp != null && wp.isAlive() && !wp.getGame().isFrozen()) {
                if (itemHeld.getType().name().endsWith("_BANNER")) {
                    if (wp.getFlagDropCooldown() > 0) {
                        player.sendMessage(Component.text("You cannot drop the flag yet, please wait 3 seconds!", NamedTextColor.RED));
                    } else if (wp.getCooldownManager().hasCooldownExtends(AbstractTimeWarpBase.class)) {
                        player.sendMessage(Component.text("You cannot drop the flag with a Time Warp active!", NamedTextColor.RED));
                    } else {
                        FlagHolder.dropFlagForPlayer(wp);
                        wp.setFlagDropCooldown(5);
                    }
                    return;
                }
                switch (itemHeld.getType()) {
                    case BONE -> {
                        if (!itemHeld.equals(UndyingArmy.BONE)) {
                            break;
                        }
                        player.getInventory().remove(UndyingArmy.BONE);
                        wp.addDamageInstance(
                                Warlords.getPlayer(player),
                                "",
                                100000,
                                100000,
                                0,
                                100,
                                false
                        );
                    }
                    case COMPASS -> {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                        wp.toggleTeamFlagCompass();
                    }
                    case GOLD_NUGGET -> {
                        player.playSound(player.getLocation(), Sound.BLOCK_SNOW_BREAK, 500, 2);
                        ((WarlordsPlayer) wp).getAbilityTree().openAbilityTree();
                    }
                    default -> {
                        if (heldItemSlot == 0 || PlayerSettings.getPlayerSettings(wp.getUuid()).getHotkeyMode() == Settings.HotkeyMode.CLASSIC_MODE) {
                            if (heldItemSlot == 8 && wp instanceof WarlordsPlayer warlordsPlayer) {
                                AbstractWeapon weapon = warlordsPlayer.getWeapon();
                                if (weapon instanceof AbstractLegendaryWeapon) {
                                    ((AbstractLegendaryWeapon) weapon).activateAbility(warlordsPlayer, player, false);
                                }
                            } else {
                                wp.getSpec().onRightClick(wp, player, heldItemSlot, false);
                            }
                        }
                    }
                }
            } else {
                Warlords.getGameManager().getPlayerGame(player.getUniqueId())
                        .flatMap(g -> g.getState(PreLobbyState.class))
                        .ifPresent(state -> state.interactEvent(player, heldItemSlot));
            }
        } else if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {
            if (action == Action.LEFT_CLICK_AIR) {

            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked().getType() != EntityType.ARMOR_STAND) {
            return;
        }
        Player player = e.getPlayer();
        WarlordsEntity wp = Warlords.getPlayer(player);
        if (wp == null) {
            return;
        }
        int heldItemSlot = player.getInventory().getHeldItemSlot();
        if (heldItemSlot == 0 || PlayerSettings.getPlayerSettings(wp.getUuid()).getHotkeyMode() == Settings.HotkeyMode.CLASSIC_MODE) {
            if (heldItemSlot == 8 && wp instanceof WarlordsPlayer warlordsPlayer) {
                AbstractWeapon weapon = warlordsPlayer.getWeapon();
                if (weapon instanceof AbstractLegendaryWeapon) {
                    ((AbstractLegendaryWeapon) weapon).activateAbility(warlordsPlayer, player, false);
                }
            } else {
                wp.getSpec().onRightClick(wp, player, heldItemSlot, false);
            }
        }
    }

//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void onPlayerInteractEntity(PlayerInteractAtEntityEvent e) {
//        System.out.println("PlayerInteractAtEntityEvent");
//        System.out.println(e.isCancelled());
//        e.setCancelled(true);
////        if(e.getRightClicked().getType() == EntityType.WOLF) {
////            e.setCancelled(true);
////        }
//        System.out.println(e.isCancelled());
//    }
//
//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
//        System.out.println("PlayerInteractEntityEvent");
//        System.out.println(e.isCancelled());
//        e.setCancelled(true);
////        if(e.getRightClicked().getType() == EntityType.WOLF) {
////            e.setCancelled(true);
////        }
//        System.out.println(e.isCancelled());
//    }
//
//    @EventHandler
//    public void onPlayerConsumeEvent(PlayerItemConsumeEvent e) {
//        System.out.println("PlayerItemConsumeEvent");
//    }
//
//    @EventHandler
//    public void onEntityInteractEvent(EntityInteractEvent e) {
//        System.out.println("EntityInteractEvent");
//    }

    @EventHandler
    public void onDismount(VehicleExitEvent e) {
        e.getVehicle().remove();
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.UNKNOWN) {
            WarlordsEntity warlordsPlayer = Warlords.getPlayer(e.getPlayer().getUniqueId());
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
        Player player = e.getPlayer();
        WarlordsEntity wp = Warlords.getPlayer(player);
        if (wp != null) {
            boolean hotkeyMode = PlayerSettings.getPlayerSettings(wp.getUuid()).getHotkeyMode() == Settings.HotkeyMode.NEW_MODE;
            if (hotkeyMode) {
                if (slot == 1 || slot == 2 || slot == 3 || slot == 4) {
                    wp.getSpec().onRightClick(wp, player, slot, true);
                    e.setCancelled(true);
                } else if (slot == 8 && wp instanceof WarlordsPlayer warlordsPlayer) {
                    AbstractWeapon weapon = warlordsPlayer.getWeapon();
                    if (weapon instanceof AbstractLegendaryWeapon) {
                        AbstractAbility ability = ((AbstractLegendaryWeapon) weapon).getAbility();
                        if (ability != null) {
                            ((AbstractLegendaryWeapon) weapon).activateAbility(warlordsPlayer, player, true);
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (e.getWhoClicked().getGameMode() != GameMode.CREATIVE) {
            e.setCancelled(true);
        }
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
    public void onPlayerDropEvent(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onHorseJump(HorseJumpEvent e) {
        List<Entity> passengers = e.getEntity().getPassengers();
        if (passengers.isEmpty()) {
            return;
        }
        WarlordsEntity warlordsEntity = Warlords.getPlayer(passengers.get(0).getUniqueId());
        if (warlordsEntity != null) {
            if (warlordsEntity.getGame().isFrozen()) {
                e.setCancelled(true); //TODO
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

        WarlordsEntity warlordsEntity = Warlords.getPlayer(e.getPlayer());
        if (warlordsEntity != null) {
            warlordsEntity.setCurrentVector(e.getTo().toVector().subtract(e.getFrom().toVector()).normalize().clone());
            //System.out.println(warlordsEntity.getCurrentVector());
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                e.getEntity().teleport(Warlords.getRejoinPoint(e.getEntity().getUniqueId()));
                WarlordsEntity wp = Warlords.getPlayer(e.getEntity());
                if (wp != null) {
                    if (wp.isDead()) {
                        wp.getEntity().teleport(wp.getLocation().clone().add(0, 100, 0));
                    } else {
                        wp.addDamageInstance(wp, "Fall", 1000000, 1000000, 0, 100, false);
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
                    WarlordsEntity wp = Warlords.getPlayer(e.getEntity());
                    if (wp != null) {
                        int damage = (int) e.getDamage();
                        if (damage > 5) {
                            wp.addDamageInstance(wp, "Fall", ((damage + 3) * 40 - 200), ((damage + 3) * 40 - 200), 0, 100, false);
                            wp.resetRegenTimer();
                        }
                    }
                }
            } else if (e.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
                //100 flat
                if (e.getEntity() instanceof Player) {
                    WarlordsEntity wp = Warlords.getPlayer(e.getEntity());
                    if (wp != null && !wp.getGame().isFrozen()) {
                        wp.addDamageInstance(wp, "Fall", 100, 100, 0, 100, false);
                        wp.resetRegenTimer();
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

    @EventHandler
    public void chat(AsyncChatEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        if (MuteCommand.MUTED_PLAYERS.getOrDefault(uuid, false)) {
            e.setCancelled(true);
            return;
        }

        if (!ChatChannels.PLAYER_CHAT_CHANNELS.containsKey(uuid) || ChatChannels.PLAYER_CHAT_CHANNELS.get(uuid) == null) {
            ChatChannels.PLAYER_CHAT_CHANNELS.put(uuid, ChatChannels.ALL);
        }

        Component prefixWithColor = Permissions.getPrefixWithColor(player);
        if (Objects.requireNonNull(prefixWithColor.color()).value() == NamedTextColor.WHITE.value()) {
            ChatUtils.MessageTypes.WARLORDS.sendErrorMessage("Player has invalid rank or permissions have not been set up properly!");
        }

        ChatChannels channel = ChatChannels.PLAYER_CHAT_CHANNELS.getOrDefault(uuid, ChatChannels.ALL);
        channel.onPlayerChatEvent(e, prefixWithColor);
    }

    @EventHandler
    public void onPlayerVelocity(PlayerVelocityEvent event) {
        Player player = event.getPlayer();
        EntityDamageEvent lastDamage = player.getLastDamageCause();

        if ((!(lastDamage instanceof EntityDamageByEntityEvent))) {
            return;
        }

        if ((((EntityDamageByEntityEvent) lastDamage).getDamager() instanceof Player)) {
            event.setCancelled(true);
        }
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
    public void onBlockPhysics(BlockPhysicsEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onFlagChange(WarlordsFlagUpdatedEvent event) {
        Game game = event.getGame();
        FlagLocation eventNew = event.getNew();
        FlagLocation eventOld = event.getOld();
        Team eventTeam = event.getTeam();
        NamedTextColor teamColor = eventTeam.teamColor();
        Component coloredPrefix = eventTeam.coloredPrefix();

        if (eventOld instanceof PlayerFlagLocation) {
            ((PlayerFlagLocation) eventOld).getPlayer().setCarriedFlag(null);
        }

        if (eventNew instanceof PlayerFlagLocation pfl) {
            WarlordsEntity player = pfl.getPlayer();
            player.setCarriedFlag(event.getInfo());
            //removing invis for assassins
            OrderOfEviscerate.removeCloak(player, false);
            if (eventOld instanceof PlayerFlagLocation) {
                // PLAYER -> PLAYER only happens if the multiplier gets to a new scale
                int computedHumanMultiplier = pfl.getComputedHumanMultiplier();
                if (computedHumanMultiplier % 10 == 0) {
                    game.forEachOnlinePlayer((p, t) -> {
                        PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(p);
                        if (t != null && playerSettings.getFlagMessageMode() == Settings.FlagMessageMode.RELATIVE) {
                            NamedTextColor playerColor = pfl.getPlayer().getTeam().teamColor;
                            if (t != eventTeam) {
                                p.sendMessage(Component.text("", NamedTextColor.YELLOW)
                                                       .append(Component.text("YOUR", playerColor))
                                                       .append(Component.text(" flag carrier now takes "))
                                                       .append(Component.text(computedHumanMultiplier + "%", NamedTextColor.RED))
                                                       .append(Component.text(" increased damage!"))
                                );
                            } else {
                                p.sendMessage(Component.text("The ", NamedTextColor.YELLOW)
                                                       .append(Component.text("ENEMY", playerColor))
                                                       .append(Component.text(" flag carrier now takes "))
                                                       .append(Component.text(computedHumanMultiplier + "%", NamedTextColor.RED))
                                                       .append(Component.text(" increased damage!"))
                                );
                            }
                        } else {
                            p.sendMessage(Component.text("The ", NamedTextColor.YELLOW)
                                                   .append(coloredPrefix)
                                                   .append(Component.text(" flag carrier now takes "))
                                                   .append(Component.text(computedHumanMultiplier + "%", NamedTextColor.RED))
                                                   .append(Component.text(" increased damage!"))
                            );
                        }
                    });
                }
            } else {
                // eg GROUND -> PLAYER
                // or SPAWN -> PLAYER
                game.forEachOnlinePlayer((p, t) -> {
                    PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(p);
                    Component playerColoredName = player.getColoredName();
                    Component flagMessage = Component.text("", NamedTextColor.YELLOW)
                                                     .append(playerColoredName)
                                                     .append(Component.text(" picked up the "))
                                                     .append(coloredPrefix)
                                                     .append(Component.text(" §eflag!"));
                    if (t != null) {
                        if (t == eventTeam) {
                            p.playSound(player.getLocation(), "ctf.friendlyflagtaken", 500, 1);
                            if (playerSettings.getFlagMessageMode() == Settings.FlagMessageMode.RELATIVE) {
                                flagMessage = Component.text("", NamedTextColor.YELLOW)
                                                       .append(playerColoredName)
                                                       .append(Component.text(" picked up "))
                                                       .append(Component.text("YOUR", teamColor))
                                                       .append(Component.text(" flag!"));
                            }
                        } else {
                            p.playSound(player.getLocation(), "ctf.enemyflagtaken", 500, 1);
                            if (playerSettings.getFlagMessageMode() == Settings.FlagMessageMode.RELATIVE) {
                                flagMessage = Component.text("", NamedTextColor.YELLOW)
                                                       .append(playerColoredName)
                                                       .append(Component.text(" picked up the "))
                                                       .append(Component.text("ENEMY", teamColor))
                                                       .append(Component.text(" flag!"));
                            }
                        }
                    }
                    p.sendMessage(flagMessage);
                    p.showTitle(Title.title(
                            Component.empty(),
                            flagMessage,
                            Title.Times.times(Ticks.duration(0), Ticks.duration(60), Ticks.duration(0))
                    ));

                });
            }
        } else if (eventNew instanceof SpawnFlagLocation) {
            WarlordsEntity toucher = ((SpawnFlagLocation) eventNew).getFlagReturner();
            if (eventOld instanceof GroundFlagLocation) {
                if (toucher != null) {
                    toucher.addFlagReturn();
                    game.forEachOnlinePlayer((p, t) -> {
                        boolean sameTeam = t == eventTeam;
                        PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(p);
                        Component toucherColoredName = toucher.getColoredName();
                        Component flagMessage = Component.text("", NamedTextColor.YELLOW)
                                                         .append(toucherColoredName)
                                                         .append(Component.text(" has returned the "))
                                                         .append(coloredPrefix)
                                                         .append(Component.text(" flag!"));
                        if (playerSettings.getFlagMessageMode() == Settings.FlagMessageMode.RELATIVE) {
                            if (sameTeam) {
                                flagMessage = Component.text("", NamedTextColor.YELLOW)
                                                       .append(toucherColoredName)
                                                       .append(Component.text(" has returned "))
                                                       .append(Component.text("YOUR", teamColor))
                                                       .append(Component.text(" flag!"));
                            } else {
                                flagMessage = Component.text("", NamedTextColor.YELLOW)
                                                       .append(toucherColoredName)
                                                       .append(Component.text(" has returned the "))
                                                       .append(Component.text("ENEMY", teamColor))
                                                       .append(Component.text(" flag!"));
                            }
                        }
                        p.sendMessage(flagMessage);
                        p.showTitle(Title.title(
                                Component.empty(),
                                flagMessage,
                                Title.Times.times(Ticks.duration(0), Ticks.duration(60), Ticks.duration(0))
                        ));

                        if (sameTeam) {
                            p.playSound(p.getLocation(), "ctf.flagreturned", 500, 1);
                        }
                    });
                } else {
                    game.forEachOnlinePlayer((p, t) -> {
                        PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(p);
                        if (playerSettings.getFlagMessageMode() == Settings.FlagMessageMode.RELATIVE) {
                            if (t == eventTeam) {
                                p.sendMessage(Component.text("", NamedTextColor.YELLOW)
                                                       .append(Component.text("YOUR", teamColor))
                                                       .append(Component.text(" flag has returned to base!"))
                                );
                            } else {
                                p.sendMessage(Component.text("The ", NamedTextColor.YELLOW)
                                                       .append(Component.text("ENEMY", teamColor))
                                                       .append(Component.text(" flag has returned to base!"))
                                );
                            }
                        } else {
                            p.sendMessage(Component.text("The ", NamedTextColor.YELLOW)
                                                   .append(coloredPrefix)
                                                   .append(Component.text(" flag has returned to base!"))
                            );
                        }
                    });
                }
            }
        } else if (eventNew instanceof GroundFlagLocation) {
            if (eventOld instanceof PlayerFlagLocation pfl) {
                pfl.getPlayer().updateArmor();
                game.forEachOnlinePlayer((p, t) -> {
                    PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(p);
                    Component coloredName = pfl.getPlayer().getColoredName();
                    Component flagMessage = Component.text("", NamedTextColor.YELLOW)
                                                     .append(coloredName)
                                                     .append(Component.text(" has dropped the "))
                                                     .append(coloredPrefix)
                                                     .append(Component.text(" flag!"));
                    if (playerSettings.getFlagMessageMode() == Settings.FlagMessageMode.RELATIVE) {
                        if (t == eventTeam) {
                            flagMessage = Component.text("", NamedTextColor.YELLOW)
                                                   .append(coloredName)
                                                   .append(Component.text(" has dropped "))
                                                   .append(Component.text("YOUR", teamColor))
                                                   .append(Component.text(" flag!"));
                        } else {
                            flagMessage = Component.text("", NamedTextColor.YELLOW)
                                                   .append(coloredName)
                                                   .append(Component.text(" has dropped the "))
                                                   .append(Component.text("ENEMY", teamColor))
                                                   .append(Component.text(" flag!"));
                        }
                    }
                    p.sendMessage(flagMessage);
                    p.showTitle(Title.title(
                            Component.empty(),
                            flagMessage,
                            Title.Times.times(Ticks.duration(0), Ticks.duration(60), Ticks.duration(0))
                    ));
                });
            }
        } else if (eventNew instanceof WaitingFlagLocation && ((WaitingFlagLocation) eventNew).getScorer() != null) {
            WarlordsEntity player = ((WaitingFlagLocation) eventNew).getScorer();
            player.addFlagCap();
            game.forEachOnlinePlayer((p, t) -> {
                boolean sameTeam = t == eventTeam;
                PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(p);
                Component coloredName = player.getColoredName();
                Component flagMessage = Component.text("", NamedTextColor.YELLOW)
                                                 .append(coloredName)
                                                 .append(Component.text(" has captured the "))
                                                 .append(coloredPrefix)
                                                 .append(Component.text(" flag!"));
                if (playerSettings.getFlagMessageMode() == Settings.FlagMessageMode.RELATIVE) {
                    if (sameTeam) {
                        flagMessage = Component.text("", NamedTextColor.YELLOW)
                                               .append(coloredName)
                                               .append(Component.text(" has captured "))
                                               .append(Component.text("YOUR", teamColor))
                                               .append(Component.text(" flag!"));
                    } else {
                        flagMessage = Component.text("", NamedTextColor.YELLOW)
                                               .append(coloredName)
                                               .append(Component.text(" has captured the "))
                                               .append(Component.text("ENEMY", teamColor))
                                               .append(Component.text(" flag!"));
                    }
                }
                p.sendMessage(flagMessage);
                p.showTitle(Title.title(
                        Component.empty(),
                        flagMessage,
                        Title.Times.times(Ticks.duration(0), Ticks.duration(60), Ticks.duration(0))
                ));

                if (t != null) {
                    if (sameTeam) {
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

    public boolean dropFlag(Player player) {
        return dropFlag(Warlords.getPlayer(player));
    }

    public boolean dropFlag(@Nullable WarlordsEntity player) {
        if (player == null) {
            return false;
        }
        FlagHolder.dropFlagForPlayer(player);
        return true;
    }

    @EventHandler
    public void onPlayerDeath(WarlordsDeathEvent event) {
        dropFlag(event.getWarlordsEntity());
    }
}
