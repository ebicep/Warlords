package com.ebicep.warlords.database.leaderboards.sections.subsections;

import com.ebicep.warlords.database.leaderboards.Leaderboard;
import com.ebicep.warlords.database.leaderboards.sections.LeaderboardCategory;
import com.ebicep.warlords.database.leaderboards.sections.LeaderboardGameType;
import com.ebicep.warlords.database.repositories.player.pojos.ctf.DatabasePlayerCTF;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.Location;

import java.util.List;

public class LeaderboardCTF extends LeaderboardGameType<DatabasePlayerCTF> {

    public LeaderboardCTF() {
        super(
                new LeaderboardCategory<>(DatabasePlayer::getCtfStats),
                new LeaderboardCategory<>(databasePlayer -> databasePlayer.getCompStats().getCtfStats()),
                new LeaderboardCategory<>(databasePlayer -> databasePlayer.getPubStats().getCtfStats())
        );
    }

    public void addLeaderboards() {
        addBaseLeaderboards(general);
        addBaseLeaderboards(comps);
        addBaseLeaderboards(pubs);
    }

    @Override
    public void addExtraLeaderboards(LeaderboardCategory<DatabasePlayerCTF> leaderboardCategory) {
        List<Leaderboard> leaderboards = leaderboardCategory.getLeaderboards();
        leaderboards.add(new Leaderboard("Flags Captured", new Location(world, -2540.5, 56, 712.5),
                databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getFlagsCaptured(),
                databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getFlagsCaptured())));
        leaderboards.add(new Leaderboard("Flags Returned", new Location(world, -2608.5, 52, 737.5),
                databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getFlagsReturned(),
                databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getFlagsReturned())));

        leaderboards.add(new Leaderboard("Mage Experience", new Location(world, -2523.5, 58, 734.5),
                databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getMage().getExperience(),
                databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getMage().getExperience())));
        leaderboards.add(new Leaderboard("Warrior Experience", new Location(world, -2520.5, 58, 739.5),
                databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getWarrior().getExperience(),
                databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getWarrior().getExperience())));
        leaderboards.add(new Leaderboard("Paladin Experience", new Location(world, -2516.5, 58, 744.5),
                databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getPaladin().getExperience(),
                databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getPaladin().getExperience())));
        leaderboards.add(new Leaderboard("Shaman Experience", new Location(world, -2520.5, 58, 749.5),
                databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getShaman().getExperience(),
                databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getShaman().getExperience())));
        leaderboards.add(new Leaderboard("Rogue Experience", new Location(world, -2523.5, 58, 754.5),
                databasePlayer -> leaderboardCategory.statFunction.apply(databasePlayer).getRogue().getExperience(),
                databasePlayer -> NumberFormat.addCommaAndRound(leaderboardCategory.statFunction.apply(databasePlayer).getRogue().getExperience())));
    }
}
