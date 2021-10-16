package com.ebicep.warlords.player;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.maps.flags.GroundFlagLocation;
import com.ebicep.warlords.maps.flags.PlayerFlagLocation;
import com.ebicep.warlords.maps.flags.SpawnFlagLocation;
import com.ebicep.warlords.maps.state.PlayingState;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CustomScoreboard {

    private final Player player;
    private final Scoreboard scoreboard;
    private final Objective sideBar;
    private Objective health;
    private static final String[] teamEntries = new String[]{"🎂", "🎉", "🎁", "👹", "🏀", "⚽", "🍭", "🌠", "👾", "🐍", "🔮", "👽", "💣", "🍫", "🔫"};

    public CustomScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();

        sideBar = scoreboard.registerNewObjective("WARLORDS", "dummy");
        sideBar.setDisplaySlot(DisplaySlot.SIDEBAR);
        sideBar.setDisplayName("§e§lWARLORDS 2.0");

        for (int i = 0; i < 15; i++) {
            Team tempTeam = scoreboard.registerNewTeam("team_" + (i + 1));
            tempTeam.addEntry(teamEntries[i]);
            sideBar.getScore(teamEntries[i]).setScore(i + 1);
        }

        this.player = player;
        this.player.setScoreboard(scoreboard);
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Objective getHealth() {
        return health;
    }

    public void setHealth(Objective health) {
        this.health = health;
    }

    public void setScoreboardTeamEntry(int team, String name) {
        scoreboard.getTeam("team_" + team).addEntry(name);
    }

    public void setScoreboardTeamPrefix(int team, String prefix) {
        scoreboard.getTeam("team_" + team).setPrefix(prefix);
    }

    public void setScoreboardTeamSuffix(int team, String suffix) {
        scoreboard.getTeam("team_" + team).setSuffix(suffix);
    }


    public void giveMainLobbyScoreboard() {
        if(!DatabaseManager.connected) return;
        sideBar.setDisplaySlot(DisplaySlot.SIDEBAR);
        sideBar.setDisplayName("    §e§lWARLORDS 2.0    ");
        setScoreboardTeamEntry(15, "");
        setScoreboardTeamEntry(14, "Kills: " + ChatColor.GREEN + Utils.addCommaAndRound(((Integer) DatabaseManager.getPlayerInfoWithDotNotation(player, "kills"))));
        setScoreboardTeamEntry(13, "Assists: " + ChatColor.GREEN + Utils.addCommaAndRound(((Integer) DatabaseManager.getPlayerInfoWithDotNotation(player, "assists"))));
        setScoreboardTeamEntry(12, "Deaths: " + ChatColor.GREEN + Utils.addCommaAndRound(((Integer) DatabaseManager.getPlayerInfoWithDotNotation(player, "deaths"))));
        setScoreboardTeamEntry(11, " ");
        setScoreboardTeamEntry(10, "Wins: " + ChatColor.GREEN + Utils.addCommaAndRound(((Integer) DatabaseManager.getPlayerInfoWithDotNotation(player, "wins"))));
        setScoreboardTeamEntry(9, "Losses: " + ChatColor.GREEN + Utils.addCommaAndRound(((Integer) DatabaseManager.getPlayerInfoWithDotNotation(player, "losses"))));
        setScoreboardTeamEntry(8, "  ");
        setScoreboardTeamEntry(7, "Damage: " + ChatColor.RED + Utils.addCommaAndRound(((Number) DatabaseManager.getPlayerInfoWithDotNotation(player, "damage")).doubleValue()));
        setScoreboardTeamEntry(6, "Healing: " + ChatColor.DARK_GREEN + Utils.addCommaAndRound(((Number) DatabaseManager.getPlayerInfoWithDotNotation(player, "healing")).doubleValue()));
        setScoreboardTeamEntry(5, "Absorbed: " + ChatColor.GOLD + Utils.addCommaAndRound(((Number) DatabaseManager.getPlayerInfoWithDotNotation(player, "absorbed")).doubleValue()));
        setScoreboardTeamEntry(4, "    ");
        setScoreboardTeamEntry(3, "    ");
        setScoreboardTeamEntry(2, "          §e§lUpdate");
        setScoreboardTeamEntry(1, "         " + ChatColor.GREEN + ChatColor.BOLD + Warlords.VERSION);
    }
}