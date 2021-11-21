package com.ebicep.warlords.player;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class CustomScoreboard {

    private final Player player;
    private final Scoreboard scoreboard;
    private Objective sideBar;
    private Objective health;
    private static final String[] teamEntries = new String[]{"🎂", "🎉", "🎁", "👹", "🏀", "⚽", "🍭", "🌠", "👾", "🐍", "🔮", "👽", "💣", "🍫", "🔫"};

    public CustomScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();

        sideBar = scoreboard.registerNewObjective("WARLORDS", "dummy");
        sideBar.setDisplaySlot(DisplaySlot.SIDEBAR);
        sideBar.setDisplayName("§e§lWARLORDS 2.0");

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

    public void setSideBarTeamPrefixAndSuffix(int team, String prefix, String suffix) {
        if(prefix.length() > 16) {
            prefix = "Error";
        }
        if(suffix.length() > 16) {
            suffix = "Error";
        }
        scoreboard.getTeam("team_" + team).setPrefix(prefix);
        scoreboard.getTeam("team_" + team).setSuffix(suffix);
    }

    public void setSideBarTeam(int team, String entry) {
        if(entry.length() > 16) {
            if(entry.charAt(15) == '§') {
                scoreboard.getTeam("team_" + team).setPrefix(entry.substring(0, 15));
                if(entry.length() > 31) {
                    scoreboard.getTeam("team_" + team).setSuffix(entry.substring(15, 31));
                } else {
                    scoreboard.getTeam("team_" + team).setSuffix(entry.substring(15));
                }
            } else {
                scoreboard.getTeam("team_" + team).setPrefix(entry.substring(0, 16));
                if(entry.length() > 32) {
                    scoreboard.getTeam("team_" + team).setSuffix(entry.substring(16, 32));
                } else {
                    scoreboard.getTeam("team_" + team).setSuffix(entry.substring(16));
                }
            }
        } else {
            scoreboard.getTeam("team_" + team).setPrefix(entry);
            scoreboard.getTeam("team_" + team).setSuffix("");
        }
    }

    public void giveNewSideBar(boolean forceClear, CustomScoreboardPair... pairs) {
        //clearing all teams if size doesnt match
        int sideBarTeams = (int) scoreboard.getTeams().stream().filter(team -> team.getName().contains("team")).count();
        if(forceClear || pairs.length != sideBarTeams) {
            scoreboard.getTeams().forEach(Team::unregister);
            clearSideBar();

            //making new sidebar
            for (int i = 0; i < pairs.length; i++) {
                Team tempTeam = scoreboard.registerNewTeam("team_" + (i + 1));
                tempTeam.addEntry(teamEntries[i]);
                sideBar.getScore(teamEntries[i]).setScore(i + 1);
            }
        }

        //giving prefix/suffix from pairs
        for (int i = pairs.length; i > 0; i--) {
            CustomScoreboardPair pair = pairs[pairs.length - i];
            setSideBarTeamPrefixAndSuffix(i, pair.getPrefix(), pair.getSuffix());
        }
    }
    public void giveNewSideBar(boolean forceClear, String ... entries) {
        //clearing all teams if size doesnt match
        int sideBarTeams = (int) scoreboard.getTeams().stream().filter(team -> team.getName().contains("team")).count();
        if(forceClear || entries.length != sideBarTeams) {
            scoreboard.getTeams().forEach(Team::unregister);
            clearSideBar();

            //making new sidebar
            for (int i = 0; i < entries.length; i++) {
                Team tempTeam = scoreboard.registerNewTeam("team_" + (i + 1));
                tempTeam.addEntry(teamEntries[i]);
                sideBar.getScore(teamEntries[i]).setScore(i + 1);
            }
        }

        //giving prefix/suffix from pairs
        for (int i = entries.length; i > 0; i--) {
            String entry = entries[entries.length - i];
            setSideBarTeam(i, entry == null ? "" : entry);
        }
    }

    private void clearSideBar() {
        sideBar.unregister();
        sideBar = scoreboard.registerNewObjective("WARLORDS", "dummy");
        sideBar.setDisplaySlot(DisplaySlot.SIDEBAR);
        sideBar.setDisplayName("§e§lWARLORDS 2.0");
    }


    public void giveMainLobbyScoreboard() {
        if (!DatabaseManager.connected) return;
        if(scoreboard.getObjective("health") != null) {
            scoreboard.getObjective("health").unregister();
            health = null;
        }
        Warlords.newChain()
                .async(() -> DatabaseManager.addPlayer(player.getUniqueId(), false))
                .sync(() -> {
                    giveNewSideBar(true,
                            "",
                            "Kills: " + ChatColor.GREEN + Utils.addCommaAndRound(((Integer) DatabaseManager.getPlayerInfoWithDotNotation(player, "kills"))),
                            "Assists: " + ChatColor.GREEN + Utils.addCommaAndRound(((Integer) DatabaseManager.getPlayerInfoWithDotNotation(player, "assists"))),
                            "Deaths: " + ChatColor.GREEN + Utils.addCommaAndRound(((Integer) DatabaseManager.getPlayerInfoWithDotNotation(player, "deaths"))),
                            " " + "",
                            "Wins: " + ChatColor.GREEN + Utils.addCommaAndRound(((Integer) DatabaseManager.getPlayerInfoWithDotNotation(player, "wins"))),
                            "Losses: " + ChatColor.GREEN + Utils.addCommaAndRound(((Integer) DatabaseManager.getPlayerInfoWithDotNotation(player, "losses"))),
                            "  " + "",
                            "Damage: " + ChatColor.RED + Utils.addCommaAndRound(((Long) DatabaseManager.getPlayerInfoWithDotNotation(player, "damage"))),
                            "Healing: " + ChatColor.DARK_GREEN + Utils.addCommaAndRound(((Long) DatabaseManager.getPlayerInfoWithDotNotation(player, "healing"))),
                            "Absorbed: " + ChatColor.GOLD + Utils.addCommaAndRound(((Long) DatabaseManager.getPlayerInfoWithDotNotation(player, "absorbed"))),
                            "    ",
                            "     ",
                            "            " + ChatColor.YELLOW + ChatColor.BOLD + "Update",
                            "     " + ChatColor.GOLD + ChatColor.BOLD + Warlords.VERSION
                    );
                }).execute();
    }
}