package com.ebicep.warlords.database.leaderboards.guilds;

import com.ebicep.holograms.Hologram;
import com.ebicep.holograms.HologramDataText;
import com.ebicep.holograms.HologramManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.leaderboards.PlayerLeaderboardInfo;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.events.pojos.GameEvents;
import com.ebicep.warlords.database.repositories.timings.pojos.Timing;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;

public class GuildLeaderboardManager {

    private static final int MAX_PAGES = 5;
    private static final int GUILDS_PER_PAGE = 10;
    public static final HashMap<Timing, TreeSet<Guild>> EXPERIENCE_LEADERBOARD = new HashMap<>() {{
        for (Timing value : Timing.VALUES) {
            put(value, new TreeSet<>((g1, g2) -> Long.compare(g2.getExperience(value), g1.getExperience(value))));
        }
    }};
    public static final HashMap<Timing, TreeSet<Guild>> COINS_LEADERBOARD = new HashMap<>() {{
        for (Timing value : Timing.VALUES) {
            put(value, new TreeSet<>((g1, g2) -> Long.compare(g2.getCoins(value), g1.getCoins(value))));
        }
    }};
    public static final Location EVENT_LEADERBOARD_LOCATION = new Location(StatsLeaderboardManager.MAIN_LOBBY, -2539.5, 55, 737.5);
    public static final List<Hologram> EVENT_LEADERBOARDS = new ArrayList<>();

    public static void recalculateAllLeaderboards() {
        EXPERIENCE_LEADERBOARD.forEach((timing, guilds) -> {
            guilds.clear();
            guilds.addAll(GuildManager.GUILDS);
        });
        COINS_LEADERBOARD.forEach((timing, guilds) -> {
            guilds.clear();
            guilds.addAll(GuildManager.GUILDS);
        });
        resetEventBoards();
    }

    public static void resetEventBoards() {
        EVENT_LEADERBOARDS.forEach(Hologram::deleteHologram);
        DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
        if (currentGameEvent == null) {
            return;
        }
        GameEvents event = currentGameEvent.getEvent();
        long startDateSecond = currentGameEvent.getStartDateSecond();

        List<Guild> sortedGuilds = new ArrayList<>(GuildManager.GUILDS);
        sortedGuilds.sort((o1, o2) -> o2.getEventStats().getOrDefault(event, new HashMap<>()).getOrDefault(startDateSecond, 0L)
                                        .compareTo(o1.getEventStats().getOrDefault(event, new HashMap<>()).getOrDefault(startDateSecond, 0L)));

        List<HologramDataText> pageHologramData = new ArrayList<>();
        int totalPages = Math.min(MAX_PAGES, (int) Math.ceil((double) sortedGuilds.size() / GUILDS_PER_PAGE));
        for (int i = 0; i < totalPages; i++) {
            pageHologramData.add(getPagedHologramData(event, sortedGuilds, i));
        }
        if (pageHologramData.isEmpty()) {
            return;
        }
        Hologram board = new Hologram.Builder(
                "guildEventLeaderboardPoints",
                EVENT_LEADERBOARD_LOCATION,
                p -> {
                    PlayerLeaderboardInfo playerInfo = StatsLeaderboardManager.getPlayerInfo(p);
                    int page = Math.min(playerInfo.getPage(), pageHologramData.size() - 1);
                    return pageHologramData.get(page);
                }
        ).build();
        HologramManager.addHologram(board);
        EVENT_LEADERBOARDS.add(board);
        Bukkit.getOnlinePlayers().forEach(GuildLeaderboardManager::resetVisibility);
    }

    public static void resetVisibility(Player player) {
        if (!Warlords.hologramsEnabled) {
            return;
        }
        StatsLeaderboardManager.validatePlayerHolograms(player);
        EVENT_LEADERBOARDS.forEach(hologram -> HologramManager.updateHologram(player, hologram));
    }

    private static HologramDataText getPagedHologramData(GameEvents event, List<Guild> sortedGuilds, int page) {
        ComponentBuilder componentBuilder = ComponentBuilder.create("Guild Event Points", NamedTextColor.AQUA, TextDecoration.BOLD)
                                                            .newLine(event.name, NamedTextColor.GRAY);

        for (int i = page * GUILDS_PER_PAGE; i < (page + 1) * GUILDS_PER_PAGE && i < sortedGuilds.size(); i++) {
            Guild guild = sortedGuilds.get(i);
            componentBuilder.newLine((i + 1) + ". ", NamedTextColor.YELLOW)
                            .text(guild.getName(), NamedTextColor.AQUA)
                            .text(" - ", NamedTextColor.GRAY)
                            .text(NumberFormat.addCommas(guild.getEventStats()
                                                              .getOrDefault(event, new HashMap<>())
                                                              .getOrDefault(DatabaseGameEvent.currentGameEvent.getStartDateSecond(), 0L)), NamedTextColor.YELLOW);
        }
        return new HologramDataText.Builder<>(componentBuilder.build())
                .setBillboard(Display.Billboard.VERTICAL)
                .build();
    }

    public static void recalculateLeaderboard(Timing timing) {
        EXPERIENCE_LEADERBOARD.get(timing).clear();
        EXPERIENCE_LEADERBOARD.get(timing).addAll(GuildManager.GUILDS);
        COINS_LEADERBOARD.get(timing).clear();
        COINS_LEADERBOARD.get(timing).addAll(GuildManager.GUILDS);
    }

    public static Component getLeaderboardList(TreeSet<Guild> leaderboard, String leaderboardName, Function<Guild, Number> valueFunction) {
        TextComponent.Builder componentBuilder = Component.text("Guild " + leaderboardName + " Leaderboards", NamedTextColor.GREEN)
                                                          .append(Component.newline())
                                                          .toBuilder();

        int index = 0;
        for (Guild guild : leaderboard) {
            componentBuilder.append(Component.text(index + 1 + ". ", NamedTextColor.GRAY))
                            .append(Component.text(guild.getName(), NamedTextColor.GOLD))
                            .append(Component.text(" - ", NamedTextColor.GRAY))
                            .append(Component.text(NumberFormat.addCommaAndRound(valueFunction.apply(guild).doubleValue())))
                            .append(Component.newline());
            index++;
        }

        return componentBuilder.build();
    }

}
