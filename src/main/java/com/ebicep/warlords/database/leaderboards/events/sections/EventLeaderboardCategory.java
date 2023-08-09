//package com.ebicep.warlords.database.leaderboards.events.sections;
//
//import com.ebicep.warlords.database.leaderboards.events.EventLeaderboard;
//import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
//import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
//import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventDatabaseStatInformation;
//import me.filoghost.holographicdisplays.api.hologram.Hologram;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.function.BiFunction;
//import java.util.function.Function;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//
//public class EventLeaderboardCategory<T extends AbstractDatabaseStatInformation> {
//
//    private final BiFunction<DatabasePlayer, Long, T> statFunction;
//    private final String categoryName;
//    private final List<EventLeaderboard> eventLeaderboards = new ArrayList<>();
//
//    public EventLeaderboardCategory(BiFunction<DatabasePlayer, Long, T> statFunction, String categoryName) {
//        this.statFunction = statFunction;
//        this.categoryName = categoryName;
//    }
//
//    public void resetLeaderboards(Long eventTime, Predicate<DatabasePlayer> externalFilter, String categoryName, String subTitle) {
//        for (EventLeaderboard eventLeaderboard : getEventLeaderboards()) {
//            eventLeaderboard.resetHolograms(eventTime, externalFilter, categoryName, subTitle);
//        }
//    }
//
//    public List<Hologram> getAllHolograms() {
//        return getEventLeaderboards().stream()
//                                     .flatMap(statsLeaderboard -> statsLeaderboard.getSortedTimedHolograms().values().stream())
//                                     .flatMap(Collection::stream)
//                                     .flatMap(Collection::stream)
//                                     .toList();
//    }
//
//    public List<EventLeaderboard> getEventLeaderboards() {
//        return eventLeaderboards;
//    }
//
//    public BiFunction<DatabasePlayer, Long, T> getStatFunction() {
//        return statFunction;
//    }
//
//    public String getCategoryName() {
//        return categoryName;
//    }
//}
