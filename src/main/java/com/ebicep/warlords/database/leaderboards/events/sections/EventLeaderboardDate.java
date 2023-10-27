//package com.ebicep.warlords.database.leaderboards.events.sections;
//
//import com.ebicep.warlords.database.leaderboards.events.EventLeaderboard;
//import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardLocations;
//import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
//import com.ebicep.warlords.database.repositories.player.pojos.pve.events.PvEEventDatabaseStatInformation;
//import com.ebicep.warlords.util.bukkit.LocationBuilder;
//import com.ebicep.warlords.util.java.NumberFormat;
//import me.filoghost.holographicdisplays.api.hologram.Hologram;
//
//import java.util.List;
//
//public abstract class EventLeaderboardDate<T extends AbstractDatabaseStatInformation> {
//
//    protected final Long time;
//    protected final List<EventLeaderboardCategory<T>> eventCategories;
//
//    public EventLeaderboardDate(Long time, List<EventLeaderboardCategory<T>> eventCategories) {
//        this.time = time;
//        this.eventCategories = eventCategories;
//    }
//
//    public void addLeaderboards() {
//        for (EventLeaderboardCategory<T> category : eventCategories) {
//        }
//    }
//
//    public void resetLeaderboards(Long eventTime) {
//        for (EventLeaderboardCategory<T> eventCategory : eventCategories) {
//            eventCategory.resetLeaderboards(eventTime, null, eventCategory.getCategoryName(), "?????");
//        }
//    }
//
//    public abstract void addExtraLeaderboards(EventLeaderboardCategory<T> eventLeaderboardCategory);
//
//    public void addBaseLeaderboards(EventLeaderboardCategory<T> eventLeaderboardCategory) {
//        eventLeaderboardCategory.getAllHolograms().forEach(Hologram::delete);
//
//        List<EventLeaderboard> eventLeaderboards = eventLeaderboardCategory.getEventLeaderboards();
//        eventLeaderboards.clear();
//
//        eventLeaderboards.add(new EventLeaderboard(
//                time,
//                "Highest Game Event Points",
//                StatsLeaderboardLocations.MAIN_LOBBY,
//                (databasePlayer, time) -> eventLeaderboardCategory.getStatFunction().apply(databasePlayer, time).getHighestEventPointsGame(),
//                (databasePlayer, time) -> NumberFormat.addCommaAndRound(eventLeaderboardCategory.getStatFunction().apply(databasePlayer).getHighestEventPointsGame())
//        ));
//        eventLeaderboards.add(new EventLeaderboard(
//                time,
//                "Event Points",
//                new LocationBuilder(StatsLeaderboardLocations.MAIN_LOBBY).forward(10),
//                (databasePlayer, time) -> eventLeaderboardCategory.getStatFunction().apply(databasePlayer, time).getHighestEventPointsGame(),
//                (databasePlayer, time) -> NumberFormat.addCommaAndRound(eventLeaderboardCategory.getStatFunction().apply(databasePlayer).getHighestEventPointsGame())
//        ));
//
//        this.addExtraLeaderboards(eventLeaderboardCategory);
//    }
//}
