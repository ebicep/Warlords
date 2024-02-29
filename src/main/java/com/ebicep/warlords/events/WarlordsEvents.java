package com.ebicep.warlords.events;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.IceBarrier;
import com.ebicep.warlords.abilities.SoulShackle;
import com.ebicep.warlords.abilities.UndyingArmy;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractTimeWarp;
import com.ebicep.warlords.commands.debugcommands.misc.AdminCommand;
import com.ebicep.warlords.commands.debugcommands.misc.MuteCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.FutureMessage;
import com.ebicep.warlords.events.player.DatabasePlayerFirstLoadEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameManager;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.game.state.PreLobbyState;
import com.ebicep.warlords.menu.PlayerHotBarItemListener;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.player.general.CustomScoreboard;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.general.Settings;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.flags.Unsilencable;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.chat.ChatUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.potion.PotionEffectType;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class WarlordsEvents implements Listener {

    @EventHandler
    public static void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (Bukkit.hasWhitelist() && Bukkit.getWhitelistedPlayers().stream().noneMatch(p -> p.getUniqueId().equals(event.getUniqueId()))) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, Component.text("The server is currently under maintenance!"));
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
                    if (databasePlayer.getName() == null || !Objects.equals(databasePlayer.getName(), event.getName())) {
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
            wp.getGame().getState().onPlayerReJoinGame(player);
            e.joinMessage(Component.textOfChildren(
                            wp.getColoredNameBold(),
                            Component.text(" rejoined the game!", NamedTextColor.GOLD)
                    )
            );
        } else {
            player.setAllowFlight(true);
            e.joinMessage(Permissions.getPrefixWithColor(player, false)
                                     .append(Component.text(player.getName()))
                                     .append(Component.text(" joined the lobby!", NamedTextColor.GOLD))
            );
            ChatUtils.sendCenteredMessage(player, Component.text("-----------------------------------------------------", NamedTextColor.GRAY));
            ChatUtils.sendCenteredMessage(player, Component.textOfChildren(
                    Component.text("Welcome to Warlords 2.0 ", NamedTextColor.GOLD, TextDecoration.BOLD),
                    Component.text("(", NamedTextColor.GRAY),
                    Component.text(Warlords.VERSION, Warlords.VERSION_COLOR),
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
            ChatUtils.sendCenteredMessage(player, Component.text("https://docs.flairy.me/index.html", NamedTextColor.RED)
                                                           .clickEvent(ClickEvent.openUrl("https://docs.flairy.me/index.html")));
            ChatUtils.sendCenteredMessage(player, Component.text("https://ojagerl.nl/", NamedTextColor.RED)
                                                           .clickEvent(ClickEvent.openUrl("https://ojagerl.nl/")));
            ChatUtils.sendCenteredMessage(player, Component.empty());
            ChatUtils.sendCenteredMessage(player,
                    Component.text("Discord: ", NamedTextColor.GOLD).append(Component.text("discord.gg/GWPAx9sEG7", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD)
                                                                                     .clickEvent(ClickEvent.openUrl("https://discord.gg/GWPAx9sEG7")))
            );
            ChatUtils.sendCenteredMessage(player,
                    Component.text("Resource Pack: ", NamedTextColor.GOLD).append(Component.text("https://bit.ly/47lZHGz", NamedTextColor.GREEN, TextDecoration.BOLD)
                                                                                           .clickEvent(ClickEvent.openUrl("https://bit.ly/47lZHGz")))
            );
            ChatUtils.sendCenteredMessage(player, Component.text("-----------------------------------------------------", NamedTextColor.GRAY));
        }

        CustomScoreboard customScoreboard = CustomScoreboard.getPlayerScoreboard(player);
        player.setScoreboard(customScoreboard.getScoreboard());
        joinInteraction(player, false);

        sendHeaderFooterToAll(false);
        Warlords.getGameManager().dropPlayerFromQueueOrGames(e.getPlayer());
    }

    public static void joinInteraction(Player player, boolean fromGame) {
        player.playerListName(null);
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (attribute != null) {
            attribute.setBaseValue(1024); // remove attack charge up / recoil
        }
        attribute = player.getAttribute(Attribute.GENERIC_MAX_ABSORPTION);
        if (attribute != null) {
            attribute.setBaseValue(Integer.MAX_VALUE); // give absorption capability
        }
        UUID uuid = player.getUniqueId();
        Location rejoinPoint = Warlords.getRejoinPoint(uuid);
        boolean isSpawnWorld = Objects.requireNonNull(StatsLeaderboardManager.MAIN_LOBBY).equals(rejoinPoint.getWorld());
        boolean playerIsInWrongWorld = !player.getWorld().getName().equals(rejoinPoint.getWorld().getName());
        if ((!fromGame && isSpawnWorld) || playerIsInWrongWorld) {
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

            List<BossBar> bossBars = new ArrayList<>();
            player.activeBossBars().forEach(bossBars::add);
            bossBars.forEach(player::hideBossBar);

            player.setGameMode(GameMode.ADVENTURE);
            player.setMaxHealth(20);
            player.setHealth(20);
            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});
            PlayerHotBarItemListener.giveLobbyHotBar(player, fromGame);

            DatabaseManager.getPlayer(uuid, databasePlayer -> {
                if (fromGame) {
                    ExperienceManager.checkForPrestige(player, uuid, databasePlayer);
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
            player.playerListName(null);
        }

        Warlords.getInstance().hideAndUnhidePeople(player);
    }

    private static void sendHeaderFooterToAll(boolean left) {
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.sendPlayerListHeaderAndFooter(
                    Component.textOfChildren(

                            Component.text("Welcome to ", NamedTextColor.AQUA),
                            Component.text("Warlords 2.0", NamedTextColor.YELLOW, TextDecoration.BOLD)
                    ),
                    Component.textOfChildren(
                            Component.text("COMPWL.APEXMC.CO", NamedTextColor.RED, TextDecoration.BOLD),
                            Component.newline(),
                            Component.text("Players Online: ", NamedTextColor.GREEN),
                            Component.text(Bukkit.getOnlinePlayers().size() - (left ? 1 : 0), NamedTextColor.GRAY)
                    )
            );
        });
    }

    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent e) {
        WarlordsEntity wp1 = Warlords.getPlayer(e.getPlayer());
        WarlordsPlayer wp = wp1 instanceof WarlordsPlayer ? (WarlordsPlayer) wp1 : null;
        if (wp != null) {
            wp.updatePlayerReference(null);
            e.quitMessage(Component.textOfChildren(
                            wp.getColoredNameBold(),
                            Component.text(" left the game!", NamedTextColor.GOLD)
                    )
            );
        } else {
            e.quitMessage(Permissions.getPrefixWithColor(e.getPlayer(), false)
                                     .append(Component.text(e.getPlayer().getName()))
                                     .append(Component.text(" left the lobby!", NamedTextColor.GOLD))
            );
        }
        if (e.getPlayer().getVehicle() != null) {
            e.getPlayer().getVehicle().remove();
        }
        //removing player position boards
        StatsLeaderboardManager.removePlayerSpecificHolograms(e.getPlayer());

        sendHeaderFooterToAll(true);

        for (GameManager.GameHolder holder : Warlords.getGameManager().getGames()) {
            Game game = holder.getGame();
            if (game != null
                    && game.hasPlayer(e.getPlayer().getUniqueId())
                    && ((game.isState(PreLobbyState.class) && !game.getAddons().contains(GameAddon.PRIVATE_GAME))
                    || game.getPlayerTeam(e.getPlayer().getUniqueId()) == null)
            ) {
                game.removePlayer(e.getPlayer().getUniqueId());
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
        if ((attacker instanceof Player && ((Player) attacker).getInventory().getHeldItemSlot() != 0) || wpAttacker.getHitCooldown() > 0) {
            return;
        }

        wpAttacker.setHitCooldown(wpAttacker.getBaseHitCooldownValue());
        float energyPerHit = wpAttacker.getSpec().getEnergyPerHit();
        for (AbstractCooldown<?> abstractCooldown : wpAttacker.getCooldownManager().getCooldownsDistinct()) {
            energyPerHit = abstractCooldown.addEnergyPerHit(wpAttacker, energyPerHit);
        }
        wpAttacker.addEnergy(wpAttacker, null, energyPerHit);
        wpAttacker.getMinuteStats().addMeleeHits();

        if (wpAttacker instanceof WarlordsNPC warlordsNPC) {
            if (!warlordsNPC.getCooldownManager().hasCooldown(SoulShackle.class) && !(warlordsNPC.getMob() instanceof Unsilencable)) {
                if (!(warlordsNPC.getMinMeleeDamage() == 0)) {
                    wpVictim.addDamageInstance(
                            wpAttacker,
                            "",
                            warlordsNPC.getMinMeleeDamage(),
                            warlordsNPC.getMaxMeleeDamage(),
                            warlordsNPC.getMeleeCritChance(),
                            warlordsNPC.getMeleeCritMultiplier(),
                            EnumSet.of(InstanceFlags.NO_HIT_SOUND)
                    );
                }
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
                        EnumSet.of(InstanceFlags.NO_HIT_SOUND)
                );
            } else {
                wpVictim.addDamageInstance(
                        wpAttacker,
                        "",
                        132,
                        179,
                        25,
                        200,
                        EnumSet.of(InstanceFlags.NO_HIT_SOUND)
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
                    } else if (wp.getCooldownManager().hasCooldownExtends(AbstractTimeWarp.class)) {
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
                                100
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
                    default -> DatabaseManager.getPlayer(wp.getUuid(), databasePlayer -> {
                        if (heldItemSlot == 0 || databasePlayer.getHotkeyMode() == Settings.HotkeyMode.CLASSIC_MODE) {
                            if (heldItemSlot == 8 && wp instanceof WarlordsPlayer warlordsPlayer) {
                                AbstractWeapon weapon = warlordsPlayer.getWeapon();
                                if (weapon instanceof AbstractLegendaryWeapon) {
                                    ((AbstractLegendaryWeapon) weapon).activateAbility(warlordsPlayer, player, false);
                                }
                            } else {
                                wp.getSpec().onRightClick(wp, player, heldItemSlot, false);
                            }
                        }
                    });
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
        if (!AdminCommand.BYPASS_INTERACT_CANCEL.contains(player.getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked() instanceof Wolf) {
            e.setCancelled(true);
        }
        if (e.getRightClicked().getType() != EntityType.ARMOR_STAND) {
            return;
        }
        Player player = e.getPlayer();
        WarlordsEntity wp = Warlords.getPlayer(player);
        if (wp == null) {
            return;
        }
        int heldItemSlot = player.getInventory().getHeldItemSlot();
        DatabaseManager.getPlayer(wp.getUuid(), databasePlayer -> {
            if (heldItemSlot == 0 || databasePlayer.getHotkeyMode() == Settings.HotkeyMode.CLASSIC_MODE) {
                if (heldItemSlot == 8 && wp instanceof WarlordsPlayer warlordsPlayer) {
                    AbstractWeapon weapon = warlordsPlayer.getWeapon();
                    if (weapon instanceof AbstractLegendaryWeapon) {
                        ((AbstractLegendaryWeapon) weapon).activateAbility(warlordsPlayer, player, false);
                    }
                } else {
                    wp.getSpec().onRightClick(wp, player, heldItemSlot, false);
                }
            }
        });
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) {
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
    public void switchItemHeld(PlayerItemHeldEvent e) {
        int slot = e.getNewSlot();
        Player player = e.getPlayer();
        if (player.getInventory().getItem(slot) == null) {
            return;
        }
        WarlordsEntity wp = Warlords.getPlayer(player);
        if (wp == null) {
            return;
        }
        List<AbstractAbility> abilities = wp.getAbilities();
        DatabaseManager.getPlayer(wp.getUuid(), databasePlayer -> {
            if (databasePlayer.getHotkeyMode() == Settings.HotkeyMode.NEW_MODE) {
                if (1 <= slot && slot <= 4 && slot <= abilities.size()) {
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
        });
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (e.getPlayer().getVehicle() instanceof Horse) {
            Location location = e.getPlayer().getLocation();
            if (!LocationUtils.isMountableZone(location)) {
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
    public void onPlayerJump(PlayerJumpEvent event) {
        Player player = event.getPlayer();
        WarlordsEntity warlordsEntity = Warlords.getPlayer(player);
        if (warlordsEntity != null) {
            warlordsEntity.getMinuteStats().addJumps();
        }
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

        Component prefixWithColor = Permissions.getPrefixWithColor(player, false);
        if (Objects.requireNonNull(prefixWithColor.color()).value() == NamedTextColor.WHITE.value()) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage("Player has invalid rank or permissions have not been set up properly!");
        }

        ChatChannels channel = ChatChannels.PLAYER_CHAT_CHANNELS.getOrDefault(uuid, ChatChannels.ALL);
        ChatChannels.playerSendMessage(player, channel, e.message());
        e.setCancelled(true);
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

}
