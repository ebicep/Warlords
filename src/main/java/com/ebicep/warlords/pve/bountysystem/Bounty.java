package com.ebicep.warlords.pve.bountysystem;

import com.ebicep.warlords.pve.bountysystem.bounties.*;
import com.ebicep.warlords.pve.bountysystem.rewards.*;
import com.ebicep.warlords.pve.bountysystem.rewards.events.GardenOfHesperides1;
import com.ebicep.warlords.pve.bountysystem.rewards.events.GardenOfHesperides2;
import com.ebicep.warlords.pve.bountysystem.rewards.events.LibraryArchives1;
import com.ebicep.warlords.pve.bountysystem.rewards.events.LibraryArchives2;

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
    HUNT_BASIC_I(HuntBasicI.class, HuntBasicI::new),
    HUNT_INTERMEDIATE_I(HuntIntermediateI.class, HuntIntermediateI::new),
    HUNT_ADVANCED_I(HuntAdvancedI.class, HuntAdvancedI::new),
    HUNT_ELITE_I(HuntEliteI.class, HuntEliteI::new),
    HUNT_CHAMPION_I(HuntChampionI.class, HuntChampionI::new),
    HUNT_VOID_I(HuntVoidI.class, HuntVoidI::new),
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
    MASONRY_I(MasonryI.class, MasonryI::new),
    ARTISAN_I(ArtisanI.class, ArtisanI::new),
    CONSUMER_I(ConsumerI.class, ConsumerI::new),
    BOUNDLESS_I(BoundlessI.class, BoundlessI::new),
    DEDICATION_I(DedicationI.class, DedicationI::new),
    REPUTATION_I(ReputationI.class, ReputationI::new),
    IMPAIRMENT_I(ImpairmentI.class, ImpairmentI::new),
    ENTHRALL_I(EnthrallI.class, EnthrallI::new),
    MEND_I(MendI.class, MendI::new),
    AMASS_I(AmassI.class, AmassI::new),
    // garden of hesperides event
    ACROPOLIS_SLAYER_I(AcropolisSlayerI.class, AcropolisSlayerI::new),
    STATE_OF_MIND_I(StateOfMindI.class, StateOfMindI::new),
    TERAS_TORMENT_I(TerasTormentI.class, TerasTormentI::new),
    SPREE_I(SpreeI.class, SpreeI::new),
    ACROPOLIS_FLAWLESS_I(AcropolisFlawlessI.class, AcropolisFlawlessI::new),
    TARTARUS_SLAYER_I(TartarusSlayerI.class, TartarusSlayerI::new),
    ORDER_OF_THINGS_I(OrderOfThingsI.class, OrderOfThingsI::new),
    TARTARUS_FLAWLESS_I(TartarusFlawlessI.class, TartarusFlawlessI::new),
    WITHIN_THE_TIME_I(WithinTheTimeI.class, WithinTheTimeI::new),
    TAKE_MY_TITLE_I(TakeMyTitleI.class, TakeMyTitleI::new),
    // library archives event
    ARCHIVIST_HUNTER_I(ArchivistHunterI.class, ArchivistHunterI::new),
    CODEX_COLLECTOR_I(CodexCollectorI.class, CodexCollectorI::new),
    GRIMOIRES_GRIEF_I(GrimoiresGriefI.class, GrimoiresGriefI::new),
    THAT_WAS_CLOSE_I(ThatWasCloseI.class, ThatWasCloseI::new),
    GRAVEYARD_FLAWLESS_I(GraveyardFlawlessI.class, GraveyardFlawlessI::new),
    FORGOTTEN_SLAYER_I(ForgottenSlayerI.class, ForgottenSlayerI::new),
    CODEX_CHAOS_I(CodexChaosI.class, CodexChaosI::new),
    FORGOTTEN_FLAWLESS_I(ForgottenFlawlessI.class, ForgottenFlawlessI::new),
    CODEX_CONTINUED_I(CodexContinuedI.class, CodexContinuedI::new),
    TAKE_MY_TITLE_II(TakeMyTitleII.class, TakeMyTitleII::new),

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
        EVENT_GARDEN_OF_HESPERIDES_1(getBountyFrom(GardenOfHesperides1.class)),
        EVENT_GARDEN_OF_HESPERIDES_2(getBountyFrom(GardenOfHesperides2.class)),
        EVENT_GARDEN_OF_HESPERIDES_ALL(Stream.of(EVENT_GARDEN_OF_HESPERIDES_1, EVENT_GARDEN_OF_HESPERIDES_2)
                                             .flatMap(bountyGroup -> Arrays.stream(bountyGroup.bounties))
                                             .toArray(Bounty[]::new)),
        EVENT_LIBRARY_ARCHIVES_1(getBountyFrom(LibraryArchives1.class)),
        EVENT_LIBRARY_ARCHIVES_2(getBountyFrom(LibraryArchives2.class)),
        EVENT_LIBRARY_ARCHIVES_ALL(Stream.of(EVENT_LIBRARY_ARCHIVES_1, EVENT_LIBRARY_ARCHIVES_2)
                                         .flatMap(bountyGroup -> Arrays.stream(bountyGroup.bounties))
                                         .toArray(Bounty[]::new)),

        ;

        public final Bounty[] bounties;

        BountyGroup(Bounty[] bounties) {
            this.bounties = bounties;
        }
    }
}
