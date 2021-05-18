package com.ebicep.warlords.util;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.maps.FlagManager;
import com.ebicep.warlords.maps.FlagManager.*;
import com.ebicep.warlords.maps.Game;
import net.minecraft.server.v1_8_R3.EnumChatFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scoreboard.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CustomScoreboard {

    private Player player;
    private Scoreboard scoreboard;
    private Objective sideBar;
    private Objective health;
    private List<String> blueTeam;
    private List<String> redTeam;

    public CustomScoreboard(Player player, List<String> blueTeam, List<String> redTeam) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        String dateString = format.format(new Date());
        Objective sideBar = board.registerNewObjective(dateString, "");
        sideBar.setDisplaySlot(DisplaySlot.SIDEBAR);
        sideBar.setDisplayName("§e§lWARLORDS");
        sideBar.getScore(ChatColor.GRAY + dateString).setScore(15);
        sideBar.getScore(" ").setScore(14);
        sideBar.getScore(ChatColor.BLUE + "BLU: " + ChatColor.AQUA + Warlords.game.getBluePoints() + ChatColor.GOLD + "/1000").setScore(13);
        sideBar.getScore(ChatColor.RED + "RED: " + ChatColor.AQUA + Warlords.game.getRedPoints() + ChatColor.GOLD + "/1000").setScore(12);
        sideBar.getScore("  ").setScore(11);
        sideBar.getScore(ChatColor.WHITE + "Time Left: " + ChatColor.GREEN + "15:00").setScore(10);
        sideBar.getScore("   ").setScore(9);
        sideBar.getScore(ChatColor.RED + "RED Flag: " + ChatColor.GREEN + "Safe").setScore(8);
        sideBar.getScore(ChatColor.BLUE + "BLU Flag: " + ChatColor.GREEN + "Safe").setScore(7);
        sideBar.getScore("    ").setScore(6);
        sideBar.getScore(ChatColor.GOLD + "Lv90 " + ChatColor.GREEN + Warlords.getPlayer(player).getSpec().getClass().getSimpleName()).setScore(5);
        sideBar.getScore("     ").setScore(4);
        sideBar.getScore("" + ChatColor.GREEN + Warlords.getPlayer(player).getKills() + ChatColor.RESET + " Kills " + ChatColor.GREEN + Warlords.getPlayer(player).getAssists() + ChatColor.RESET + " Assists").setScore(3);
        sideBar.getScore("      ").setScore(2);
        sideBar.getScore(ChatColor.YELLOW + "WL 2.0 beta_b-v1.0 ").setScore(1);
        player.setScoreboard(board);
        this.scoreboard = board;
        this.sideBar = sideBar;
        this.player = player;
        this.blueTeam = blueTeam;
        this.redTeam = redTeam;
    }

    public void addHealths() {
        Objective health = scoreboard.registerNewObjective("health", "dummy");
        health.setDisplaySlot(DisplaySlot.BELOW_NAME);
        health.setDisplayName(ChatColor.RED + "❤");
        for (WarlordsPlayer value : Warlords.getPlayers().values()) {
            Score score = health.getScore(value.getPlayer());
            score.setScore(value.getHealth());
        }
        this.health = health;
    }

    public void updateHealths() {
        health.unregister();
        addHealths();
    }

    public void updateKills() {
        for (String entry : scoreboard.getEntries()) {
            String entryUnformatted = ChatColor.stripColor(entry);
            //System.out.println(entry);
            //System.out.println(scoreboard.getObjectives().iterator().next().getName());
            if (entryUnformatted.contains("BLU:")) {
                scoreboard.resetScores(entry);
                sideBar.getScore(ChatColor.BLUE + "BLU: " + ChatColor.AQUA + Warlords.game.getBluePoints() + ChatColor.GOLD + "/1000").setScore(13);
            } else if (entryUnformatted.contains("RED:")) {
                scoreboard.resetScores(entry);
                sideBar.getScore(ChatColor.RED + "RED: " + ChatColor.AQUA + Warlords.game.getRedPoints() + ChatColor.GOLD + "/1000").setScore(12);
            }
        }
    }

    public void updateTime() {
        for (String entry : scoreboard.getEntries()) {
            String entryUnformatted = ChatColor.stripColor(entry);
            if (entryUnformatted.contains("Wins in:") || entryUnformatted.contains("Time Left:")) {
                //TODO add game timer and blue/red caps
                scoreboard.resetScores(entry);
                String timeLeft = (Game.remaining / 60) + ":";
                if (Game.remaining % 60 < 10) {
                    timeLeft += "0";
                }
                timeLeft += Game.remaining % 60;
                if (Warlords.game.getBluePoints() > Warlords.game.getRedPoints()) {
                    sideBar.getScore(ChatColor.BLUE + "BLU " + ChatColor.GOLD + "Wins in: " + ChatColor.GREEN + timeLeft).setScore(10);
                } else if (Warlords.game.getRedPoints() > Warlords.game.getBluePoints()) {
                    sideBar.getScore(ChatColor.RED + "RED " + ChatColor.GOLD + "Wins in: " + ChatColor.GREEN + timeLeft).setScore(10);
                } else {
                    sideBar.getScore(ChatColor.WHITE + "Time Left: " + ChatColor.GREEN + timeLeft).setScore(10);
                }
            }
        }
    }

    public void updateFlagStatus() {
        for (String entry : scoreboard.getEntries()) {
            String entryUnformatted = ChatColor.stripColor(entry);
            if (entryUnformatted.contains("RED Flag")) {
                scoreboard.resetScores(entry);
                if (Warlords.game.getFlags().getRed().getFlag() instanceof SpawnFlagLocation) {
                    sideBar.getScore(ChatColor.RED + "RED Flag: " + ChatColor.GREEN + "Safe").setScore(8);
                } else if (Warlords.game.getFlags().getRed().getFlag() instanceof PlayerFlagLocation) {

                    PlayerFlagLocation flag = (PlayerFlagLocation) Warlords.game.getFlags().getRed().getFlag();

                    if (flag.getModifier() == 0) {
                        sideBar.getScore(ChatColor.RED + "RED Flag: " + ChatColor.RED + "Stolen!").setScore(8);
                    } else {
                        sideBar.getScore(ChatColor.RED + "RED Flag: " + ChatColor.RED + "Stolen!" + ChatColor.YELLOW + " +" + flag.getModifier() + "§e%").setScore(8);
                    }

                } else if (Warlords.game.getFlags().getRed().getFlag() instanceof GroundFlagLocation) {
                    sideBar.getScore(ChatColor.RED + "RED Flag: " + ChatColor.YELLOW + "Dropped!" + ChatColor.GRAY).setScore(8);
                } else {
                    sideBar.getScore(ChatColor.RED + "RED Flag: " + ChatColor.GRAY + "Respawning...").setScore(8);
                }
            }

            if (entryUnformatted.contains("BLU Flag")) {
                scoreboard.resetScores(entry);
                if (Warlords.game.getFlags().getBlue().getFlag() instanceof SpawnFlagLocation) {
                    sideBar.getScore(ChatColor.BLUE + "BLU Flag: " + ChatColor.GREEN + "Safe").setScore(7);
                } else if (Warlords.game.getFlags().getBlue().getFlag() instanceof PlayerFlagLocation) {

                    PlayerFlagLocation flag = (PlayerFlagLocation) Warlords.game.getFlags().getBlue().getFlag();

                    if (flag.getModifier() == 0) {
                        sideBar.getScore(ChatColor.BLUE + "BLU Flag: " + ChatColor.RED + "Stolen!").setScore(7);
                    } else {
                        sideBar.getScore(ChatColor.BLUE + "BLU Flag: " + ChatColor.RED + "Stolen!" + ChatColor.YELLOW + " +" + flag.getModifier() + "§e%").setScore(7);
                    }

                } else if (Warlords.game.getFlags().getBlue().getFlag() instanceof GroundFlagLocation) {
                    sideBar.getScore(ChatColor.BLUE + "BLU Flag: " + ChatColor.YELLOW + "Dropped!" + ChatColor.GRAY).setScore(7);
                } else {
                    sideBar.getScore(ChatColor.BLUE + "BLU Flag: " + ChatColor.GRAY + "Respawning...").setScore(7);
                }
            }
        }
    }

    public void updateKillsAssists() {
        for (String entry : scoreboard.getEntries()) {
            String entryUnformatted = ChatColor.stripColor(entry);
            if (entryUnformatted.contains("Kills")) {
                scoreboard.resetScores(entry);
                sideBar.getScore("" + ChatColor.GREEN + Warlords.getPlayer(player).getKills() + ChatColor.RESET + " Kills " + ChatColor.GREEN + Warlords.getPlayer(player).getAssists() + ChatColor.RESET + " Assists").setScore(3);
            }
        }
    }

    public boolean onSameTeam(Player player) {
        return blueTeam.contains(this.player.getName()) && blueTeam.contains(player.getName()) || redTeam.contains(this.player.getName()) && redTeam.contains(player.getName());
    }

    public Player getPlayer() {
        return player;
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

    public List<String> getBlueTeam() {
        return blueTeam;
    }

    public List<String> getRedTeam() {
        return redTeam;
    }
}