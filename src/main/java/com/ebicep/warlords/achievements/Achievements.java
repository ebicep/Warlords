package com.ebicep.warlords.achievements;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.PlayerStatisticsSecond;
import com.ebicep.warlords.player.WarlordsPlayer;

import java.util.List;
import java.util.function.Predicate;

public enum Achievements {

    GAMES_PLAYED_50("Play 50 Games", databasePlayer -> databasePlayer.getPlays() > 50, null),
    GAMES_PLAYED_100("Play 100 Games", databasePlayer -> databasePlayer.getPlays() > 100, null),
    GAMES_PLAYED_250("Play 250 Games", databasePlayer -> databasePlayer.getPlays() > 250, null),
    GAMES_PLAYED_500("Play 500 Games", databasePlayer -> databasePlayer.getPlays() > 500, null),
    GAMES_PLAYED_1000("Play 1000 Games", databasePlayer -> databasePlayer.getPlays() > 1000, null),
    GAMES_WON_25("Win 25 Games", databasePlayer -> databasePlayer.getWins() > 25, null),
    GAMES_WON_50("Win 50 Games", databasePlayer -> databasePlayer.getWins() > 50, null),
    GAMES_WON_125("Win 125 Games", databasePlayer -> databasePlayer.getWins() > 125, null),
    GAMES_WON_250("Win 250 Games", databasePlayer -> databasePlayer.getWins() > 250, null),
    GAMES_WON_500("Win 500 Games", databasePlayer -> databasePlayer.getWins() > 500, null),
    GAMES_WON_CTF_10("Win 10 CTF Games", databasePlayer -> databasePlayer.getCtfStats().getWins() > 10, null),
    GAMES_WON_CTF_25("Win 25 CTF Games", databasePlayer -> databasePlayer.getCtfStats().getWins() > 25, null),
    GAMES_WON_CTF_50("Win 50 CTF Games", databasePlayer -> databasePlayer.getCtfStats().getWins() > 50, null),
    GAMES_WON_CTF_75("Win 75 CTF Games", databasePlayer -> databasePlayer.getCtfStats().getWins() > 75, null),
    GAMES_WON_CTF_100("Win 100 CTF Games", databasePlayer -> databasePlayer.getCtfStats().getWins() > 100, null),
    GAMES_WON_TDM_10("Win 10 TDM Games", databasePlayer -> databasePlayer.getTdmStats().getWins() > 10, null),
    GAMES_WON_TDM_25("Win 25 TDM Games", databasePlayer -> databasePlayer.getTdmStats().getWins() > 25, null),
    GAMES_WON_TDM_50("Win 50 TDM Games", databasePlayer -> databasePlayer.getTdmStats().getWins() > 50, null),
    GAMES_WON_TDM_75("Win 75 TDM Games", databasePlayer -> databasePlayer.getTdmStats().getWins() > 75, null),
    GAMES_WON_TDM_100("Win 100 TDM Games", databasePlayer -> databasePlayer.getTdmStats().getWins() > 100, null),

//    REJUVENATION("Rejuvenation", null, warlordsPlayer -> {
//        List<PlayerStatisticsSecond.Entry> entries = warlordsPlayer.getSecondStats().getEntries();
//        for (int i = 0; i < entries.size() - 3; i++) {
//            PlayerStatisticsSecond.Entry before = entries.get(i);
//            PlayerStatisticsSecond.Entry after = entries.get(i + 3);
//            if(before.getHealth() <= 1000 && after.getHealth() >)
//        }
//
//    }),
    ;

    String name;
    Predicate<DatabasePlayer> databasePlayerPredicate;
    Predicate<WarlordsPlayer> warlordsPlayerPredicate;

    Achievements(String name, Predicate<DatabasePlayer> databasePlayerPredicate, Predicate<WarlordsPlayer> warlordsPlayerPredicate) {
        this.name = name;
        this.databasePlayerPredicate = databasePlayerPredicate;
    }

    public void giveAchievements(DatabasePlayer databasePlayer) {
        if (databasePlayerPredicate.test(databasePlayer)) {

        }
    }
}
