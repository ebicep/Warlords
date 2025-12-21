package com.ebicep.warlords.pve.bountysystem;

import com.ebicep.warlords.pve.bountysystem.bounties.*;
import com.ebicep.warlords.pve.bountysystem.bounties.boltaroevent.*;
import com.ebicep.warlords.pve.bountysystem.rewards.*;
import com.ebicep.warlords.pve.bountysystem.rewards.events.*;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

public enum Bounty {

    // Daily bounties
    ADVANCE_I(AdvanceI.class, AdvanceI::new),
    ADVANCE_II(AdvanceII.class, AdvanceII::new),
    CHALLENGE_I(ChallengeI.class, ChallengeI::new),
    CHALLENGE_II(ChallengeII.class, ChallengeII::new),
    CHARMING_I(CharmingI.class, CharmingI::new),
    EXPLORE_I(ExploreI.class, ExploreI::new),
    EXPLORE_II(ExploreII.class, ExploreII::new),
    FLAWLESS_I(FlawlessI.class, FlawlessI::new),
    FLAWLESS_II(FlawlessII.class, FlawlessII::new),
    FLAWLESS_III(FlawlessIII.class, FlawlessIII::new),
    FLAWLESS_IV(FlawlessIV.class, FlawlessIV::new),
    GAMBLER_I(GamblerI.class, GamblerI::new),
    HUNT_BOLTARO_I(HuntBoltaroI.class, HuntBoltaroI::new),
    HUNT_GHOULCALLER_I(HuntGhoulcallerI.class, HuntGhoulcallerI::new),
    HUNT_NARMER_I(HuntNarmerI.class, HuntNarmerI::new),
    HUNT_MITHRA_I(HuntMithraI.class, HuntMithraI::new),
    HUNT_ZENITH_I(HuntZenithI.class, HuntZenithI::new),
    LUCKY_I(LuckyI.class, LuckyI::new),
    RECOUP_I(RecoupI.class, RecoupI::new),
    PURSUE_I(PursueI.class, PursueI::new),
    SALVAGE_I(SalvageI.class, SalvageI::new),
    SALVAGE_II(SalvageII.class, SalvageII::new),
    SALVAGE_III(SalvageIII.class, SalvageIII::new),
    SKIRMISH_I(SkirmishI.class, SkirmishI::new),
    SLAYER_I(SlayerI.class, SlayerI::new),
    SLAYER_II(SlayerII.class, SlayerII::new),
    SYNTHWEAVE_I(SynthweaveI.class, SynthweaveI::new),
    // Weekly bounties
    ADVANCE_III(AdvanceIII.class, AdvanceIII::new),
    ADVANCE_IV(AdvanceIV.class, AdvanceIV::new),
    BRUTE_I(BruteI.class, BruteI::new),
    CHALLENGE_III(ChallengeIII.class, ChallengeIII::new),
    CHALLENGE_IV(ChallengeIV.class, ChallengeIV::new),
    CHALLENGE_V(ChallengeV.class, ChallengeV::new),
    CHALLENGE_VI(ChallengeVI.class, ChallengeVI::new),
    CHALLENGE_VII(ChallengeVII.class, ChallengeVII::new),
    DEVELOP_I(DevelopI.class, DevelopI::new),
    DEVELOP_II(DevelopII.class, DevelopII::new),
    DEVELOP_III(DevelopIII.class, DevelopIII::new),
    DEVELOP_IV(DevelopIV.class, DevelopIV::new),
    DUE_ON_TIME_I(DueOnTimeI.class, DueOnTimeI::new),
    FLAWLESS_V(FlawlessV.class, FlawlessV::new),
    HUNT_BASIC_I(HuntBasicI.class, HuntBasicI::new),
    HUNT_INTERMEDIATE_I(HuntIntermediateI.class, HuntIntermediateI::new),
    HUNT_ADVANCED_I(HuntAdvancedI.class, HuntAdvancedI::new),
    HUNT_ELITE_I(HuntEliteI.class, HuntEliteI::new),
    HUNT_CHAMPION_I(HuntChampionI.class, HuntChampionI::new),
    HUNT_VOID_I(HuntVoidI.class, HuntVoidI::new),
    NONCOMPLIANCE_I(NoncomplianceI.class, NoncomplianceI::new),
    SEPARATION_I(SeparationI.class, SeparationI::new),
    SALVAGE_IV(SalvageIV.class, SalvageIV::new),
    SALVAGE_V(SalvageV.class, SalvageV::new),
    SALVAGE_VI(SalvageVI.class, SalvageVI::new),
    SCRAP_I(ScrapI.class, ScrapI::new),
    SCRAP_II(ScrapII.class, ScrapII::new),
    SCRAP_III(ScrapIII.class, ScrapIII::new),
    SLASHER_I(SlasherI.class, SlasherI::new),
    TAKE_THAT_BACK_I(TakeThatBackI.class, TakeThatBackI::new),
    THRIVE_I(ThriveI.class, ThriveI::new),
    // Lifetime bounties
    AMASS_I(AmassI.class, AmassI::new),
    ARTISAN_I(ArtisanI.class, ArtisanI::new),
    BOUNDLESS_I(BoundlessI.class, BoundlessI::new),
    CONSUMER_I(ConsumerI.class, ConsumerI::new),
    DEDICATION_I(DedicationI.class, DedicationI::new),
    ENTHRALL_I(EnthrallI.class, EnthrallI::new),
    IMPAIRMENT_I(ImpairmentI.class, ImpairmentI::new),
    MASONRY_I(MasonryI.class, MasonryI::new),
    MEND_I(MendI.class, MendI::new),
    REFINED_I(RefinedI.class, RefinedI::new),
    REPUTATION_I(ReputationI.class, ReputationI::new),
    SYNTHESIZER_I(SynthesizerI.class, SynthesizerI::new),
    // Fighters Glory event
    BOLTAROS_BANE_I(BoltarosBaneI.class, BoltarosBaneI::new),
    BOLTARO_AND_GOLIATH_I(BoltaroAndGoliathI.class, BoltaroAndGoliathI::new),
    BONANZA_FLAWLESS_I(BonanzaFlawlessI.class, BonanzaFlawlessI::new),
    EXTERMINATOR_I(ExterminatorI.class, ExterminatorI::new),
    HUNT_LAIR_I(HuntLairI.class, HuntLairI::new),
    INTO_THE_SHADOW_I(IntoTheShadowI.class, IntoTheShadowI::new),
    LAIR_FLAWLESS_I(LairFlawlessI.class, LairFlawlessI::new),
    COWARD_I(CowardI.class, CowardI::new),
    TAKE_MY_TITLE_III(TakeMyTitleIII.class, TakeMyTitleIII::new),
    BOLTAROS_ADVANCE_I(BoltarosAdvanceI.class, BoltarosAdvanceI::new),
    // Garden of Hesperides event
    ACROPOLIS_FLAWLESS_I(AcropolisFlawlessI.class, AcropolisFlawlessI::new),
    ACROPOLIS_SLAYER_I(AcropolisSlayerI.class, AcropolisSlayerI::new),
    ORDER_OF_THINGS_I(OrderOfThingsI.class, OrderOfThingsI::new),
    SPREE_I(SpreeI.class, SpreeI::new),
    STATE_OF_MIND_I(StateOfMindI.class, StateOfMindI::new),
    TAKE_MY_TITLE_I(TakeMyTitleI.class, TakeMyTitleI::new),
    TARTARUS_FLAWLESS_I(TartarusFlawlessI.class, TartarusFlawlessI::new),
    TARTARUS_SLAYER_I(TartarusSlayerI.class, TartarusSlayerI::new),
    TERAS_TORMENT_I(TerasTormentI.class, TerasTormentI::new),
    WITHIN_THE_TIME_I(WithinTheTimeI.class, WithinTheTimeI::new),
    // Library Archives event
    ARCHIVIST_HUNTER_I(ArchivistHunterI.class, ArchivistHunterI::new),
    CODEX_CHAOS_I(CodexChaosI.class, CodexChaosI::new),
    CODEX_COLLECTOR_I(CodexCollectorI.class, CodexCollectorI::new),
    CODEX_CONTINUED_I(CodexContinuedI.class, CodexContinuedI::new),
    FORGOTTEN_FLAWLESS_I(ForgottenFlawlessI.class, ForgottenFlawlessI::new),
    FORGOTTEN_SLAYER_I(ForgottenSlayerI.class, ForgottenSlayerI::new),
    GRAVEYARD_FLAWLESS_I(GraveyardFlawlessI.class, GraveyardFlawlessI::new),
    GRIMOIRES_GRIEF_I(GrimoiresGriefI.class, GrimoiresGriefI::new),
    TAKE_MY_TITLE_II(TakeMyTitleII.class, TakeMyTitleII::new),
    THAT_WAS_CLOSE_I(ThatWasCloseI.class, ThatWasCloseI::new),
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
        EVENT_FIGHTERS_GLORY_1(getBountyFrom(FightersGloryReward1.class)),
        EVENT_FIGHTERS_GLORY_2(getBountyFrom(FightersGloryReward2.class)),
        EVENT_FIGHTERS_GLORY_ALL(Stream.of(EVENT_FIGHTERS_GLORY_1, EVENT_FIGHTERS_GLORY_2)
                .flatMap(bountyGroup -> Arrays.stream(bountyGroup.bounties))
                .toArray(Bounty[]::new)),
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
