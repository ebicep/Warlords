package com.ebicep.warlords.game.state;

import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.PreGameItemOption;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.player.general.CustomScoreboard;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.sr.Balancer;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.DateUtil;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ebicep.warlords.util.chat.ChatUtils.sendMessage;

public class PreLobbyState implements State, TimerDebugAble {

    private final Game game;
    private final PreGameItemOption[] items = new PreGameItemOption[9];
    private int timer = 0;
    private int maxTimer = 0;
    private boolean timerHasBeenSkipped = false;

    public PreLobbyState(Game game) {
        this.game = game;
    }

    @Override
    public void begin() {
        if (game.getGameMode() == com.ebicep.warlords.game.GameMode.TUTORIAL) {
            return;
        }
        this.maxTimer = game.getMap().getLobbyCountdown();
        this.resetTimer();
        game.setAcceptsPlayers(true);
        game.setAcceptsSpectators(false);
        for (Option option : game.getOptions()) {
            if (option instanceof PreGameItemOption preGameItemOption) {
                items[preGameItemOption.getSlot()] = preGameItemOption;
            }
        }
    }

    @Override
    public State run() {
        if (game.getGameMode() == com.ebicep.warlords.game.GameMode.TUTORIAL) {
            return new SyncTimerState(game);
        }
        if (hasEnoughPlayers() || timerHasBeenSkipped) {
            timerHasBeenSkipped = false;
            if (timer % 20 == 0) {
                int time = timer / 20;
                game.forEachOnlinePlayerWithoutSpectators((player, team) -> {
                    giveLobbyScoreboard(false, player);
                    player.setAllowFlight(false);
                });
                if (time == 30) {
                    game.forEachOnlinePlayerWithoutSpectators((player, team) -> {
                        sendMessage(player, false, Component.text("The game starts in ", NamedTextColor.YELLOW)
                                                            .append(Component.text("30 ", NamedTextColor.GREEN))
                                                            .append(Component.text("seconds!"))
                        );
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                    });
                } else if (time == 20) {
                    game.forEachOnlinePlayerWithoutSpectators((player, team) -> {
                        sendMessage(player, false, Component.text("The game starts in 20 seconds!", NamedTextColor.YELLOW));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                    });
                } else if (time == 10) {
                    game.forEachOnlinePlayerWithoutSpectators((player, team) -> {
                        sendMessage(player, false, Component.text("The game starts in ", NamedTextColor.YELLOW)
                                                            .append(Component.text("10 ", NamedTextColor.GOLD))
                                                            .append(Component.text("seconds!"))
                        );
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                    });
                } else if (time <= 5 && time != 0) {
                    game.forEachOnlinePlayerWithoutSpectators((player, team) -> {
                        String s = time == 1 ? "!" : "s!";
                        sendMessage(player, false, Component.text("The game starts in ", NamedTextColor.YELLOW)
                                                            .append(Component.text(time, NamedTextColor.RED))
                                                            .append(Component.text(" second" + s))
                        );
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                    });
                } else if (time == 0) {
                    game.forEachOnlinePlayerWithoutSpectators((player, team) -> {
                        player.playSound(player.getLocation(), "gamestart", 1, 1);
                        player.setAllowFlight(false);
                    });
                }
            }

            if (timer % 200 == 0) {
                String bannedSpecs = Arrays.stream(Specializations.VALUES)
                                           .filter(Specializations::isBanned)
                                           .map(specializations -> specializations.name)
                                           .collect(Collectors.joining(", "));
                if (!bannedSpecs.isEmpty()) {
                    game.forEachOnlinePlayerWithoutSpectators((player, team) -> {
                        sendMessage(player,
                                false,
                                Component.text("WARNING: Currently disabled specs: " + bannedSpecs + ". Your spec will be automatically switched if any of those are selected.",
                                        NamedTextColor.RED
                                )
                        );
                    });
                }
            }

            if (timer <= 0) {

                if (!game.getAddons().contains(GameAddon.PRIVATE_GAME) && !com.ebicep.warlords.game.GameMode.isPvE(game.getGameMode())) {
                    Balancer balancer = new Balancer(game);
                    balancer.balance(false);
                    balancer.printDebugInfo();

                    Map<Team, Balancer.TeamInfo> bestTeam = balancer.getBestTeam();
                    bestTeam.forEach((team, teamInfo) -> teamInfo.getPlayersSpecs().forEach((uuid, specializations) -> {
                        PlayerSettings.getPlayerSettings(uuid).setWantedTeam(team);
                        game.setPlayerTeam(uuid, team);
                        List<LobbyLocationMarker> lobbies = game.getMarkers(LobbyLocationMarker.class);
                        LobbyLocationMarker location = lobbies.stream().filter(e -> e.matchesTeam(team)).collect(Utils.randomElement());
                        if (location != null) {
                            Player player = Bukkit.getPlayer(uuid);
                            if (player != null) {
                                player.teleport(location.getLocation());
                                Warlords.setRejoinPoint(player.getUniqueId(), location.getLocation());
                            }
                        }
                    }));
                }

                if (game.getPlayers().size() >= 14) {
                    boolean isPrivate = game.getAddons().contains(GameAddon.PRIVATE_GAME);
                    BotManager.sendMessageToStatusChannel(
                            "[GAME] A " + (isPrivate ? "" : "Public ") + "**" + game.getMap().getMapName() + "** started with **" + game.getPlayers()
                                                                                                                                        .size() + (game.getPlayers()
                                                                                                                                                       .size() == 1 ? "** player!" : "** players!")
                    );
                }

                return new SyncTimerState(game);
            }
            timer--;
        } else {
            resetTimer();
            game.forEachOnlinePlayerWithoutSpectators((player, team) -> giveLobbyScoreboard(false, player));
        }
        return null;
    }

    public boolean hasEnoughPlayers() {
        int players = game.playersCount();
        return players >= game.getMinPlayers();
    }

    public void giveLobbyScoreboard(boolean init, Player player) {
        CustomScoreboard customScoreboard = CustomScoreboard.getPlayerScoreboard(player);

        Component date = Component.text(DateUtil.formatCurrentDateEST("MM/dd/yyyy"), NamedTextColor.GRAY);
        Component map = Component.text("Map: ", NamedTextColor.WHITE).append(Component.text(game.getMap().getMapName(), NamedTextColor.GREEN));
        Component players = Component.text("Players: ", NamedTextColor.WHITE)
                                     .append(Component.text(game.playersCount() + "/" + game.getMaxPlayers(), NamedTextColor.GREEN));
        Specializations specializations = PlayerSettings.getPlayerSettings(player.getUniqueId()).getSelectedSpec();
        Component level = Component.text("Lv" + ExperienceManager.getLevelString(ExperienceManager.getLevelForSpec(player.getUniqueId(), specializations)) + " ",
                                           NamedTextColor.GRAY
                                   )
                                   .append(Component.text(Specializations.getClass(specializations).name, NamedTextColor.GOLD));
        Component spec = Component.text("Spec: ", NamedTextColor.WHITE).append(Component.text(specializations.name, NamedTextColor.GREEN));
        Component version = Component.text(Warlords.VERSION, NamedTextColor.YELLOW);
        if (hasEnoughPlayers()) {
            customScoreboard.giveNewSideBar(init,
                    date,
                    Component.empty(),
                    map,
                    players,
                    Component.empty(),
                    Component.text("Starting in: ", NamedTextColor.WHITE)
                             .append(Component.text(getTimeLeftString(), NamedTextColor.GREEN)),
                    Component.empty(),
                    level,
                    spec,
                    Component.empty(),
                    version
            );
        } else {
            customScoreboard.giveNewSideBar(init,
                    date,
                    Component.empty(),
                    map,
                    players,
                    Component.empty(),
                    Component.text("Starting if ", NamedTextColor.WHITE)
                             .append(Component.text((game.getMap().getMinPlayers() - game.playersCount()), NamedTextColor.GREEN))
                             .append(Component.text(" more")),
                    Component.text("players join ", NamedTextColor.WHITE),
                    Component.empty(),
                    level,
                    spec,
                    Component.empty(),
                    version
            );
        }
    }

    public String getTimeLeftString() {
        int seconds = timer / 20;
        int minutes = seconds / 60;
        return (minutes < 10 ? "0" : "") + minutes + ":" + (seconds % 60 < 10 ? "0" : "") + seconds % 60;
    }

    @Override
    public void end() {
    }

    @Override
    public void onPlayerJoinGame(OfflinePlayer op, boolean asSpectator) {
        if (asSpectator) {
            return;
        }
        EnumSet<Team> teams = TeamMarker.getTeams(game);
        Team team = PlayerSettings.getPlayerSettings(op.getUniqueId()).getWantedTeam();
        if (team == null || !teams.contains(team)) {
            team = TeamMarker.getTeams(game).stream().filter(t -> t != Team.GAME).collect(Utils.randomElement());
        }
        game.setPlayerTeam(op, team);
        List<LobbyLocationMarker> lobbies = game.getMarkers(LobbyLocationMarker.class);
        Team finalTeam = team;
        LobbyLocationMarker location = lobbies.stream().filter(e -> e.matchesTeam(finalTeam)).collect(Utils.randomElement());
        if (location == null) {
            location = lobbies.stream().collect(Utils.randomElement());
        }
        if (location != null) {
            Warlords.setRejoinPoint(op.getUniqueId(), location.getLocation());
        }
    }

    @Override
    public void onPlayerReJoinGame(Player player) {
        Team team = game.getPlayerTeam(player.getUniqueId());
        player.getActivePotionEffects().clear();
        player.getInventory().clear();

        player.setAllowFlight(team == null);
        if (team != null) {
            for (PreGameItemOption item : items) {
                if (item != null) {
                    player.getInventory().setItem(item.getSlot(), item.getItem(game, player));
                }
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setGameMode(team == null ? GameMode.SPECTATOR : GameMode.ADVENTURE);
            }
        }.runTaskLater(Warlords.getInstance(), 1);

        LobbyLocationMarker location = LobbyLocationMarker.getRandomLobbyLocation(game, team);
        if (location != null) {
            player.teleport(location.getLocation());
            Warlords.setRejoinPoint(player.getUniqueId(), location.getLocation());
        } else {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage("Unable to warp player to lobby!, no lobby marker found");
        }
    }

    @Override
    public int getTicksElapsed() {
        return 0;
    }

    @Override
    public void skipTimer() {
        this.timer = 0;
        this.timerHasBeenSkipped = true;
    }

    @Override
    public void resetTimer() throws IllegalStateException {
        this.timer = this.maxTimer;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public int getMaxTimer() {
        return maxTimer;
    }

    public void setMaxTimer(int maxTimer) {
        this.maxTimer = maxTimer;
    }

    public void interactEvent(Player player, int heldItemSlot) {
        if (heldItemSlot >= 0 && heldItemSlot < 9 && this.items[heldItemSlot] != null) {
            this.items[heldItemSlot].runOnClick(this.game, player);
        }
    }

}
