package com.ebicep.warlords.pve.bountysystem;

import com.ebicep.warlords.pve.bountysystem.bounties.*;
import com.ebicep.warlords.pve.bountysystem.rewards.DailyRewardSpendable1;

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

    ;

    public static final Bounty[] VALUES = values();
    public static final Bounty[] DAILY_1 = Arrays.stream(VALUES)
                                                 .filter(bounties -> DailyRewardSpendable1.class.isAssignableFrom(bounties.clazz))
                                                 .toArray(Bounty[]::new);

    public final Class<?> clazz;
    public final Supplier<AbstractBounty> create;

    Bounty(Class<?> clazz, Supplier<AbstractBounty> create) {
        this.clazz = clazz;
        this.create = create;
    }

    public enum BountyGroup {
        DAILY_1(Bounty.DAILY_1),
        ;

        public final Bounty[] bounties;

        BountyGroup(Bounty[] bounties) {
            this.bounties = bounties;
        }
    }
}
