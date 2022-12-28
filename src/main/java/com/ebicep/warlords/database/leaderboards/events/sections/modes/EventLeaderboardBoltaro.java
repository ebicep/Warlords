//package com.ebicep.warlords.database.leaderboards.events.sections.modes;
//
//import com.ebicep.warlords.database.leaderboards.events.sections.EventLeaderboardCategory;
//import com.ebicep.warlords.database.leaderboards.events.sections.EventLeaderboardDate;
//import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.DatabasePlayerPvEEventBoltaroStats;
//import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.boltaro.PvEEventBoltaroDatabaseStatInformation;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class EventLeaderboardBoltaro extends EventLeaderboardDate<PvEEventBoltaroDatabaseStatInformation> {
//
//    private static final List<EventLeaderboardCategory<DatabasePlayerPvEEventBoltaroStats>> CATEGORIES = new ArrayList<>() {{
//        add(new EventLeaderboardCategory<>((databasePlayer, time) -> databasePlayer.getPveStats().getEventStats().getBoltaroStats(), "General"));
//        add(new EventLeaderboardCategory<>((databasePlayer, time) -> databasePlayer.getPveStats().getEventStats().getBoltaroStats().getEvent(time).getLairStats(), "General"));
//        add(new EventLeaderboardCategory<>((databasePlayer, time) -> databasePlayer.getPveStats().getEventStats().getBoltaroStats(), "General"));
//    }};
//
//
//    public EventLeaderboardBoltaro() {
//        super(time, new A);
//    }
//
//    @Override
//    public void addExtraLeaderboards(EventLeaderboardCategory<PvEEventBoltaroDatabaseStatInformation> eventLeaderboardCategory) {
//
//    }
//}
