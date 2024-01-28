package com.ebicep.warlords.sr;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.party.PartyManager;
import com.ebicep.warlords.party.PartyPlayer;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class Balancer {

    // TODO update balancing system to read a games Team Markers,
    // this is needed for when we support more teams in the future
    public static void balance(Game game) {
        //separating internalPlayers into even teams because it might be uneven bc internalPlayers couldve left

        //balancing based on specs

        //parties first
        int sameTeamPartyLimit = 2;
        HashMap<Team, List<UUID>> partyMembers = new HashMap<>() {{
            put(Team.BLUE, new ArrayList<>());
            put(Team.RED, new ArrayList<>());
        }};
        game.onlinePlayersWithoutSpectators().filter(e -> e.getValue() != null).forEach(e -> {
            Player player = e.getKey();
            Team team = e.getValue();
            //check if player already is recorded
            // TODO Test this logic if player are not online if this happens (we do not have player objects in this case)
            if (partyMembers.values().stream().anyMatch(list -> list.contains(player.getUniqueId()))) {
                return;
            }
            Pair<Party, PartyPlayer> partyPlayerPair = PartyManager.getPartyAndPartyPlayerFromAny(player.getUniqueId());
            if (partyPlayerPair == null) {
                return;
            }
            Party party = partyPlayerPair.getA();
            List<UUID> partyPlayersInGame = party.getAllPartyPeoplePlayerOnline()
                                                 .stream()
                                                 .map(Player::getUniqueId)
                                                 .filter(uniqueId -> game.onlinePlayers().anyMatch(e2 -> Objects.equals(e2.getKey().getUniqueId(), uniqueId)))
                                                 .toList();
            //check if party has more than limit to get on one team, if so then skip party, they get normally balanced
            if (partyPlayersInGame.size() > sameTeamPartyLimit) {
                return;
            }
            List<UUID> bluePlayers = partyMembers.get(Team.BLUE);
            List<UUID> redPlayers = partyMembers.get(Team.RED);
            List<UUID> partyPlayers = new ArrayList<>(partyPlayersInGame);
            Collections.shuffle(partyPlayers);
            int teamSizeDiff = Math.abs(bluePlayers.size() - redPlayers.size());
            //check if whole party can go on the same team to get an even amount of internalPlayers on each team
            if (teamSizeDiff > partyPlayers.size()) {
                if (bluePlayers.size() > redPlayers.size()) {
                    bluePlayers.addAll(partyPlayers);
                } else {
                    redPlayers.addAll(partyPlayers);
                }
            } else {
                bluePlayers.addAll(partyPlayers);
            }
        });

        HashMap<UUID, Integer> playersSR = new HashMap<>();
        SRCalculator.PLAYERS_SR.forEach((key, value1) -> playersSR.put(key.getUuid(), value1 == null ? 500 : value1));

        HashMap<UUID, Team> bestTeam = new HashMap<>();
        int bestBlueSR = 0;
        int bestRedSR = 0;
        int bestTeamSRDifference = Integer.MAX_VALUE;

        int maxSRDiff = 200;
        for (int i = 0; i < 5000; i++) {
            HashMap<UUID, Team> teams = new HashMap<>();
            HashMap<Specializations, List<UUID>> playerSpecs = new HashMap<>();
            game.onlinePlayersWithoutSpectators().filter(e -> e.getValue() != null).forEach(e -> {
                Player player = e.getKey();
                Team team = e.getValue();
                PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player.getUniqueId());
                playerSpecs.computeIfAbsent(playerSettings.getSelectedSpec(), v -> new ArrayList<>()).add(player.getUniqueId());
            });
            //specs that dont have an even amount of players to redistribute later
            List<UUID> playersLeft = new ArrayList<>();
            //distributing specs evenly
            playerSpecs.forEach((specs, playerList) -> {
                int amountOfTargetSpecsOnBlue = (int) teams.entrySet()
                                                           .stream()
                                                           .filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.BLUE && PlayerSettings.getPlayerSettings(
                                                                   playerTeamEntry.getKey()).getSelectedSpec() == specs)
                                                           .count();
                int amountOfTargetSpecsOnRed = (int) teams.entrySet()
                                                          .stream()
                                                          .filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED && PlayerSettings.getPlayerSettings(
                                                                  playerTeamEntry.getKey()).getSelectedSpec() == specs)
                                                          .count();
                Collections.shuffle(playerList);
                for (UUID uuid : playerList) {
                    //add to red team
                    if (amountOfTargetSpecsOnBlue > amountOfTargetSpecsOnRed) {
                        teams.put(uuid, Team.RED);
                        amountOfTargetSpecsOnRed++;
                    }
                    //add to blue team
                    else if (amountOfTargetSpecsOnRed > amountOfTargetSpecsOnBlue) {
                        teams.put(uuid, Team.BLUE);
                        amountOfTargetSpecsOnBlue++;
                    }
                    //same amount on each team - add to playersleft to redistribute
                    else {
                        playersLeft.add(uuid);
                    }
                }
            });
            //start on team with least amount of players
            int blueSR = teams.entrySet().stream()
                              .filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.BLUE)
                              .mapToInt(value -> playersSR.getOrDefault(value.getKey(), 500))
                              .sum();
            int redSR = teams.entrySet().stream()
                             .filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED)
                             .mapToInt(value -> playersSR.getOrDefault(value.getKey(), 500))
                             .sum();

//                        playersLeft = playersLeft.stream()
//                                .sorted(Comparator.comparing(o -> Warlords.getPlayerSettings(o.getUniqueId()).getSelectedClass().specType))
//                                .toList();
            for (UUID uuid : playersLeft) {
                if (redSR > blueSR) {
                    teams.put(uuid, Team.BLUE);
                    blueSR += playersSR.getOrDefault(uuid, 500);
                } else {
                    teams.put(uuid, Team.RED);
                    redSR += playersSR.getOrDefault(uuid, 500);
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
            HashMap<UUID, Team> teams = new HashMap<>();
            HashMap<Specializations, List<UUID>> playerSpecs = new HashMap<>();
            //all players are online or else they wouldve been removed from queue
            game.onlinePlayersWithoutSpectators().filter(e -> e.getValue() != null).forEach(e -> {
                Player player = e.getKey();
                Team team = e.getValue();
                PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player.getUniqueId());
                playerSpecs.computeIfAbsent(playerSettings.getSelectedSpec(), v -> new ArrayList<>()).add(player.getUniqueId());
            });
            List<UUID> playersLeft = new ArrayList<>();
            //distributing specs evenly
            playerSpecs.forEach((classes, playerList) -> {
                int amountOfTargetSpecsOnBlue = (int) teams.entrySet()
                                                           .stream()
                                                           .filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.BLUE && PlayerSettings.getPlayerSettings(
                                                                   playerTeamEntry.getKey()).getSelectedSpec() == classes)
                                                           .count();
                int amountOfTargetSpecsOnRed = (int) teams.entrySet()
                                                          .stream()
                                                          .filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED && PlayerSettings.getPlayerSettings(
                                                                  playerTeamEntry.getKey()).getSelectedSpec() == classes)
                                                          .count();
                for (UUID uuid : playerList) {
                    //add to red team
                    if (amountOfTargetSpecsOnBlue > amountOfTargetSpecsOnRed) {
                        teams.put(uuid, Team.RED);
                        amountOfTargetSpecsOnRed++;
                    }
                    //add to blue team
                    else if (amountOfTargetSpecsOnRed > amountOfTargetSpecsOnBlue) {
                        teams.put(uuid, Team.BLUE);
                        amountOfTargetSpecsOnBlue++;
                    }
                    //same amount on each team - add to playersleft to redistribute
                    else {
                        playersLeft.add(uuid);
                    }
                }
            });

            //start on team with least amount of players
            int amountOnBlue = (int) teams.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.BLUE).count();
            int amountOnRed = (int) teams.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED).count();
            final boolean[] toBlueTeam = {amountOnBlue <= amountOnRed};
            playersLeft.stream()
                       .sorted(Comparator.comparing(o -> PlayerSettings.getPlayerSettings(o).getSelectedSpec().specType))
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
                              .mapToInt(value -> playersSR.getOrDefault(value.getKey(), 500))
                              .sum();
            bestRedSR = teams.entrySet().stream()
                             .filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED)
                             .mapToInt(value -> playersSR.getOrDefault(value.getKey(), 500))
                             .sum();
            bestTeamSRDifference = Math.abs(bestBlueSR - bestRedSR);
        }

        if (bestTeamSRDifference > 5000) {
            secondFailSafeActive = true;
            HashMap<UUID, Team> teams = new HashMap<>();
            HashMap<Specializations, List<UUID>> playerSpecs = new HashMap<>();
            game.onlinePlayersWithoutSpectators().filter(e -> e.getValue() != null).forEach(e -> {
                Player player = e.getKey();
                Team team = e.getValue();
                PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player.getUniqueId());
                playerSpecs.computeIfAbsent(playerSettings.getSelectedSpec(), v -> new ArrayList<>()).add(player.getUniqueId());
            });
            int blueSR = 0;
            int redSR = 0;
            for (List<UUID> value : playerSpecs.values()) {
                for (UUID uuid : value) {
                    if (blueSR > redSR) {
                        teams.put(uuid, Team.RED);
                        redSR += playersSR.getOrDefault(uuid, 500);
                    } else {
                        teams.put(uuid, Team.BLUE);
                        blueSR += playersSR.getOrDefault(uuid, 500);
                    }
                }
            }
            bestTeam = teams;
            bestBlueSR = blueSR;
            bestRedSR = redSR;
            bestTeamSRDifference = Math.abs(bestBlueSR - bestRedSR);
        }

        bestTeam.forEach((uuid, team) -> {
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
        });

        int bluePlayers = (int) bestTeam.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.BLUE).count();
        int redPlayers = (int) bestTeam.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED).count();

        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("----- BALANCE INFORMATION -----", NamedTextColor.DARK_AQUA));
        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("Max SR Diff: " + maxSRDiff, NamedTextColor.GREEN));
        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("SR Diff: " + bestTeamSRDifference, NamedTextColor.GREEN));
        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("Blue Players: ", NamedTextColor.BLUE)
                                                                     .append(Component.text(bluePlayers, NamedTextColor.GOLD))
                                                                     .append(Component.text(" - ", NamedTextColor.GRAY))
                                                                     .append(Component.text("SR: "))
                                                                     .append(Component.text(bestBlueSR, NamedTextColor.GOLD)));
        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("Red Players: ", NamedTextColor.RED)
                                                                     .append(Component.text(redPlayers, NamedTextColor.GOLD))
                                                                     .append(Component.text(" - ", NamedTextColor.GRAY))
                                                                     .append(Component.text("SR: "))
                                                                     .append(Component.text(bestRedSR, NamedTextColor.GOLD)));
        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("Fail Safe: ", NamedTextColor.GREEN)
                                                                     .append(Component.text(failSafeActive, NamedTextColor.GOLD)));
        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("Second Fail Safe: ", NamedTextColor.GREEN)
                                                                     .append(Component.text(secondFailSafeActive, NamedTextColor.GOLD)));
        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("-------------------------------", NamedTextColor.DARK_AQUA));
        bestTeam.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(playerTeamEntry -> {
                    UUID uuid = playerTeamEntry.getKey();
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null) {
                        return;
                    }
                    Specializations specializations = PlayerSettings.getPlayerSettings(uuid).getSelectedSpec();
                    ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text(player.getName(), playerTeamEntry.getValue().teamColor())
                                                                                 .append(Component.text(" - ", NamedTextColor.GRAY))
                                                                                 .append(Component.text(specializations.name, specializations.specType.getTextColor()))
                                                                                 .append(Component.text(" - ", NamedTextColor.GRAY))
                                                                                 .append(Component.text(playersSR.getOrDefault(uuid, 500),
                                                                                         NamedTextColor.GOLD
                                                                                 )));
                });
        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("-------------------------------", NamedTextColor.DARK_AQUA));
    }

}
