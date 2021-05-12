package com.ebicep.warlords.util;

import com.ebicep.warlords.Warlords;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CustomScoreboard {

    private Player player;
    private Scoreboard scoreboard;
    private Objective objective;
    private List<String> blueTeam;
    private List<String> redTeam;

    public CustomScoreboard(Player player, List<String> blueTeam, List<String> redTeam) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        String dateString = format.format(new Date());
        Objective objective = board.registerNewObjective(dateString, "");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§e§lWARLORDS");
        objective.getScore(ChatColor.GRAY + dateString).setScore(15);
        objective.getScore(" ").setScore(14);
        objective.getScore(ChatColor.BLUE + "BLU: " + ChatColor.AQUA + Warlords.blueKills * 5 + ChatColor.GOLD + "/1000").setScore(13);
        objective.getScore(ChatColor.RED + "RED: " + ChatColor.AQUA + Warlords.redKills * 5 + ChatColor.GOLD + "/1000").setScore(12);
        objective.getScore("  ").setScore(11);
        objective.getScore("   ").setScore(9);
        objective.getScore("    ").setScore(6);
        objective.getScore(ChatColor.GOLD + "Lv90 " + ChatColor.GREEN + Warlords.getPlayer(player).getSpec().getClass().getSimpleName()).setScore(5);
        System.out.println(player.getName() + " = " + Warlords.getPlayer(player).getSpec().getClass().getSimpleName());
        objective.getScore("     ").setScore(4);
        objective.getScore("" + ChatColor.GREEN + Warlords.getPlayer(player).getKills() + ChatColor.RESET + " Kills " + ChatColor.GREEN + Warlords.getPlayer(player).getAssists() + ChatColor.RESET + " Assists").setScore(3);
        objective.getScore("      ").setScore(2);
        objective.getScore(ChatColor.YELLOW + "localhost").setScore(1);
        player.setScoreboard(board);
        this.scoreboard = board;
        this.objective = objective;
        this.player = player;
        this.blueTeam = blueTeam;
        this.redTeam = redTeam;
    }

    public void updateKills() {
        for (String entry : scoreboard.getEntries()) {
            String entryUnformatted = ChatColor.stripColor(entry);
            //System.out.println(entry);
            //System.out.println(scoreboard.getObjectives().iterator().next().getName());
            if (entryUnformatted.contains("BLU")) {
                scoreboard.resetScores(entry);
                objective.getScore(ChatColor.BLUE + "BLU: " + ChatColor.AQUA + Warlords.blueKills * 5 + ChatColor.GOLD + "/1000").setScore(13);
            } else if (entryUnformatted.contains("RED")) {
                scoreboard.resetScores(entry);
                objective.getScore(ChatColor.RED + "RED: " + ChatColor.AQUA + Warlords.redKills * 5 + ChatColor.GOLD + "/1000").setScore(12);
            }
        }
    }

    public void updateTime() {
        for (String entry : scoreboard.getEntries()) {
            String entryUnformatted = ChatColor.stripColor(entry);
            if (entryUnformatted.contains("Wins in")) {
                //TODO add game timer and blue/red caps
                scoreboard.resetScores(entry);
                if (Warlords.blueKills > Warlords.redKills) {
                    objective.getScore(ChatColor.BLUE + "BLU " + ChatColor.GOLD + "Wins in: " + ChatColor.GREEN + "10:00").setScore(10);
                } else if (Warlords.redKills > Warlords.blueKills) {
                    objective.getScore(ChatColor.RED + "RED " + ChatColor.GOLD + "Wins in: " + ChatColor.GREEN + "10:00").setScore(10);
                } else {
                    objective.getScore(ChatColor.WHITE + "Time Left: " + ChatColor.GREEN + "10:00").setScore(10);
                }
            }
        }
    }

    public void updateFlagStatus() {
        for (String entry : scoreboard.getEntries()) {
            String entryUnformatted = ChatColor.stripColor(entry);
            if (entryUnformatted.contains("RED Flag")) {
                scoreboard.resetScores(entry);
                //TODO add flag stuff
                objective.getScore(ChatColor.RED + "RED Flag: " + ChatColor.GREEN + "Safe").setScore(8);
            }
            if (entryUnformatted.contains("BLU Flag")) {
                scoreboard.resetScores(entry);
                //TODO add flag stuff
                objective.getScore(ChatColor.BLUE + "BLU Flag: " + ChatColor.GREEN + "Safe").setScore(7);
            }
        }
    }

    public void updateKillsAssists() {
        //TODO add assits
        for (String entry : scoreboard.getEntries()) {
            String entryUnformatted = ChatColor.stripColor(entry);
            if (entryUnformatted.contains("Kills")) {
                scoreboard.resetScores(entry);

            }
        }
    }

    public Player getPlayer() {
        return player;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Objective getObjective() {
        return objective;
    }

    public List<String> getBlueTeam() {
        return blueTeam;
    }

    public List<String> getRedTeam() {
        return redTeam;
    }
}