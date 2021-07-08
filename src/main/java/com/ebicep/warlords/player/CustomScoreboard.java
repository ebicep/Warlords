package com.ebicep.warlords.player;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.flags.GroundFlagLocation;
import com.ebicep.warlords.maps.flags.PlayerFlagLocation;
import com.ebicep.warlords.maps.flags.SpawnFlagLocation;
import com.ebicep.warlords.maps.state.PlayingState;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomScoreboard {
    private final WarlordsPlayer warlordsPlayer;
    private final Scoreboard scoreboard;
    private final Objective sideBar;
    private Objective health;
    private final PlayingState gameState;

    public CustomScoreboard(WarlordsPlayer warlordsPlayer, PlayingState gameState) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        sideBar = scoreboard.registerNewObjective("WARLORDS", "");
        sideBar.setDisplaySlot(DisplaySlot.SIDEBAR);
        sideBar.setDisplayName("§e§lWARLORDS");
        sideBar.getScore(ChatColor.GRAY + format.format(new Date())).setScore(15);
        sideBar.getScore(" ").setScore(14);
        sideBar.getScore(ChatColor.BLUE + "BLU: " + ChatColor.AQUA + gameState.getStats(com.ebicep.warlords.maps.Team.RED).points() + ChatColor.GOLD + "/1000").setScore(13);
        sideBar.getScore(ChatColor.RED + "RED: " + ChatColor.AQUA + gameState.getStats(com.ebicep.warlords.maps.Team.BLUE).points() + ChatColor.GOLD + "/1000").setScore(12);
        sideBar.getScore("  ").setScore(11);
        sideBar.getScore(ChatColor.WHITE + "Time Left: " + ChatColor.GREEN + "15:00").setScore(10);
        sideBar.getScore("   ").setScore(9);
        sideBar.getScore(ChatColor.RED + "RED Flag: " + ChatColor.GREEN + "Safe").setScore(8);
        sideBar.getScore(ChatColor.BLUE + "BLU Flag: " + ChatColor.GREEN + "Safe").setScore(7);
        sideBar.getScore("    ").setScore(6);
        sideBar.getScore(ChatColor.WHITE + "Class: " + ChatColor.GREEN + warlordsPlayer.getSpec().getClass().getSimpleName()).setScore(5);
        sideBar.getScore("     ").setScore(4);
        sideBar.getScore("" + ChatColor.GREEN + warlordsPlayer.getTotalKills() + ChatColor.RESET + " Kills " + ChatColor.GREEN + warlordsPlayer.getTotalAssists() + ChatColor.RESET + " Assists").setScore(3);
        sideBar.getScore("      ").setScore(2);
        sideBar.getScore(ChatColor.YELLOW + "WL 2.0 RC-3").setScore(1);
        this.gameState = gameState;
        this.warlordsPlayer = warlordsPlayer;
    }

    public void updateHealth() {
        if (health != null) {
            health.unregister();
        }
        health = scoreboard.registerNewObjective("health", "dummy");
        health.setDisplaySlot(DisplaySlot.BELOW_NAME);
        health.setDisplayName(ChatColor.RED + "❤");
        this.gameState.getGame().forEachOfflinePlayer((player, team) -> {
            WarlordsPlayer s = Warlords.getPlayer(player);
            Score score = health.getScore(s.getName());
            score.setScore(s.getHealth());
        });
    }

    public void updateNames() {
        this.gameState.getGame().forEachOfflinePlayer((player, team) -> {
            WarlordsPlayer s = Warlords.getPlayer(player);
            Team temp = scoreboard.registerNewTeam(s.getName());
            temp.setPrefix(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + s.getSpec().getClassNameShort() + ChatColor.DARK_GRAY + "] " + team.teamColor());
            temp.addEntry(s.getName());
            temp.setSuffix(ChatColor.DARK_GRAY + " [" + ChatColor.GOLD + "Lv90" + ChatColor.DARK_GRAY + "]");
        });
    }

    public void updateBasedOnGameState(PlayingState gameState) {

        this.updateHealth();

        // Timer
        {
            int secondsRemaining = gameState.getTimer() / 20;
            int minute = secondsRemaining / 60;
            int second = secondsRemaining % 60;
            String timeLeft = "";
            if (minute < 10) {
                timeLeft += "0";
            }
            timeLeft += minute + ":";
            if (second < 10) {
                timeLeft += "0";
            }
            timeLeft += second;
            for (String entry : scoreboard.getEntries()) {
                String entryUnformatted = entry;
                if (entryUnformatted.contains("Wins in:") || entryUnformatted.contains("Time Left:")) {
                    scoreboard.resetScores(entry);
                    com.ebicep.warlords.maps.Team team = gameState.calculateWinnerByPoints();
                    if (team != null) {
                        sideBar.getScore(team.coloredPrefix() + " " + ChatColor.GOLD + "Wins in: " + ChatColor.GREEN + timeLeft).setScore(10);
                    } else {
                        sideBar.getScore(ChatColor.WHITE + "Time Left: " + ChatColor.GREEN + timeLeft).setScore(10);
                    }
                }
            }
        }
        // Points
        {
            for (String entry : scoreboard.getEntries()) {
                String entryUnformatted = ChatColor.stripColor(entry);
                //System.out.println(entry);
                //System.out.println(scoreboard.getObjectives().iterator().next().getName());
                if (entryUnformatted.contains("BLU:")) {
                    scoreboard.resetScores(entry);
                    sideBar.getScore(ChatColor.BLUE + "BLU: " + ChatColor.AQUA + gameState.getBluePoints() + ChatColor.GOLD + "/1000").setScore(13);
                } else if (entryUnformatted.contains("RED:")) {
                    scoreboard.resetScores(entry);
                    sideBar.getScore(ChatColor.RED + "RED: " + ChatColor.AQUA + gameState.getRedPoints() + ChatColor.GOLD + "/1000").setScore(12);
                }
            }
        }
        // Flags
        {
            for (String entry : scoreboard.getEntries()) {
                String entryUnformatted = ChatColor.stripColor(entry);
                if (entryUnformatted.contains("RED Flag")) {
                    scoreboard.resetScores(entry);
                    if (gameState.flags().getRed().getFlag() instanceof SpawnFlagLocation) {
                        sideBar.getScore(ChatColor.RED + "RED Flag: " + ChatColor.GREEN + "Safe").setScore(8);
                    } else if (gameState.flags().getRed().getFlag() instanceof PlayerFlagLocation) {

                        PlayerFlagLocation flag = (PlayerFlagLocation) gameState.flags().getRed().getFlag();

                        if (flag.getPickUpTicks() == 0) {
                            sideBar.getScore(ChatColor.RED + "RED Flag: " + ChatColor.RED + "Stolen!").setScore(8);
                        } else {
                            sideBar.getScore(ChatColor.RED + "RED Flag: " + ChatColor.RED + "Stolen!" + ChatColor.YELLOW + " +" + flag.getComputedHumanMultiplier() + "§e%").setScore(8);
                        }

                    } else if (gameState.flags().getRed().getFlag() instanceof GroundFlagLocation) {
                        sideBar.getScore(ChatColor.RED + "RED Flag: " + ChatColor.YELLOW + "Dropped!" + ChatColor.GRAY).setScore(8);
                    } else {
                        sideBar.getScore(ChatColor.RED + "RED Flag: " + ChatColor.GRAY + "Respawning...").setScore(8);
                    }
                }

                if (entryUnformatted.contains("BLU Flag")) {
                    scoreboard.resetScores(entry);
                    if (gameState.flags().getBlue().getFlag() instanceof SpawnFlagLocation) {
                        sideBar.getScore(ChatColor.BLUE + "BLU Flag: " + ChatColor.GREEN + "Safe").setScore(7);
                    } else if (gameState.flags().getBlue().getFlag() instanceof PlayerFlagLocation) {

                        PlayerFlagLocation flag = (PlayerFlagLocation) gameState.flags().getBlue().getFlag();

                        if (flag.getPickUpTicks() == 0) {
                            sideBar.getScore(ChatColor.BLUE + "BLU Flag: " + ChatColor.RED + "Stolen!").setScore(7);
                        } else {
                            sideBar.getScore(ChatColor.BLUE + "BLU Flag: " + ChatColor.RED + "Stolen!" + ChatColor.YELLOW + " +" + flag.getComputedHumanMultiplier() + "§e%").setScore(7);
                        }

                    } else if (gameState.flags().getBlue().getFlag() instanceof GroundFlagLocation) {
                        sideBar.getScore(ChatColor.BLUE + "BLU Flag: " + ChatColor.YELLOW + "Dropped!" + ChatColor.GRAY).setScore(7);
                    } else {
                        sideBar.getScore(ChatColor.BLUE + "BLU Flag: " + ChatColor.GRAY + "Respawning...").setScore(7);
                    }
                }
            }
        }
    }

    public void updateKillsAssists() {
        for (String entry : scoreboard.getEntries()) {
            String entryUnformatted = ChatColor.stripColor(entry);
            if (entryUnformatted.contains("Kills")) {
                scoreboard.resetScores(entry);
                sideBar.getScore("" + ChatColor.GREEN + warlordsPlayer.getTotalKills() + ChatColor.RESET + " Kills " + ChatColor.GREEN + warlordsPlayer.getTotalAssists() + ChatColor.RESET + " Assists").setScore(3);
            }
        }
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Objective getSideBar() {
        return sideBar;
    }

    public Objective getHealth() {
        return health;
    }


    public static void giveMainLobbyScoreboard(Player player) {
        Scoreboard mainLobbyScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective sideBar = mainLobbyScoreboard.registerNewObjective("WARLORDS", "");
        sideBar.setDisplaySlot(DisplaySlot.SIDEBAR);
        sideBar.setDisplayName("§e§lWARLORDS");
        sideBar.getScore("").setScore(15);
        sideBar.getScore("Kills: " + ChatColor.GREEN + Utils.addCommaAndRound(((Integer) Warlords.databaseManager.getPlayerInformation(player, "kills")))).setScore(14);
        sideBar.getScore("Assists: " + ChatColor.GREEN + Utils.addCommaAndRound(((Integer) Warlords.databaseManager.getPlayerInformation(player, "assists")))).setScore(13);
        sideBar.getScore("Deaths: " + ChatColor.GREEN + Utils.addCommaAndRound(((Integer) Warlords.databaseManager.getPlayerInformation(player, "deaths")))).setScore(12);
        sideBar.getScore(" ").setScore(11);
        sideBar.getScore("Wins: " + ChatColor.GREEN + Utils.addCommaAndRound(((Integer) Warlords.databaseManager.getPlayerInformation(player, "wins")))).setScore(10);
        sideBar.getScore("Losses: " + ChatColor.GREEN + Utils.addCommaAndRound(((Integer) Warlords.databaseManager.getPlayerInformation(player, "losses")))).setScore(9);
        sideBar.getScore("  ").setScore(8);
        sideBar.getScore("Damage: " + ChatColor.DARK_RED + Utils.addCommaAndRound(((Integer) Warlords.databaseManager.getPlayerInformation(player, "damage")))).setScore(7);
        sideBar.getScore("Healing: " + ChatColor.DARK_GREEN + Utils.addCommaAndRound(((Integer) Warlords.databaseManager.getPlayerInformation(player, "healing")))).setScore(6);
        sideBar.getScore("Absorbed: " + ChatColor.GOLD + Utils.addCommaAndRound(((Integer) Warlords.databaseManager.getPlayerInformation(player, "absorbed")))).setScore(5);
        sideBar.getScore("   ").setScore(4);
        sideBar.getScore("dubious").setScore(3);
        sideBar.getScore("    ").setScore(2);
        sideBar.getScore(ChatColor.YELLOW + "WL 2.0 RC-3").setScore(1);
        player.setScoreboard(mainLobbyScoreboard);
    }
}