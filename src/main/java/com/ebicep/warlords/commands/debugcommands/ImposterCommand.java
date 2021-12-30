package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.party.Poll;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PacketUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class ImposterCommand implements CommandExecutor {

    public static boolean enabled = false;
    public static String blueImposterName = null;
    public static String redImposterName = null;
    public static int blueVoters = 0;
    public static int redVoters = 0;
    public static List<WarlordsPlayer> voters = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (args.length < 1) {
            return true;
        }
        String input = args[0];
        if (input.equalsIgnoreCase("toggle") || input.equalsIgnoreCase("assign")) {
            if (!sender.isOp()) {
                sender.sendMessage("§cYou do not have permission to do that.");
                return true;
            }
        }
        if (!input.equalsIgnoreCase("toggle") && !enabled) {
            sender.sendMessage(ChatColor.RED + "The imposter gamemode is currently disabled");
            return true;
        }
        switch (input.toLowerCase()) {
            case "toggle": {
                enabled = !enabled;
                if (enabled) {
                    sender.sendMessage(ChatColor.GREEN + "Imposter gamemode is now enabled");
                } else {
                    sender.sendMessage(ChatColor.RED + "Imposter gamemode is now disabled");
                }
                break;
            }
            case "assign": {
                WarlordsPlayer warlordsPlayer = BaseCommand.requireWarlordsPlayer(sender);
                if (warlordsPlayer == null) return true;

                List<Player> bluePlayers = warlordsPlayer.getGame().players()
                        .filter(uuidTeamEntry -> uuidTeamEntry.getValue() == Team.BLUE)
                        .map(Map.Entry::getKey)
                        .map(Bukkit::getPlayer)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                List<Player> redPlayers = warlordsPlayer.getGame().players()
                        .filter(uuidTeamEntry -> uuidTeamEntry.getValue() == Team.RED)
                        .map(Map.Entry::getKey)
                        .map(Bukkit::getPlayer)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                blueImposterName = bluePlayers.get(new Random().nextInt(bluePlayers.size())).getName();
                redImposterName = redPlayers.get(new Random().nextInt(redPlayers.size())).getName();
                System.out.println("BLUE IMPOSTER - " + blueImposterName);
                System.out.println("RED IMPOSTER - " + redImposterName);
                blueVoters = 0;
                redVoters = 0;
                voters.clear();
                List<Player> players = new ArrayList<>();
                players.addAll(bluePlayers);
                players.addAll(redPlayers);
                new BukkitRunnable() {
                    int counter = 0;

                    @Override
                    public void run() {
                        players.forEach(player -> {
                            String title = "";
                            switch (counter) {
                                case 0:
                                    title = ChatColor.GREEN + "3";
                                    break;
                                case 1:
                                    title = ChatColor.YELLOW + "2";
                                    break;
                                case 2:
                                    title = ChatColor.RED + "1";
                                    break;
                                case 3:
                                    title = ChatColor.YELLOW + "You are...";
                                    break;
                                case 4:
                                    if (player.getName().equalsIgnoreCase(blueImposterName) || player.getName().equalsIgnoreCase(redImposterName)) {
                                        title = ChatColor.RED + "The IMPOSTER";
                                        Party.sendMessageToPlayer(player, ChatColor.RED + "You are the IMPOSTER", true, true);
                                    } else {
                                        title = ChatColor.GREEN + "INNOCENT";
                                        Party.sendMessageToPlayer(player, ChatColor.GREEN + "You are INNOCENT", true, true);
                                    }
                                    break;
                            }
                            PacketUtils.sendTitle(player, title, "", 0, 100, 40);
                        });
                        counter++;
                        if (counter == 5) {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Warlords.getInstance(), 10, 20);
                break;
            }
            case "vote": {
                WarlordsPlayer warlordsPlayer = BaseCommand.requireWarlordsPlayer(sender);
                if (warlordsPlayer == null) return true;
                if (!Warlords.partyManager.inAParty(warlordsPlayer.getUuid())) return true;
                if (blueImposterName == null || redImposterName == null) {
                    sender.sendMessage(ChatColor.RED + "The imposters have not been assigned yet! Or have they...");
                    return true;
                }
                if (voters.contains(warlordsPlayer)) {
                    sender.sendMessage(ChatColor.RED + "You already voted to vote!");
                    return true;
                }

                Party party = Warlords.partyManager.getPartyFromAny(warlordsPlayer.getUuid()).get();
                if (!party.getPolls().isEmpty()) {
                    sender.sendMessage(ChatColor.GREEN + "There is an ongoing poll!");
                    return true;
                }
                voters.add(warlordsPlayer);

                if (warlordsPlayer.getGame().isBlueTeam(warlordsPlayer.getUuid())) {
                    int playersNeeded = (int) (warlordsPlayer.getGame().getPlayers().entrySet().stream().filter(uuidTeamEntry -> uuidTeamEntry.getValue() == Team.BLUE).count() / 2);
                    blueVoters++;
                    if (blueVoters >= playersNeeded) {
                        Poll poll = party.addPoll(
                                "Who is the most SUS on your team?",
                                warlordsPlayer.getGame().offlinePlayers()
                                        .filter(uuidTeamEntry -> uuidTeamEntry.getValue() == Team.BLUE)
                                        .map(offlinePlayerTeamEntry -> offlinePlayerTeamEntry.getKey().getName())
                                        .collect(Collectors.toList()),
                                true,
                                warlordsPlayer.getGame().offlinePlayers()
                                        .filter(uuidTeamEntry -> uuidTeamEntry.getValue() == Team.RED)
                                        .map(offlinePlayerTeamEntry -> offlinePlayerTeamEntry.getKey().getUniqueId())
                                        .collect(Collectors.toList()),
                                () -> {
                                }
                        );
                        poll.setRunnableAfterPollEnded(() -> {
                            String mostVoted = Collections.max(poll.getOptionsWithVotes().entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
                            boolean votedCorrectly = mostVoted.equalsIgnoreCase(blueImposterName);
                            new BukkitRunnable() {
                                int counter = 0;

                                @Override
                                public void run() {
                                    String title = "";
                                    String subtitle = "";
                                    switch (counter) {
                                        case 0:
                                        case 1:
                                            title = ChatColor.BLUE + "BLUE VOTED...";
                                            break;
                                        case 2:
                                        case 3:
                                            if (votedCorrectly) {
                                                title = ChatColor.GREEN + "Correctly!";
                                            } else {
                                                title = ChatColor.RED + "Incorrectly!";
                                            }
                                            subtitle = ChatColor.BLUE + blueImposterName + ChatColor.YELLOW + " was the imposter";
                                            break;
                                    }
                                    counter++;
                                    if (counter < 6) {
                                        String finalTitle = title;
                                        String finalSubtitle = subtitle;
                                        warlordsPlayer.getGame().getPlayers()
                                                .forEach((uuid, team) -> {
                                                    Player player = Bukkit.getPlayer(uuid);
                                                    if (player != null) {
                                                        PacketUtils.sendTitle(player, finalTitle, finalSubtitle, 0, 150, 40);
                                                    }
                                                });
                                    } else if (counter == 6) {
                                        warlordsPlayer.getGame().getPlayers()
                                                .forEach((uuid, team) -> {
                                                    Player player = Bukkit.getPlayer(uuid);
                                                    if (player != null) {
                                                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                                                        if (team == Team.BLUE) {
                                                            if ((votedCorrectly && !player.getName().equalsIgnoreCase(blueImposterName)) || (!votedCorrectly && player.getName().equalsIgnoreCase(blueImposterName))) {
                                                                PacketUtils.sendTitle(player, ChatColor.GREEN + "YOU WON!", "", 0, 300, 40);
                                                                player.sendMessage(ChatColor.GREEN + "You won!");
                                                            } else {
                                                                PacketUtils.sendTitle(player, ChatColor.RED + "YOU LOST!", "", 0, 300, 40);
                                                                player.sendMessage(ChatColor.RED + "You lost!");
                                                            }
                                                        } else if (team == Team.RED) {
                                                            if (votedCorrectly) {
                                                                PacketUtils.sendTitle(player, ChatColor.RED + "YOU LOST!", "", 0, 300, 40);
                                                                player.sendMessage(ChatColor.RED + "You lost!");
                                                            } else {
                                                                PacketUtils.sendTitle(player, ChatColor.GREEN + "YOU WON!", "", 0, 300, 40);
                                                                player.sendMessage(ChatColor.GREEN + "You won!");
                                                            }
                                                        }
                                                    }
                                                });
                                    } else if (counter == 10) {
                                        warlordsPlayer.getGame().freeze("", false);
                                        if (votedCorrectly) {
                                            warlordsPlayer.getGameState().getStats(Team.BLUE).setPoints(1000);
                                        } else {
                                            warlordsPlayer.getGameState().getStats(Team.RED).setPoints(1000);
                                        }
                                        this.cancel();
                                        blueImposterName = null;
                                        redImposterName = null;
                                    }
                                }
                            }.runTaskTimer(Warlords.getInstance(), 5, 20);
                        });
                        warlordsPlayer.getGame().freeze(ChatColor.BLUE + "BLUE" + ChatColor.GREEN + " is voting!", true);
                    } else {
                        warlordsPlayer.getGame().forEachOnlinePlayer((player, team) -> {
                            if (team == Team.BLUE) {
                                Party.sendMessageToPlayer(player, ChatColor.GREEN + "A player wants to vote out someone! (" + blueVoters + "/" + playersNeeded + ")", true, true);
                            }
                        });
                    }
                } else if (warlordsPlayer.getGame().isRedTeam(warlordsPlayer.getUuid())) {
                    int playersNeeded = (int) (warlordsPlayer.getGame().getPlayers().entrySet().stream().filter(uuidTeamEntry -> uuidTeamEntry.getValue() == Team.RED).count() / 2);
                    redVoters++;
                    if (redVoters >= playersNeeded) {
                        Poll poll = party.addPoll(
                                "Who is the most SUS on your team?",
                                warlordsPlayer.getGame().offlinePlayers()
                                        .filter(uuidTeamEntry -> uuidTeamEntry.getValue() == Team.RED)
                                        .map(offlinePlayerTeamEntry -> offlinePlayerTeamEntry.getKey().getName())
                                        .collect(Collectors.toList()),
                                true,
                                warlordsPlayer.getGame().offlinePlayers()
                                        .filter(uuidTeamEntry -> uuidTeamEntry.getValue() == Team.BLUE)
                                        .map(offlinePlayerTeamEntry -> offlinePlayerTeamEntry.getKey().getUniqueId())
                                        .collect(Collectors.toList()),
                                () -> {
                                }
                        );
                        poll.setRunnableAfterPollEnded(() -> {
                            String mostVoted = Collections.max(poll.getOptionsWithVotes().entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey();
                            boolean votedCorrectly = mostVoted.equalsIgnoreCase(redImposterName);
                            new BukkitRunnable() {
                                int counter = 0;

                                @Override
                                public void run() {
                                    String title = "";
                                    String subtitle = "";
                                    switch (counter) {
                                        case 0:
                                        case 1:
                                            title = ChatColor.RED + "RED VOTED...";
                                            break;
                                        case 2:
                                        case 3:
                                            if (votedCorrectly) {
                                                title = ChatColor.GREEN + "Correctly!";
                                            } else {
                                                title = ChatColor.RED + "Incorrectly!";
                                            }
                                            subtitle = ChatColor.RED + redImposterName + ChatColor.YELLOW + " was the imposter";
                                            break;
                                    }
                                    counter++;
                                    if (counter < 6) {
                                        String finalTitle = title;
                                        String finalSubtitle = subtitle;
                                        warlordsPlayer.getGame().getPlayers()
                                                .forEach((uuid, team) -> {
                                                    Player player = Bukkit.getPlayer(uuid);
                                                    if (player != null) {
                                                        PacketUtils.sendTitle(player, finalTitle, finalSubtitle, 0, 150, 40);
                                                    }
                                                });
                                    } else if (counter == 6) {
                                        warlordsPlayer.getGame().getPlayers()
                                                .forEach((uuid, team) -> {
                                                    Player player = Bukkit.getPlayer(uuid);
                                                    if (player != null) {
                                                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                                                        if (team == Team.RED) {
                                                            if ((votedCorrectly && !player.getName().equalsIgnoreCase(redImposterName)) || (!votedCorrectly && player.getName().equalsIgnoreCase(redImposterName))) {
                                                                PacketUtils.sendTitle(player, ChatColor.GREEN + "YOU WON!", "", 0, 300, 40);
                                                                player.sendMessage(ChatColor.GREEN + "You won!");
                                                            } else {
                                                                PacketUtils.sendTitle(player, ChatColor.RED + "YOU LOST!", "", 0, 300, 40);
                                                                player.sendMessage(ChatColor.RED + "You lost!");
                                                            }
                                                        } else if (team == Team.BLUE) {
                                                            if (votedCorrectly) {
                                                                PacketUtils.sendTitle(player, ChatColor.RED + "YOU LOST!", "", 0, 300, 40);
                                                                player.sendMessage(ChatColor.RED + "You lost!");
                                                            } else {
                                                                PacketUtils.sendTitle(player, ChatColor.GREEN + "YOU WON!", "", 0, 300, 40);
                                                                player.sendMessage(ChatColor.GREEN + "You won!");
                                                            }
                                                        }
                                                    }
                                                });
                                    } else if (counter == 10) {
                                        warlordsPlayer.getGame().freeze("", false);
                                        if (votedCorrectly) {
                                            warlordsPlayer.getGameState().getStats(Team.RED).setPoints(1000);
                                        } else {
                                            warlordsPlayer.getGameState().getStats(Team.BLUE).setPoints(1000);
                                        }
                                        this.cancel();
                                        blueImposterName = null;
                                        redImposterName = null;
                                    }
                                }
                            }.runTaskTimer(Warlords.getInstance(), 5, 20);
                        });
                        warlordsPlayer.getGame().freeze(ChatColor.RED + "RED" + ChatColor.GREEN + " is voting!", true);
                    } else {
                        warlordsPlayer.getGame().forEachOnlinePlayer((player, team) -> {
                            if (team == Team.RED) {
                                player.sendMessage(ChatColor.GREEN + "A player wants to vote out someone! (" + redVoters + "/" + playersNeeded + ")");
                            }
                        });
                    }
                }
                break;
            }
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("imposter").setExecutor(this);
    }

}
