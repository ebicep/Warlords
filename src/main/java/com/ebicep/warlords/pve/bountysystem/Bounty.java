package com.ebicep.warlords.pve.bountysystem;

import com.ebicep.warlords.pve.bountysystem.bounties.*;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable2;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable3;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable4;

import java.util.Arrays;
import java.util.function.Supplier;

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

        ;

        public final Bounty[] bounties;

        BountyGroup(Bounty[] bounties) {
            this.bounties = bounties;
        }
    }
}
