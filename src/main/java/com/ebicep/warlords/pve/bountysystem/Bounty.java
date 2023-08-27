package com.ebicep.warlords.pve.bountysystem;

import com.ebicep.warlords.pve.bountysystem.bounties.*;
import com.ebicep.warlords.pve.bountysystem.rewards.*;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

public enum Bounty {

    SLAYER_I(SlayerI.class, SlayerI::new),
    SLAYER_II(SlayerII.class, SlayerII::new),
    CHARMING_I(CharmingI.class, CharmingI::new),
    PURSUE_I(PursueI.class, PursueI::new),
    EXPLORE_I(ExploreI.class, ExploreI::new),
    SKIRMISH_I(SkirmishI.class, SkirmishI::new),
    LUCKY_I(LuckyI.class, LuckyI::new),
    EXPLORE_II(ExploreII.class, ExploreII::new),
    SALVAGE_I(SalvageI.class, SalvageI::new),
    SALVAGE_II(SalvageII.class, SalvageII::new),
    HUNT_BOLTARO_I(HuntBoltaroI.class, HuntBoltaroI::new),
    HUNT_GHOULCALLER_I(HuntGhoulcallerI.class, HuntGhoulcallerI::new),
    HUNT_NARMER_I(HuntNarmerI.class, HuntNarmerI::new),
    HUNT_MITHRA_I(HuntMithraI.class, HuntMithraI::new),
    HUNT_ZENITH_I(HuntZenithI.class, HuntZenithI::new),
    FLAWLESS_I(FlawlessI.class, FlawlessI::new),
    FLAWLESS_II(FlawlessII.class, FlawlessII::new),
    FLAWLESS_III(FlawlessIII.class, FlawlessIII::new),
    FLAWLESS_IV(FlawlessIV.class, FlawlessIV::new),
    ADVANCE_I(AdvanceI.class, AdvanceI::new),
    ADVANCE_II(AdvanceII.class, AdvanceII::new),
    CHALLENGE_I(ChallengeI.class, ChallengeI::new),
    CHALLENGE_II(ChallengeII.class, ChallengeII::new),
    RECOUP_I(RecoupI.class, RecoupI::new),
    SALVAGE_III(SalvageIII.class, SalvageIII::new),
    THRIVE_I(ThriveI.class, ThriveI::new),
    BRUTE_I(BruteI.class, BruteI::new),
    DEVELOP_I(DevelopI.class, DevelopI::new),
    DEVELOP_II(DevelopII.class, DevelopII::new),
    DEVELOP_III(DevelopIII.class, DevelopIII::new),
    DEVELOP_IV(DevelopIV.class, DevelopIV::new),
    SEPARATION_I(SeparationI.class, SeparationI::new),
    SLASHER_I(SlasherI.class, SlasherI::new),
    NONCOMPLIANCE_I(NoncomplianceI.class, NoncomplianceI::new),
    FLAWLESS_V(FlawlessV.class, FlawlessV::new),
    CHALLENGE_III(ChallengeIII.class, ChallengeIII::new),
    CHALLENGE_IV(ChallengeIV.class, ChallengeIV::new),
    CHALLENGE_V(ChallengeV.class, ChallengeV::new),
    CHALLENGE_VI(ChallengeVI.class, ChallengeVI::new),
    ADVANCE_III(AdvanceIII.class, AdvanceIII::new),
    ADVANCE_IV(AdvanceIV.class, AdvanceIV::new),
    CHALLENGE_VII(ChallengeVII.class, ChallengeVII::new),
    SALVAGE_IV(SalvageIV.class, SalvageIV::new),
    SALVAGE_V(SalvageV.class, SalvageV::new),
    SALVAGE_VI(SalvageVI.class, SalvageVI::new),

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
