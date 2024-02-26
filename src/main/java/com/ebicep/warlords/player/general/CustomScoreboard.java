package com.ebicep.warlords.player.general;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.PlayerLeaderboardInfo;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.leaderboards.stats.sections.StatsLeaderboardCategory;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.guilds.GuildTag;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import javax.annotation.Nonnull;
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
    private static final TextComponent UPDATE_TEXT = Component.text("        Update", NamedTextColor.WHITE, TextDecoration.BOLD);
    private static final TextComponent VERSION_TEXT = Component.text(" " + Warlords.VERSION, Warlords.VERSION_COLOR, TextDecoration.BOLD);


    public static void reloadPvEScoreboard(DatabasePlayerPvE databasePlayerPvE) {
        for (DatabasePlayer loadedPlayer : DatabaseManager.getLoadedPlayers(PlayersCollections.LIFETIME).values()) {
            if (loadedPlayer.getPveStats() == databasePlayerPvE) {
                Player player = Bukkit.getPlayer(loadedPlayer.getUuid());
                if (player != null && player.getWorld().getName().equalsIgnoreCase("MainLobby")) {
                    UUID playerUUID = player.getUniqueId();
                    validatePlayerHolograms(playerUUID);
                    PlayerLeaderboardInfo playerLeaderboardInfo = PLAYER_LEADERBOARD_INFOS.get(playerUUID);
                    if (GameType.isPve(playerLeaderboardInfo.getStatsGameType())) {
                        CustomScoreboard customScoreboard = getPlayerScoreboard(playerUUID);
                        customScoreboard.givePvESidebar(databasePlayerPvE, false);
                    }
                }
                break;
            }
        }
    }

    public static CustomScoreboard getPlayerScoreboard(UUID uuid) {
        return PLAYER_SCOREBOARDS.computeIfAbsent(uuid, CustomScoreboard::new);
    }

    private void givePvESidebar(DatabasePlayerPvE pveStats, boolean forceClear) {
        long starPieces = pveStats.getCurrencyValue(Currencies.COMMON_STAR_PIECE) +
                pveStats.getCurrencyValue(Currencies.RARE_STAR_PIECE) +
                pveStats.getCurrencyValue(Currencies.EPIC_STAR_PIECE) +
                pveStats.getCurrencyValue(Currencies.LEGENDARY_STAR_PIECE);
        giveNewSideBar(forceClear,
                Component.text("PvE", NamedTextColor.GRAY),
                Component.empty(),
                Component.text("Kills: ").append(getSidebarComponent(pveStats.getKills(), NamedTextColor.GREEN)),
                Component.text("Assists: ").append(getSidebarComponent(pveStats.getAssists(), NamedTextColor.GREEN)),
                Component.text("Wins: ").append(getSidebarComponent(pveStats.getWins(), NamedTextColor.GREEN)),
                Component.text(" "),
                Component.text("Coins: ").append(getCurrencyComponent(pveStats, Currencies.COIN)),
                Component.text("Synthetic Shards: ").append(getCurrencyComponent(pveStats, Currencies.SYNTHETIC_SHARD)),
                Component.text("Legend Fragments: ").append(getCurrencyComponent(pveStats, Currencies.LEGEND_FRAGMENTS)),
                Component.text("Star Pieces: ").append(getSidebarComponent(starPieces, NamedTextColor.GREEN)),
                Component.text("Supply Drop Tokens: ").append(getCurrencyComponent(pveStats, Currencies.SUPPLY_DROP_TOKEN)),
                Component.text("Fairy Essence: ").append(getCurrencyComponent(pveStats, Currencies.FAIRY_ESSENCE)),
                Component.empty(),
                UPDATE_TEXT,
                VERSION_TEXT
        );
    }

    public CustomScoreboard(UUID uuid) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();

        sideBar = scoreboard.registerNewObjective("WARLORDS", Criteria.DUMMY, Component.text("WARLORDS 2.0", NamedTextColor.YELLOW, TextDecoration.BOLD));
        sideBar.setDisplaySlot(DisplaySlot.SIDEBAR);

        this.uuid = uuid;
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.setScoreboard(scoreboard);
        }
    }

    public void giveNewSideBar(boolean forceClear, Component... entries) {
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
                tempTeam.addEntry(ChatColor.values()[i].toString());
                sideBar.getScore(ChatColor.values()[i].toString()).setScore(i + 1);
            }
        }

        //giving prefix/suffix from pairs
        for (int i = entries.length; i > 0; i--) {
            Component entry = entries[entries.length - i];
            setSideBarTeam(i, entry == null ? Component.empty() : entry);
        }
    }

    @Nonnull
    private static TextComponent getSidebarComponent(long currencyValue, TextColor textColor) {
        return Component.text(addCommaAndRound(currencyValue), textColor);
    }

    @Nonnull
    private static TextComponent getCurrencyComponent(DatabasePlayerPvE pveStats, Currencies currency) {
        return getSidebarComponent(pveStats.getCurrencyValue(currency), currency.textColor);
    }

    private void clearSideBar() {
        sideBar.unregister();
        sideBar = scoreboard.registerNewObjective("WARLORDS", Criteria.DUMMY, Component.text("WARLORDS 2.0", NamedTextColor.YELLOW, TextDecoration.BOLD));
        sideBar.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void setSideBarTeam(int team, Component entry) {
        Team scoreboardTeam = scoreboard.getTeam("!team_" + team);
        if (scoreboardTeam == null) {
            scoreboardTeam = scoreboard.registerNewTeam("!team_" + team);
            scoreboardTeam.addEntry(ChatColor.values()[team - 1].toString());
            sideBar.getScore(ChatColor.values()[team - 1].toString()).setScore(team);
        }
        scoreboardTeam.prefix(entry);
    }

    public static void updateLobbyPlayerNames() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            CustomScoreboard.getPlayerScoreboard(onlinePlayer).updateLobbyPlayerNamesInternal();
        }
    }

    public void updateLobbyPlayerNamesInternal() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }
        WarlordsEntity warlordsEntity = Warlords.getPlayer(uuid);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            WarlordsEntity onlinePlayerWE = Warlords.getPlayer(onlinePlayer);
            if (warlordsEntity != null && onlinePlayerWE != null && warlordsEntity.getGame().equals(onlinePlayerWE.getGame())) {
                continue;
            }
            String name = onlinePlayer.getName();
            Team team = scoreboard.getTeam(name);
            if (team == null) {
                team = scoreboard.registerNewTeam(name);
            }
            Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(onlinePlayer.getUniqueId());
            if (guildPlayerPair != null && guildPlayerPair.getA().getTag() != null) {
                GuildTag tag = guildPlayerPair.getA().getTag();
                team.suffix(Component.space().append(tag.getTag(false)));
            } else {
                team.suffix(Component.empty());
            }
            team.prefix(Permissions.getPrefixWithColor(onlinePlayer, false));
            team.addEntry(name);
            team.color(Permissions.getColor(onlinePlayer));
        }
    }

    public static CustomScoreboard getPlayerScoreboard(Player player) {
        return getPlayerScoreboard(player.getUniqueId());
    }

    private final UUID uuid;
    private final Scoreboard scoreboard;
    private Objective sideBar;
    private Objective health;

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Objective getHealth() {
        return health;
    }

    public void setHealth(Objective health) {
        this.health = health;
    }

    public void giveNewSideBar(boolean forceClear, List<Component> entries) {
        // 0 is faster here than .size(), see https://stackoverflow.com/a/29444594/1542723
        giveNewSideBar(forceClear, entries.toArray(new Component[0]));
    }

    public void giveMainLobbyScoreboard() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.getWorld().getName().equals("MainLobby")) {
            return;
        }
        Objective healthObjective = scoreboard.getObjective("health");
        if (healthObjective != null) {
            healthObjective.unregister();
            health = null;
        }

        updateLobbyPlayerNamesInternal();

        if (!DatabaseManager.enabled) {
            giveNASidebar("PvE");
            return;
        }

        if (!StatsLeaderboardManager.enabled) {
            givePvESidebar();
            return;
        }

        if (StatsLeaderboardManager.loaded) {
            StatsLeaderboardCategory<?, ?, ?> statsLeaderboardCategory = getLeaderboardCategoryFromUUID(uuid);
            if (statsLeaderboardCategory == null) {
                return;
            }
            validatePlayerHolograms(uuid);
            PlayerLeaderboardInfo playerLeaderboardInfo = PLAYER_LEADERBOARD_INFOS.get(uuid);
            GameType selectedGameType = playerLeaderboardInfo.getStatsGameType();
            if (GameType.isPve(selectedGameType)) {
                givePvESidebar();
                return;
            }
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

            Optional<DatabasePlayer> optionalDatabasePlayer = databasePlayerList.stream()
                                                                                .filter(databasePlayer -> databasePlayer.getUuid().equals(uuid))
                                                                                .findAny();
            if (optionalDatabasePlayer.isPresent()) {
                DatabasePlayer databasePlayer = optionalDatabasePlayer.get();
                Stats playerInformation = statsLeaderboardCategory.getStatFunction().apply(databasePlayer);
                givePvPSidebar(scoreboardSelection, playerInformation);
            } else {
                giveNASidebar(scoreboardSelection);
            }
            return;
        }

        DatabaseManager.getPlayer(uuid,
                databasePlayer -> givePvPSidebar("Lifetime", databasePlayer),
                () -> giveNASidebar("Lifetime")
        );
    }

    private void giveNASidebar(String title) {
        giveNewSideBar(true,
                Component.text(title, NamedTextColor.GRAY),
                Component.empty(),
                Component.text("Kills: ").append(Component.text("N/A", NamedTextColor.GREEN)),
                Component.text("Assists: ").append(Component.text("N/A", NamedTextColor.GREEN)),
                Component.text("Deaths: ").append(Component.text("N/A", NamedTextColor.GREEN)),
                Component.empty(),
                Component.text("Wins: ").append(Component.text("N/A", NamedTextColor.GREEN)),
                Component.text("Losses: ").append(Component.text("N/A", NamedTextColor.GREEN)),
                Component.empty(),
                Component.text("Damage: ").append(Component.text("N/A", NamedTextColor.RED)),
                Component.text("Healing: ").append(Component.text("N/A", NamedTextColor.DARK_GREEN)),
                Component.text("Absorbed: ").append(Component.text("N/A", NamedTextColor.GOLD)),
                Component.empty(),
                UPDATE_TEXT,
                VERSION_TEXT
        );
    }

    private void givePvESidebar() {
        DatabaseManager.getPlayer(uuid,
                databasePlayer -> givePvESidebar(databasePlayer.getPveStats(), true),
                () -> giveNASidebar("PvE")
        );
    }


    private void givePvPSidebar(String title, Stats statInformation) {
        giveNewSideBar(true,
                Component.text(title, NamedTextColor.GRAY),
                Component.empty(),
                Component.text("Kills: ").append(getSidebarComponent(statInformation.getKills(), NamedTextColor.GREEN)),
                Component.text("Assists: ").append(getSidebarComponent(statInformation.getAssists(), NamedTextColor.GREEN)),
                Component.text("Deaths: ").append(getSidebarComponent(statInformation.getDeaths(), NamedTextColor.GREEN)),
                Component.empty(),
                Component.text("Wins: ").append(getSidebarComponent(statInformation.getWins(), NamedTextColor.GREEN)),
                Component.text("Losses: ").append(getSidebarComponent(statInformation.getLosses(), NamedTextColor.GREEN)),
                Component.empty(),
                Component.text("Damage: ").append(getSidebarComponent(statInformation.getDamage(), NamedTextColor.RED)),
                Component.text("Healing: ").append(getSidebarComponent(statInformation.getHealing(), NamedTextColor.DARK_GREEN)),
                Component.text("Absorbed: ").append(getSidebarComponent(statInformation.getAbsorbed(), NamedTextColor.GOLD)),
                Component.empty(),
                UPDATE_TEXT,
                VERSION_TEXT
        );
    }
}