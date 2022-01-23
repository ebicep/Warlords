package com.ebicep.warlords.maps.state;

import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.GameAddon;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.player.*;
import com.ebicep.warlords.sr.SRCalculator;
import com.ebicep.warlords.util.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.ebicep.warlords.util.ChatUtils.sendMessage;
import com.ebicep.warlords.util.ItemBuilder;
import org.bukkit.*;

public class PreLobbyState implements State, TimerDebugAble {

    private final Game game;
    private final Map<UUID, TeamPreference> teamPreferences = new HashMap<>();
    private int timer = 0;
    private int maxTimer = 0;

    public PreLobbyState(Game game) {
        this.game = game;
    }

    @Override
    public void begin() {
        this.maxTimer = game.getMap().getCountdownTimerInTicks();
        this.resetTimer();
        // Debug
        game.printDebuggingInformation();
        game.setAcceptsPlayers(true);
        game.setAcceptsSpectators(false);
    }

    @Override
    public State run() {
        int players = game.playersCount();
        if (players >= game.getMap().getMinPlayers()) {
            if (timer % 20 == 0) {
                int time = timer / 20;
                game.forEachOnlinePlayer((player, team) -> {
                    giveLobbyScoreboard(false, player);
                    player.setAllowFlight(false);
                });
                if (time == 30) {
                    game.forEachOnlinePlayer((player, team) -> {
                        sendMessage(player, false, ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + "30 " + ChatColor.YELLOW + "seconds!");
                        player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1, 1);
                    });
                } else if (time == 20) {
                    game.forEachOnlinePlayer((player, team) -> {
                        sendMessage(player, false, ChatColor.YELLOW + "The game starts in 20 seconds!");
                        player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1, 1);
                    });
                } else if (time == 10) {
                    game.forEachOnlinePlayer((player, team) -> {
                        sendMessage(player, false, ChatColor.YELLOW + "The game starts in " + ChatColor.GOLD + "10 " + ChatColor.YELLOW + "seconds!");
                        player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1, 1);
                    });
                } else if (time <= 5 && time != 0) {
                    game.forEachOnlinePlayer((player, team) -> {
                        String s = time == 1 ? "!" : "s!";
                        sendMessage(player, false, ChatColor.YELLOW + "The game starts in " + ChatColor.RED + time + ChatColor.YELLOW + " second" + s);
                        player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1, 1);
                    });
                } else if (time == 0) {
                    game.forEachOnlinePlayer((player, team) -> {
                        ChatUtils.sendMessage(player, false, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                        ChatUtils.sendMessage(player, true, "" + ChatColor.WHITE + ChatColor.BOLD + "Warlords");
                        ChatUtils.sendMessage(player, true, "");
                        ChatUtils.sendMessage(player, true, "" + ChatColor.YELLOW + ChatColor.BOLD + "Steal and capture the enemy team's flag to");
                        ChatUtils.sendMessage(player, true, "" + ChatColor.YELLOW + ChatColor.BOLD + "earn " + ChatColor.AQUA + ChatColor.BOLD + "250 " + ChatColor.YELLOW + ChatColor.BOLD + "points! The first team with a");
                        ChatUtils.sendMessage(player, true, "" + ChatColor.YELLOW + ChatColor.BOLD + "score of " + ChatColor.AQUA + ChatColor.BOLD + "1000 " + ChatColor.YELLOW + ChatColor.BOLD + "wins!");
                        ChatUtils.sendMessage(player, true, "");
                        ChatUtils.sendMessage(player, false, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                        player.playSound(player.getLocation(), "gamestart", 1, 1);
                        player.setAllowFlight(false);
                    });
                }

                BotManager.sendStatusMessage(false);
            }

            if (timer <= 0) {
                if (!game.getAddons().contains(GameAddon.PRIVATE_GAME)) {
                    //separating internalPlayers into even teams because it might be uneven bc internalPlayers couldve left

                    //balancing based on specs

                    //parties first
                    int sameTeamPartyLimit = 2;
                    HashMap<Team, List<Player>> partyMembers = new HashMap<Team, List<Player>>() {{
                        put(Team.BLUE, new ArrayList<>());
                        put(Team.RED, new ArrayList<>());
                    }};
                    game.forEachOnlinePlayer((player, team) -> {
                        //check if player already is recorded
                        if (partyMembers.values().stream().anyMatch(list -> list.contains(player))) {
                            return;
                        }
                        Warlords.partyManager.getPartyFromAny(player.getUniqueId()).ifPresent(party -> {
                            List<Player> partyPlayersInGame = party.getAllPartyPeoplePlayerOnline().stream().filter(p -> game.getPlayers().containsKey(p.getUniqueId())).collect(Collectors.toList());
                            //check if party has more than limit to get on one team, if so then skip party, they get normally balanced
                            if (partyPlayersInGame.size() > sameTeamPartyLimit) {
                                return;
                            }
                            List<Player> bluePlayers = partyMembers.get(Team.BLUE);
                            List<Player> redPlayers = partyMembers.get(Team.RED);
                            List<Player> partyPlayers = new ArrayList<>(partyPlayersInGame);
                            Collections.shuffle(partyPlayers);
                            int teamSizeDiff = Math.abs(bluePlayers.size() - redPlayers.size());
                            //check if whole party can go on the same team to get an even amount of internalPlayers on each team
                            if (teamSizeDiff > partyPlayers.size()) {
                                if (bluePlayers.size() > redPlayers.size())
                                    bluePlayers.addAll(partyPlayers);
                                else
                                    redPlayers.addAll(partyPlayers);
                            } else {
                                bluePlayers.addAll(partyPlayers);
                            }
                        });
                    });

                    HashMap<String, Integer> playersSR = new HashMap<>();
                    SRCalculator.playersSR.forEach((key, value1) -> playersSR.put(key.getUuid(), value1 == null ? 500 : value1));

                    HashMap<Player, Team> bestTeam = new HashMap<>();
                    int bestBlueSR = 0;
                    int bestRedSR = 0;
                    int bestTeamSRDifference = Integer.MAX_VALUE;

                    int maxSRDiff = 200;
                    for (int i = 0; i < 5000; i++) {
                        HashMap<Player, Team> teams = new HashMap<>();
                        HashMap<Classes, List<Player>> playerSpecs = new HashMap<>();
                        game.forEachOnlinePlayer((player, team) -> {
                            PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
                            playerSpecs.computeIfAbsent(playerSettings.getSelectedClass(), v -> new ArrayList<>()).add(player);
                        });
                        //specs that dont have an even amount of players to redistribute later
                        List<Player> playersLeft = new ArrayList<>();
                        //distributing specs evenly
                        playerSpecs.forEach((classes, playerList) -> {
                            int amountOfTargetSpecsOnBlue = (int) teams.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.BLUE && Warlords.getPlayerSettings(playerTeamEntry.getKey().getUniqueId()).getSelectedClass() == classes).count();
                            int amountOfTargetSpecsOnRed = (int) teams.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED && Warlords.getPlayerSettings(playerTeamEntry.getKey().getUniqueId()).getSelectedClass() == classes).count();
                            for (Player player : playerList) {
                                //add to red team
                                if (amountOfTargetSpecsOnBlue > amountOfTargetSpecsOnRed) {
                                    teams.put(player, Team.RED);
                                    amountOfTargetSpecsOnRed++;
                                }
                                //add to blue team
                                else if (amountOfTargetSpecsOnRed > amountOfTargetSpecsOnBlue) {
                                    teams.put(player, Team.BLUE);
                                    amountOfTargetSpecsOnBlue++;
                                }
                                //same amount on each team - add to playersleft to redistribute
                                else {
                                    playersLeft.add(player);
                                }
                            }
                        });
                        //start on team with least amount of players
                        int blueSR = teams.entrySet().stream()
                                .filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.BLUE)
                                .mapToInt(value -> playersSR.getOrDefault(value.getKey().getUniqueId().toString(), 500))
                                .sum();
                        int redSR = teams.entrySet().stream()
                                .filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED)
                                .mapToInt(value -> playersSR.getOrDefault(value.getKey().getUniqueId().toString(), 500))
                                .sum();

//                        playersLeft = playersLeft.stream()
//                                .sorted(Comparator.comparing(o -> Warlords.getPlayerSettings(o.getUniqueId()).getSelectedClass().specType))
//                                .collect(Collectors.toList());
                        for (Player player : playersLeft) {
                            if (redSR > blueSR) {
                                teams.put(player, Team.BLUE);
                                blueSR += playersSR.getOrDefault(player.getUniqueId().toString(), 500);
                            } else {
                                teams.put(player, Team.RED);
                                redSR += playersSR.getOrDefault(player.getUniqueId().toString(), 500);
                            }
                        }

                        int bluePlayers = (int) teams.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.BLUE).count();
                        int redPlayers = (int) teams.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED).count();
                        //uneven teams
                        if ((bluePlayers + redPlayers) % 2 != 0) {
                            int srDiffRed = Math.abs(redSR - 3500 - blueSR);
                            int srDiffBlue = Math.abs(blueSR - 3500 - redSR);
                            if ((bluePlayers > redPlayers && srDiffRed > maxSRDiff && srDiffRed > 0) || (redPlayers > bluePlayers && srDiffBlue > maxSRDiff && srDiffBlue > 0)) {
                                maxSRDiff++;
                                continue;
                            }
                        } else {
                            if (Math.abs(redSR - blueSR) > maxSRDiff) {
                                maxSRDiff++;
                                continue;
                            }
                        }
                        if (Math.abs(bluePlayers - redPlayers) > 1) {
                            maxSRDiff++;
                            continue;
                        }

                        if (Math.abs(redSR - blueSR) < bestTeamSRDifference) {
                            bestTeam = teams;
                            bestBlueSR = blueSR;
                            bestRedSR = redSR;
                            bestTeamSRDifference = Math.abs(redSR - blueSR);
                        }
                    }
                    boolean failSafeActive = false;
                    boolean secondFailSafeActive = false;

                    //INCASE COULDNT BALANCE
                    if (bestTeam.isEmpty()) {
                        failSafeActive = true;
                        HashMap<Player, Team> teams = new HashMap<>();
                        HashMap<Classes, List<Player>> playerSpecs = new HashMap<>();
                        //all players are online or else they wouldve been removed from queue
                        game.forEachOnlinePlayer((player, team) -> {
                            PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
                            playerSpecs.computeIfAbsent(playerSettings.getSelectedClass(), v -> new ArrayList<>()).add(player);
                        });
                        List<Player> playersLeft = new ArrayList<>();
                        //distributing specs evenly
                        playerSpecs.forEach((classes, playerList) -> {
                            int amountOfTargetSpecsOnBlue = (int) teams.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.BLUE && Warlords.getPlayerSettings(playerTeamEntry.getKey().getUniqueId()).getSelectedClass() == classes).count();
                            int amountOfTargetSpecsOnRed = (int) teams.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED && Warlords.getPlayerSettings(playerTeamEntry.getKey().getUniqueId()).getSelectedClass() == classes).count();
                            for (Player player : playerList) {
                                //add to red team
                                if (amountOfTargetSpecsOnBlue > amountOfTargetSpecsOnRed) {
                                    teams.put(player, Team.RED);
                                    amountOfTargetSpecsOnRed++;
                                }
                                //add to blue team
                                else if (amountOfTargetSpecsOnRed > amountOfTargetSpecsOnBlue) {
                                    teams.put(player, Team.BLUE);
                                    amountOfTargetSpecsOnBlue++;
                                }
                                //same amount on each team - add to playersleft to redistribute
                                else {
                                    playersLeft.add(player);
                                }
                            }
                        });

                        //start on team with least amount of players
                        int amountOnBlue = (int) teams.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.BLUE).count();
                        int amountOnRed = (int) teams.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED).count();
                        final boolean[] toBlueTeam = {amountOnBlue <= amountOnRed};
                        playersLeft.stream()
                                .sorted(Comparator.comparing(o -> Warlords.getPlayerSettings(o.getUniqueId()).getSelectedClass().specType))
                                .forEachOrdered(player -> {
                                    if (toBlueTeam[0]) {
                                        teams.put(player, Team.BLUE);
                                    } else {
                                        teams.put(player, Team.RED);
                                    }
                                    toBlueTeam[0] = !toBlueTeam[0];
                                });

                        bestTeam = teams;
                        bestBlueSR = teams.entrySet().stream()
                                .filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.BLUE)
                                .mapToInt(value -> playersSR.getOrDefault(value.getKey().getUniqueId().toString(), 500))
                                .sum();
                        bestRedSR = teams.entrySet().stream()
                                .filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED)
                                .mapToInt(value -> playersSR.getOrDefault(value.getKey().getUniqueId().toString(), 500))
                                .sum();
                        bestTeamSRDifference = Math.abs(bestBlueSR - bestRedSR);
                    }

                    if (bestTeamSRDifference > 5000) {
                        secondFailSafeActive = true;
                        HashMap<Player, Team> teams = new HashMap<>();
                        HashMap<Classes, List<Player>> playerSpecs = new HashMap<>();
                        game.forEachOnlinePlayer((player, team) -> {
                            PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
                            playerSpecs.computeIfAbsent(playerSettings.getSelectedClass(), v -> new ArrayList<>()).add(player);
                        });
                        int blueSR = 0;
                        int redSR = 0;
                        for (List<Player> value : playerSpecs.values()) {
                            for (Player player : value) {
                                if (blueSR > redSR) {
                                    teams.put(player, Team.RED);
                                    redSR += playersSR.getOrDefault(player.getUniqueId().toString(), 500);
                                } else {
                                    teams.put(player, Team.BLUE);
                                    blueSR += playersSR.getOrDefault(player.getUniqueId().toString(), 500);
                                }
                            }
                        }
                        bestTeam = teams;
                        bestBlueSR = blueSR;
                        bestRedSR = redSR;
                        bestTeamSRDifference = Math.abs(bestBlueSR - bestRedSR);
                    }

                    bestTeam.forEach((player, team) -> {
                        game.setPlayerTeam(player, team);
                        ArmorManager.resetArmor(player, Warlords.getPlayerSettings(player.getUniqueId()).getSelectedClass(), team);
                    });

                    int bluePlayers = (int) bestTeam.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.BLUE).count();
                    int redPlayers = (int) bestTeam.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED).count();

                    for (Map.Entry<UUID, Team> uuidTeamEntry : game.getPlayers().entrySet()) {
                        Player value = Bukkit.getPlayer(uuidTeamEntry.getKey());
                        if (value.hasPermission("warlords.database.messagefeed")) {
                            value.sendMessage(ChatColor.DARK_AQUA + "----- BALANCE INFORMATION -----");
                            value.sendMessage(ChatColor.GREEN + "Max SR Diff: " + maxSRDiff);
                            value.sendMessage(ChatColor.GREEN + "SR Diff: " + bestTeamSRDifference);
                            value.sendMessage(ChatColor.BLUE + "Blue Players: " + ChatColor.GOLD + bluePlayers + ChatColor.GRAY + " - " + ChatColor.BLUE + "SR: " + ChatColor.GOLD + bestBlueSR);
                            value.sendMessage(ChatColor.RED + "Red Players: " + ChatColor.GOLD + redPlayers + ChatColor.GRAY + " - " + ChatColor.RED + "SR: " + ChatColor.GOLD + bestRedSR);
                            value.sendMessage(ChatColor.GREEN + "Fail Safe: " + ChatColor.GOLD + failSafeActive);
                            value.sendMessage(ChatColor.GREEN + "Second Fail Safe: " + ChatColor.GOLD + secondFailSafeActive);
                            value.sendMessage(ChatColor.DARK_AQUA + "-------------------------------");
                            bestTeam.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(playerTeamEntry -> {
                                Classes classes = Warlords.getPlayerSettings(playerTeamEntry.getKey().getUniqueId()).getSelectedClass();
                                value.sendMessage(playerTeamEntry.getValue().teamColor() + playerTeamEntry.getKey().getName() + ChatColor.GRAY + " - " +
                                        classes.specType.chatColor + classes.name + ChatColor.GRAY + " - " +
                                        ChatColor.GOLD + playersSR.get(playerTeamEntry.getKey().getUniqueId().toString()));
                            });

                            value.sendMessage(ChatColor.DARK_AQUA + "-------------------------------");
                        }
                    }
                }

                if (game.getPlayers().size() >= 14) {
                    BotManager.sendMessageToNotificationChannel("[GAME] A " + (game.getAddons().contains(GameAddon.PRIVATE_GAME) ? "" : "Public ") + "**" + game.getMap().getMapName() + "** started with **" + game.getPlayers().size() + (game.getPlayers().size() == 1 ? "** player!" : "** players!"), true);
                }
                return new PlayingState(game);
            }
            timer--;
        } else {
            resetTimer();
            game.forEachOnlinePlayer((player, team) -> giveLobbyScoreboard(false, player));
        }
        return null;
    }

    @Override
    public void end() {
        updateTeamPreferences();
        distributePeopleOverTeams();
    }

    public String getTimeLeftString() {
        int time = timer / 20;
        return (time < 10 ? "00:0" : "00:") + time;
    }

    public void giveLobbyScoreboard(boolean init, Player player) {
        CustomScoreboard customScoreboard = Warlords.playerScoreboards.get(player.getUniqueId());

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        String dateString = format.format(new Date());

        String mapPrefix = ChatColor.WHITE + "Map: " + ChatColor.GREEN;
        String mapSuffix;
        if (game.getMap().getMapName().length() >= 16) {
            mapPrefix += game.getMap().getMapName().substring(0, 7);
            mapSuffix = game.getMap().getMapName().substring(7);
        } else {
            mapSuffix = game.getMap().getMapName();
        }

        Classes classes = Warlords.getPlayerSettings(player.getUniqueId()).getSelectedClass();
        if (game.playersCount() >= game.getMinPlayers()) {
            customScoreboard.giveNewSideBar(init,
                    ChatColor.GRAY + dateString,
                    "  ",
                    mapPrefix + mapSuffix,
                    ChatColor.WHITE + "Players: " + ChatColor.GREEN + game.playersCount() + "/" + game.getMap().getMaxPlayers(),
                    "   ",
                    ChatColor.WHITE + "Starting in: " + ChatColor.GREEN + getTimeLeftString() + ChatColor.WHITE,
                    "    ",
                    ChatColor.GRAY + "Lv" + ExperienceManager.getLevelString(ExperienceManager.getLevelForSpec(player.getUniqueId(), classes)) + " " + ChatColor.GOLD + Classes.getClassesGroup(classes).name,
                    ChatColor.WHITE + "Spec: " + ChatColor.GREEN + classes.name,
                    "     ",
                    ChatColor.YELLOW + Warlords.VERSION);
        } else {
            customScoreboard.giveNewSideBar(init,
                    ChatColor.GRAY + dateString,
                    "  ",
                    mapPrefix + mapSuffix,
                    ChatColor.WHITE + "Players: " + ChatColor.GREEN + game.playersCount() + "/" + game.getMap().getMaxPlayers(),
                    "   ",
                    ChatColor.WHITE + "Starting if " + ChatColor.GREEN + (game.getMap().getMinPlayers() - game.playersCount()) + ChatColor.WHITE + " more",
                    ChatColor.WHITE + "players join ",
                    "    ",
                    ChatColor.GRAY + "Lv" + ExperienceManager.getLevelString(ExperienceManager.getLevelForSpec(player.getUniqueId(), classes)) + " " + ChatColor.GOLD + Classes.getClassesGroup(classes).name,
                    ChatColor.WHITE + "Spec: " + ChatColor.GREEN + classes.name,
                    "     ",
                    ChatColor.YELLOW + Warlords.VERSION);
        }
    }

    private void updateTeamPreferences() {
        this.game.offlinePlayers().forEach((e) -> {
            Team selectedTeam = Warlords.getPlayerSettings(e.getKey().getUniqueId()).getWantedTeam();
            if (selectedTeam == null) {
                Bukkit.broadcastMessage(ChatColor.GOLD + e.getKey().getName() + " §7did not choose a team!");
            }
            TeamPreference newPref = new TeamPreference(
                    e.getValue(),
                    selectedTeam == null ? e.getValue() : selectedTeam,
                    selectedTeam == null ? TeamPriority.NO_PREFERENCE : TeamPriority.PLAYER_PREFERENCE
            );
            teamPreferences.compute(e.getKey().getUniqueId(), (k, oldPref) ->
                    oldPref == null || oldPref.priority() < newPref.priority() ? newPref : oldPref
            );
        });
    }

    private boolean tryMovePeep(Map.Entry<UUID, TeamPreference> entry, Team target) {
        boolean canSwitchPeepTeam;
        if (entry.getValue().wantedTeam != target) {
            switch (entry.getValue().priority) {
                case FORCED_PREFERENCE:
                    canSwitchPeepTeam = false;
                    break;
                case PLAYER_PREFERENCE:
                    canSwitchPeepTeam = false; // Always enforce teams people manually have picked, probably set to true in the future
                    break;
                default:
                    canSwitchPeepTeam = true;
            }
        } else {
            canSwitchPeepTeam = true;
        }

        if (canSwitchPeepTeam) {
            this.game.setPlayerTeam(Bukkit.getOfflinePlayer(entry.getKey()), entry.getValue().wantedTeam);
            return true;
        }
        return false;
    }

    private void distributePeopleOverTeams() {
        List<Map.Entry<UUID, TeamPreference>> prefs = teamPreferences.entrySet().stream().collect(Collectors.toList());

        if (!prefs.isEmpty()) {
            int redIndex = 0;
            int blueIndex = prefs.size() - 1;

            boolean canPickRed = true;
            boolean canPickBlue = true;

            do {
                if (redIndex == blueIndex && canPickBlue && canPickRed) {
                    //We have 1 person remaining, and both teams still have room, put the player at its wanted team
                    tryMovePeep(prefs.get(redIndex), prefs.get(redIndex).getValue().wantedTeam);
                    canPickBlue = false;
                    canPickRed = false;
                }
                if (canPickRed) {
                    if (tryMovePeep(prefs.get(redIndex), Team.RED)) {
                        redIndex++;
                        canPickRed = redIndex < prefs.size();
                    } else {
                        canPickRed = false;
                    }
                }
                if (canPickBlue && redIndex <= blueIndex) {
                    if (tryMovePeep(prefs.get(blueIndex), Team.BLUE)) {
                        blueIndex--;
                        canPickBlue = blueIndex >= 0;
                    } else {
                        canPickBlue = false;
                    }
                }
            } while ((canPickRed || canPickBlue) && redIndex <= blueIndex);
        }
    }

    @Override
    public void skipTimer() {
        this.timer = 0;
    }

    @Override
    public void resetTimer() throws IllegalStateException {
        this.timer = this.maxTimer;
    }

    @Override
    public void onPlayerJoinGame(OfflinePlayer op, boolean asSpectator) {
        if(asSpectator) {
            
        } else {
            Team team = Warlords.getPlayerSettings(op.getUniqueId()).getWantedTeam();
            game.setPlayerTeam(op, team == null ? Team.BLUE : team);
        }
    }

    @Override
    public void onPlayerReJoinGame(Player player) {
        State.super.onPlayerReJoinGame(player);
        Team team = game.getPlayerTeam(player.getUniqueId());
        
        if(team == null) {
            
            player.getInventory().clear();
            player.setAllowFlight(true);
            player.setGameMode(GameMode.SPECTATOR);
        } else {
            player.getInventory().clear();
            player.setAllowFlight(false);
            player.setGameMode(GameMode.ADVENTURE);
            
            PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());

            player.getInventory().setItem(5, new ItemBuilder(Material.NOTE_BLOCK)
                    .name(ChatColor.GREEN + "Team Selector " + ChatColor.GRAY + "(Right-Click)")
                    .lore(ChatColor.YELLOW + "Click to select your team!")
                    .get());
            player.getInventory().setItem(6, new ItemBuilder(Material.NETHER_STAR)
                    .name(ChatColor.AQUA + "Pre-game Menu ")
                    .lore(ChatColor.GRAY + "Allows you to change your class, select a\n" + ChatColor.GRAY + "weapon, and edit your settings.")
                    .get());
            player.getInventory().setItem(1, new ItemBuilder(apc.getWeapon()
                    .getItem(playerSettings.getWeaponSkins()
                            .getOrDefault(selectedClass, Weapons.FELFLAME_BLADE).item))
                    .name("§aWeapon Skin Preview")
                    .lore("")
                    .get());

            PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
            Classes selectedClass = playerSettings.getSelectedClass();
            AbstractPlayerClass apc = selectedClass.create.get();



            // teleport player
            ArmorManager.resetArmor(player, Warlords.getPlayerSettings(player.getUniqueId()).getSelectedClass(), team);
        }
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

    private enum TeamPriority {
        FORCED_PREFERENCE,
        PLAYER_PREFERENCE,
        NO_PREFERENCE,
    }

    private final class TeamPreference implements Comparable<TeamPreference> {
        final Team wantedTeam;
        final TeamPriority priority;
        final Team currentTeam;

        public TeamPreference(Team currentTeam, Team wantedTeam, TeamPriority priority) {
            this.currentTeam = currentTeam;
            this.wantedTeam = wantedTeam;
            this.priority = priority;
        }

        public int priority() {
            return this.priority.ordinal() * 2 + 1 + (wantedTeam == currentTeam ? 1 : 0);
        }

        // Team red is negative, blue is positive
        public int toInt() {
            return (wantedTeam == Team.RED ? -1 : 1) * priority();
        }

        @Override
        public int compareTo(TeamPreference o) {
            return Integer.compare(toInt(), o.toInt());
        }

    }
}
