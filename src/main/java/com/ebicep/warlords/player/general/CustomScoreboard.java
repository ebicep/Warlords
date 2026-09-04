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
import java.util.ArrayList;
import java.util.Collection;
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
    private static final TextComponent VERSION_TEXT = Component.text("   " + Warlords.VERSION, Warlords.VERSION_COLOR, TextDecoration.BOLD);


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

    public static void removePlayerScoreboard(UUID uuid) {
        PLAYER_SCOREBOARDS.remove(uuid);
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
        Component oldPrefix = scoreboardTeam.prefix();
        Component newPrefix = entry.compact();
        if (!oldPrefix.equals(newPrefix)) {
            scoreboardTeam.prefix(newPrefix);
        }
    }

    private record LobbyNameDisplay(String name, Component prefix, Component suffix, NamedTextColor color) {}

    private static boolean isInLobby(Player player) {
        return Warlords.getGameManager().getPlayerGame(player.getUniqueId()).isEmpty();
    }

    private static LobbyNameDisplay buildLobbyNameDisplay(Player player) {
        Component suffix = Component.empty();
        Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player.getUniqueId());
        if (guildPlayerPair != null && guildPlayerPair.getA().getTag() != null) {
            GuildTag tag = guildPlayerPair.getA().getTag();
            suffix = Component.space().append(tag.getTag(false)).compact();
        }
        return new LobbyNameDisplay(
                player.getName(),
                Permissions.getPrefixWithColor(player, false).compact(),
                suffix,
                Permissions.getColor(player)
        );
    }

    private static List<LobbyNameDisplay> buildLobbyNameDisplays() {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        List<LobbyNameDisplay> displays = new ArrayList<>(onlinePlayers.size());
        for (Player onlinePlayer : onlinePlayers) {
            if (!isInLobby(onlinePlayer)) {
                continue;
            }
            displays.add(buildLobbyNameDisplay(onlinePlayer));
        }
        return displays;
    }

    private void applyLobbyNameDisplay(LobbyNameDisplay display) {
        Team team = scoreboard.getTeam(display.name());
        if (team == null) {
            team = scoreboard.registerNewTeam(display.name());
        }
        if (!team.hasEntry(display.name())) {
            team.addEntry(display.name());
        }
        if (!team.prefix().equals(display.prefix())) {
            team.prefix(display.prefix());
        }
        if (!team.suffix().equals(display.suffix())) {
            team.suffix(display.suffix());
        }
        if (!team.hasColor() || team.color() != display.color()) {
            team.color(display.color());
        }
    }

    private void applyLobbyNameDisplays(List<LobbyNameDisplay> displays) {
        for (LobbyNameDisplay display : displays) {
            applyLobbyNameDisplay(display);
        }
    }

    public static void updateLobbyPlayerNames() {
        List<LobbyNameDisplay> displays = buildLobbyNameDisplays();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!isInLobby(onlinePlayer)) {
                continue;
            }
            CustomScoreboard.getPlayerScoreboard(onlinePlayer).applyLobbyNameDisplays(displays);
        }
    }

    public static void applyLobbyPlayerNameToOthers(Player joined) {
        if (!isInLobby(joined)) {
            return;
        }
        LobbyNameDisplay display = buildLobbyNameDisplay(joined);
        UUID joinedUuid = joined.getUniqueId();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getUniqueId().equals(joinedUuid) || !isInLobby(onlinePlayer)) {
                continue;
            }
            CustomScoreboard.getPlayerScoreboard(onlinePlayer).applyLobbyNameDisplay(display);
        }
    }

    public void updateLobbyPlayerNamesInternal() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !isInLobby(player)) {
            return;
        }
        applyLobbyNameDisplays(buildLobbyNameDisplays());
    }

    public static CustomScoreboard getPlayerScoreboard(Player player) {
        return getPlayerScoreboard(player.getUniqueId());
    }

    private final UUID uuid;
    private final Scoreboard scoreboard;
    private Objective sideBar;
    private Objective health;

    public UUID getUuid() {
        return uuid;
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
            validatePlayerHolograms(uuid);
            PlayerLeaderboardInfo playerLeaderboardInfo = PLAYER_LEADERBOARD_INFOS.get(uuid);
            GameType selectedGameType = playerLeaderboardInfo.getStatsGameType();
            if (GameType.isPve(selectedGameType)) {
                givePvESidebar();
                return;
            }
            PlayersCollections selectedCollection = playerLeaderboardInfo.getStatsTime();
            int statsCategory = playerLeaderboardInfo.getStatsCategory();

            StatsLeaderboardCategory<?, ?, ?> statsLeaderboardCategory = getLeaderboardCategoryFromUUID(uuid);
            if (statsLeaderboardCategory == null) {
                DatabasePlayer databasePlayer = DatabaseManager.getPlayer(uuid);
                givePvPSidebar("Lifetime", databasePlayer);
                return;
            }
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
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(uuid);
        givePvPSidebar("Lifetime", databasePlayer);
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
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(uuid);
        givePvESidebar(databasePlayer.getPveStats(), true);
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
