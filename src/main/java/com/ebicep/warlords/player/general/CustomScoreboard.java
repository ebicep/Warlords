package com.ebicep.warlords.player.general;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.PlayerLeaderboardInfo;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.leaderboards.stats.sections.StatsLeaderboardCategory;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.guilds.GuildTag;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager.*;
import static com.ebicep.warlords.util.java.NumberFormat.addCommaAndRound;

public class CustomScoreboard {

    private static final ConcurrentHashMap<UUID, CustomScoreboard> PLAYER_SCOREBOARDS = new ConcurrentHashMap<>();
    private static final String[] TEAM_ENTRIES = new String[]{
            "🎂",
            "🎉",
            "🎁",
            "👹",
            "🏀",
            "⚽",
            "🍭",
            "🌠",
            "👾",
            "🐍",
            "🔮",
            "👽",
            "💣",
            "🍫",
            "🔫",
            "🧭",
            "🧱",
            "💈",
            "🦽",
            "🦼"
    };
    private final UUID uuid;
    private final Scoreboard scoreboard;
    private Objective sideBar;
    private Objective health;

    public CustomScoreboard(UUID uuid) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();

        sideBar = scoreboard.registerNewObjective("WARLORDS", "dummy");
        sideBar.setDisplaySlot(DisplaySlot.SIDEBAR);
        sideBar.setDisplayName("§e§lWARLORDS 2.0");

        this.uuid = uuid;
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.setScoreboard(scoreboard);
        }
    }

    public static CustomScoreboard getPlayerScoreboard(UUID uuid) {
        return PLAYER_SCOREBOARDS.computeIfAbsent(uuid, CustomScoreboard::new);
    }

    public static CustomScoreboard getPlayerScoreboard(Player player) {
        return getPlayerScoreboard(player.getUniqueId());
    }

    public static void reloadPvEScoreboard(DatabasePlayerPvE databasePlayerPvE) {
        for (DatabasePlayer loadedPlayer : DatabaseManager.getLoadedPlayers(PlayersCollections.LIFETIME).values()) {
            if (loadedPlayer.getPveStats() == databasePlayerPvE) {
                Player player = Bukkit.getPlayer(loadedPlayer.getUuid());
                if (player != null && player.getWorld().getName().equalsIgnoreCase("MainLobby")) {
                    UUID playerUUID = player.getUniqueId();
                    validatePlayerHolograms(playerUUID);
                    PlayerLeaderboardInfo playerLeaderboardInfo = PLAYER_LEADERBOARD_INFOS.get(playerUUID);
                    if (playerLeaderboardInfo.getStatsGameType() == GameType.PVE) {
                        CustomScoreboard customScoreboard = getPlayerScoreboard(playerUUID);
                        customScoreboard.givePvEScoreboard(databasePlayerPvE, false);
                    }
                }
                break;
            }
        }
    }

    private void givePvEScoreboard(DatabasePlayerPvE pveStats, boolean forceClear) {
        giveNewSideBar(forceClear,
                ChatColor.GRAY + "PvE",
                "",
                "Kills: " + ChatColor.GREEN + addCommaAndRound(pveStats.getKills()),
                "Assists: " + ChatColor.GREEN + addCommaAndRound(pveStats.getAssists()),
                "Wins: " + ChatColor.GREEN + addCommaAndRound(pveStats.getWins()),
                " ",
                "Coins: " + Currencies.COIN.chatColor + addCommaAndRound(pveStats.getCurrencyValue(Currencies.COIN)),
                "Synthetic Shards: " + Currencies.SYNTHETIC_SHARD.chatColor + addCommaAndRound(pveStats.getCurrencyValue(Currencies.SYNTHETIC_SHARD)),
                "Legend Fragments: " + Currencies.LEGEND_FRAGMENTS.chatColor + addCommaAndRound(pveStats.getCurrencyValue(Currencies.LEGEND_FRAGMENTS)),
                "Star Pieces: " + ChatColor.GREEN + addCommaAndRound(pveStats.getCurrencyValue(Currencies.COMMON_STAR_PIECE) +
                        pveStats.getCurrencyValue(Currencies.RARE_STAR_PIECE) +
                        pveStats.getCurrencyValue(Currencies.EPIC_STAR_PIECE) +
                        pveStats.getCurrencyValue(Currencies.LEGENDARY_STAR_PIECE)),
                "Supply Drop Tokens: " + Currencies.SUPPLY_DROP_TOKEN.chatColor + addCommaAndRound(pveStats.getCurrencyValue(Currencies.SUPPLY_DROP_TOKEN)),
                "Fairy Essence: " + Currencies.FAIRY_ESSENCE.chatColor + addCommaAndRound(pveStats.getCurrencyValue(Currencies.FAIRY_ESSENCE)),
                "  ",
                "            " + ChatColor.WHITE + ChatColor.BOLD + "Update",
                "  " + ChatColor.RED + ChatColor.BOLD + Warlords.VERSION
        );
    }

    public void giveNewSideBar(boolean forceClear, String... entries) {
        //clearing all teams if size doesnt match
        Set<Team> teams = scoreboard.getTeams()
                .stream()
                .filter(team -> team.getName().startsWith("!team"))
                .collect(Collectors.toSet());
        if (forceClear || entries.length != teams.size()) {
            teams.forEach(Team::unregister);
            clearSideBar();

            //making new sidebar
            for (int i = 0; i < entries.length; i++) {
                Team tempTeam = scoreboard.registerNewTeam("!team_" + (i + 1));
                tempTeam.addEntry(TEAM_ENTRIES[i]);
                sideBar.getScore(TEAM_ENTRIES[i]).setScore(i + 1);
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

    public void setSideBarTeam(int team, String entry) {
        if (entry.length() > 16) {
            if (entry.charAt(15) == '§') {
                scoreboard.getTeam("!team_" + team).setPrefix(entry.substring(0, 15));
                if (entry.length() > 31) {
                    scoreboard.getTeam("!team_" + team).setSuffix(entry.substring(15, 31));
                } else {
                    scoreboard.getTeam("!team_" + team).setSuffix(entry.substring(15));
                }
            } else {
                scoreboard.getTeam("!team_" + team).setPrefix(entry.substring(0, 16));
                if (entry.length() > 32) {
                    scoreboard.getTeam("!team_" + team).setSuffix(entry.substring(16, 32));
                } else {
                    scoreboard.getTeam("!team_" + team).setSuffix(entry.substring(16));
                }
            }
        } else {
            scoreboard.getTeam("!team_" + team).setPrefix(entry);
            scoreboard.getTeam("!team_" + team).setSuffix("");
        }
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
        if (prefix.length() > 16) {
            prefix = "Error";
        }
        if (suffix.length() > 16) {
            suffix = "Error";
        }
        scoreboard.getTeam("!team_" + team).setPrefix(prefix);
        scoreboard.getTeam("!team_" + team).setSuffix(suffix);
    }

    public void giveNewSideBar(boolean forceClear, List<String> entries) {
        // 0 is faster here than .size(), see https://stackoverflow.com/a/29444594/1542723
        giveNewSideBar(forceClear, entries.toArray(new String[0]));
    }

    public static void updateLobbyPlayerNames() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            CustomScoreboard.getPlayerScoreboard(onlinePlayer).updateLobbyPlayerNamesInternal();
        }
    }

    public void updateLobbyPlayerNamesInternal() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.getWorld().getName().equals("MainLobby")) {
            return;
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            String name = onlinePlayer.getName();
            if (scoreboard.getTeam(name) == null) {
                scoreboard.registerNewTeam(name);
            }
            Team team = scoreboard.getTeam(name);
            Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(onlinePlayer.getUniqueId());
            if (guildPlayerPair != null && guildPlayerPair.getA().getTag() != null) {
                GuildTag tag = guildPlayerPair.getA().getTag();
                team.setSuffix(" " + tag.getTag());
            } else {
                team.setSuffix("");
            }
            team.setPrefix(ChatColor.AQUA.toString());
            team.addEntry(name);
        }
    }

    public void giveMainLobbyScoreboard() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.getWorld().getName().equals("MainLobby")) {
            return;
        }
        if (scoreboard.getObjective("health") != null) {
            scoreboard.getObjective("health").unregister();
            health = null;
        }

        updateLobbyPlayerNamesInternal();

        if (!DatabaseManager.enabled) {
            giveNASidebar("PvE");
            return;
        }

        if (!StatsLeaderboardManager.enabled) {
            givePvEScoreboard();
            return;
        }

        if (StatsLeaderboardManager.loaded) {
            StatsLeaderboardCategory<?> statsLeaderboardCategory = getLeaderboardCategoryFromUUID(uuid);
            if (statsLeaderboardCategory == null) {
                return;
            }
            validatePlayerHolograms(uuid);
            PlayerLeaderboardInfo playerLeaderboardInfo = PLAYER_LEADERBOARD_INFOS.get(uuid);
            GameType selectedGameType = playerLeaderboardInfo.getStatsGameType();
            PlayersCollections selectedCollection = playerLeaderboardInfo.getStatsTime();
            int statsCategory = playerLeaderboardInfo.getStatsCategory();

            StatsLeaderboard statsLeaderboard = statsLeaderboardCategory.getStatsLeaderboards().get(0);
            List<DatabasePlayer> databasePlayerList = statsLeaderboard.getSortedPlayers(playerLeaderboardInfo.getStatsTime());

            String scoreboardSelection = "";
            if (!selectedGameType.shortName.isEmpty()) {
                scoreboardSelection += selectedGameType.shortName + "/";
            }
            String shortName = STATS_LEADERBOARDS.get(selectedGameType).getCategories().get(statsCategory).getShortName();
            if (!shortName.isEmpty()) {
                scoreboardSelection += shortName + "/";
            }
            scoreboardSelection += selectedCollection.name;

            if (selectedGameType == GameType.PVE) {
                givePvEScoreboard();
                return;
            }
            Optional<DatabasePlayer> optionalDatabasePlayer = databasePlayerList.stream()
                    .filter(databasePlayer -> databasePlayer.getUuid().equals(uuid))
                    .findAny();
            if (optionalDatabasePlayer.isPresent()) {
                DatabasePlayer databasePlayer = optionalDatabasePlayer.get();
                AbstractDatabaseStatInformation playerInformation = statsLeaderboardCategory.getStatFunction().apply(databasePlayer);
                giveNewSideBar(true,
                        ChatColor.GRAY + scoreboardSelection,
                        "",
                        "Kills: " + ChatColor.GREEN + addCommaAndRound(playerInformation.getKills()),
                        "Assists: " + ChatColor.GREEN + addCommaAndRound(playerInformation.getAssists()),
                        "Deaths: " + ChatColor.GREEN + addCommaAndRound(playerInformation.getDeaths()),
                        " ",
                        "Wins: " + ChatColor.GREEN + addCommaAndRound(playerInformation.getWins()),
                        "Losses: " + ChatColor.GREEN + addCommaAndRound(playerInformation.getLosses()),
                        "  ",
                        "Damage: " + ChatColor.RED + addCommaAndRound(playerInformation.getDamage()),
                        "Healing: " + ChatColor.DARK_GREEN + addCommaAndRound(playerInformation.getHealing()),
                        "Absorbed: " + ChatColor.GOLD + addCommaAndRound(playerInformation.getAbsorbed()),
                        "    ",
                        "            " + ChatColor.WHITE + ChatColor.BOLD + "Update",
                        "  " + ChatColor.RED + ChatColor.BOLD + Warlords.VERSION
                );
            } else {
                giveNASidebar(scoreboardSelection);
            }
            return;
        }

        DatabaseManager.getPlayer(uuid,
                databasePlayer -> {
                    giveNewSideBar(true,
                            ChatColor.GRAY + "Lifetime",
                            " ",
                            "Kills: " + ChatColor.GREEN + addCommaAndRound(databasePlayer.getKills()),
                            "Assists: " + ChatColor.GREEN + addCommaAndRound(databasePlayer.getAssists()),
                            "Deaths: " + ChatColor.GREEN + addCommaAndRound(databasePlayer.getDeaths()),
                            " ",
                            "Wins: " + ChatColor.GREEN + addCommaAndRound(databasePlayer.getWins()),
                            "Losses: " + ChatColor.GREEN + addCommaAndRound(databasePlayer.getLosses()),
                            "  ",
                            "Damage: " + ChatColor.RED + addCommaAndRound(databasePlayer.getDamage()),
                            "Healing: " + ChatColor.DARK_GREEN + addCommaAndRound(databasePlayer.getHealing()),
                            "Absorbed: " + ChatColor.GOLD + addCommaAndRound(databasePlayer.getAbsorbed()),
                            "    ",
                            "            " + ChatColor.WHITE + ChatColor.BOLD + "Update",
                            "  " + ChatColor.RED + ChatColor.BOLD + Warlords.VERSION
                    );
                },
                () -> {
                    giveNASidebar("Lifetime");
                }
        );
    }

    private void giveNASidebar(String title) {
        giveNewSideBar(true,
                ChatColor.GRAY + title,
                " ",
                "Kills: " + ChatColor.GREEN + "N/A",
                "Assists: " + ChatColor.GREEN + "N/A",
                "Deaths: " + ChatColor.GREEN + "N/A",
                " " + "",
                "Wins: " + ChatColor.GREEN + "N/A",
                "Losses: " + ChatColor.GREEN + "N/A",
                "  " + "",
                "Damage: " + ChatColor.RED + "N/A",
                "Healing: " + ChatColor.DARK_GREEN + "N/A",
                "Absorbed: " + ChatColor.GOLD + "N/A",
                "    ",
                "            " + ChatColor.WHITE + ChatColor.BOLD + "Update",
                "  " + ChatColor.RED + ChatColor.BOLD + Warlords.VERSION
        );
    }

    private void givePvEScoreboard() {
        DatabaseManager.getPlayer(uuid,
                databasePlayer -> givePvEScoreboard(databasePlayer.getPveStats(), true),
                () -> giveNASidebar("PvE")
        );
    }


}