package com.ebicep.warlords.pve.bountysystem;

import com.ebicep.warlords.pve.bountysystem.bounties.*;
import com.ebicep.warlords.pve.bountysystem.rewards.*;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

public enum Bounty {

    SLAYER1(Slayer1.class, Slayer1::new),
    SLAYER2(Slayer2.class, Slayer2::new),
    CHARMING(Charming.class, Charming::new),
    PURSUE(Pursue.class, Pursue::new),
    EXPLORE1(Explore1.class, Explore1::new),
    SKIRMISH(Skirmish.class, Skirmish::new),
    LUCKY(Lucky.class, Lucky::new),
    EXPLORE2(Explore2.class, Explore2::new),
    SALVAGE1(Salvage1.class, Salvage1::new),
    SALVAGE2(Salvage2.class, Salvage2::new),
    HUNT_BOLTARO(HuntBoltaro.class, HuntBoltaro::new),
    HUNT_GHOULCALLER(HuntGhoulcaller.class, HuntGhoulcaller::new),
    HUNT_NARMER(HuntNarmer.class, HuntNarmer::new),
    HUNT_MITHRA(HuntMithra.class, HuntMithra::new),
    HUNT_ZENITH(HuntZenith.class, HuntZenith::new),
    FLAWLESS1(Flawless1.class, Flawless1::new),
    FLAWLESS2(Flawless2.class, Flawless2::new),
    FLAWLESS3(Flawless3.class, Flawless3::new),
    FLAWLESS4(Flawless4.class, Flawless4::new),
    ADVANCE1(Advance1.class, Advance1::new),
    ADVANCE2(Advance2.class, Advance2::new),
    CHALLENGE1(Challenge1.class, Challenge1::new),
    CHALLENGE2(Challenge2.class, Challenge2::new),
    RECOUP(Recoup.class, Recoup::new),
    SALVAGE3(Salvage3.class, Salvage3::new),
    THRIVE(Thrive.class, Thrive::new),
    BRUTE(Brute.class, Brute::new),
    DEVELOP1(Develop1.class, Develop1::new),
    DEVELOP2(Develop2.class, Develop2::new),
    DEVELOP3(Develop3.class, Develop3::new),
    DEVELOP4(Develop4.class, Develop4::new),
    SEPARATION(Separation.class, Separation::new),
    SLASHER(Slasher.class, Slasher::new),
    NONCOMPLIANCE(Noncompliance.class, Noncompliance::new),

    ;

    public static final Bounty[] VALUES = values();

    public static Bounty[] getBountyFrom(Class<?> rewardSpendable) {
        return Arrays.stream(VALUES)
                     .filter(bounties -> rewardSpendable.isAssignableFrom(bounties.clazz))
                     .toArray(Bounty[]::new);
    }

    public final Class<?> clazz;
    public final Supplier<AbstractBounty> create;

    Bounty(Class<?> clazz, Supplier<AbstractBounty> create) {
        this.clazz = clazz;
        this.create = create;
    }

    public enum BountyGroup {
        DAILY_1(getBountyFrom(DailyRewardSpendable1.class)),
        DAILY_2(getBountyFrom(DailyRewardSpendable2.class)),
        DAILY_3(getBountyFrom(DailyRewardSpendable3.class)),
        DAILY_4(getBountyFrom(DailyRewardSpendable4.class)),
        DAILY_ALL(Stream.of(DAILY_1, DAILY_2, DAILY_3, DAILY_4).flatMap(bountyGroup -> Arrays.stream(bountyGroup.bounties)).toArray(Bounty[]::new)),
        WEEKLY_1(getBountyFrom(WeeklyRewardSpendable1.class)),
        WEEKLY_2(getBountyFrom(WeeklyRewardSpendable2.class)),
        WEEKLY_3(getBountyFrom(WeeklyRewardSpendable3.class)),
        WEEKLY_ALL(Stream.of(WEEKLY_1, WEEKLY_2, WEEKLY_3).flatMap(bountyGroup -> Arrays.stream(bountyGroup.bounties)).toArray(Bounty[]::new)),
        LIFETIME_1(getBountyFrom(LifetimeRewardSpendable1.class)),
        LIFETIME_2(getBountyFrom(LifetimeRewardSpendable2.class)),
        LIFETIME_3(getBountyFrom(LifetimeRewardSpendable3.class)),
        LIFETIME_ALL(Stream.of(LIFETIME_1, LIFETIME_2, LIFETIME_3).flatMap(bountyGroup -> Arrays.stream(bountyGroup.bounties)).toArray(Bounty[]::new)),

        ;

        public final Bounty[] bounties;

        BountyGroup(Bounty[] bounties) {
            this.bounties = bounties;
        }
    }
}
