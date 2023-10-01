package com.ebicep.warlords.pve.items.types.specialitems;

import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractSpecialItem;
import com.ebicep.warlords.pve.items.types.specialitems.buckler.delta.*;
import com.ebicep.warlords.pve.items.types.specialitems.buckler.gamma.*;
import com.ebicep.warlords.pve.items.types.specialitems.buckler.omega.*;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.delta.*;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma.*;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega.*;
import com.ebicep.warlords.pve.items.types.specialitems.tome.delta.*;
import com.ebicep.warlords.pve.items.types.specialitems.tome.gamma.*;
import com.ebicep.warlords.pve.items.types.specialitems.tome.omega.*;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public enum SpecialItems {
    //GAMMA
    BLAZING_GLOVES(BlazingGloves::new),
    ARCHANGELS_FIST(ArchangelsFist::new),
    BARBARIC_CLAWS(BarbaricClaws::new),
    VOLTAGE_GAUNTLET(VoltageGauntlet::new),
    TOXIC_KNUCKLES(ToxicKnuckles::new),
    FREEZING_GLOVES(FreezingGloves::new),
    CONQUERORS_FIST(ConquerorsFist::new),
    PATRIOTIC_CLAWS(PatrioticClaws::new),
    DRIPPING_GLOVES(DrippingGloves::new),
    MEDIATORS_FIST(MediatorsFist::new),
    EIDOLONIC_CLAWS(EidolonicClaws::new),
    BIOME_GAUNTLET(BiomeGauntlet::new),
    HERBAL_KNUCKLES(HerbalKnuckles::new),
    SHADOW_GAUNTLET(ShadowGauntlet::new),
    BLUNT_KNUCKLES(BluntKnuckles::new),
    BOOK_OF_ACTS(BookOfActs::new),
    BOOK_OF_HELAMAN(BookOfHelaman::new),
    BOOK_OF_NEPHI(BookOfNephi::new),
    EXECUTIONERS_ALMANAC(ExecutionersAlmanac::new),
    GUIDE_TO_JUDO(GuideToJudo::new),
    GUIDE_TO_JUJITSU(GuideToJuJitsu::new),
    GUIDE_TO_TAI_CHI(GuideToTaiChi::new),
    LIBERATOR_ALMANAC(LiberatorAlmanac::new),
    NECROTIC_SCROLL(NecroticScroll::new),
    PHYSICIANS_ALMANAC(PhysiciansAlmanac::new),
    STATIC_SCROLL(StaticScroll::new),
    TERRA_SCROLL(TerraScroll::new),
    TOME_OF_FIRE(TomeOfFire::new),
    TOME_OF_ICE(TomeOfIce::new),
    TOME_OF_WATER(TomeOfWater::new),
    ANOINTED_AEGIS(AnointedAegis::new),
    BLOODY_CHAKRAM(BloodyChakram::new),
    BURNING_SHIELD(BurningShield::new),
    CLANDESTINE_BUCKLER(ClandestineBuckler::new),
    ENLIGHTENED_AEGIS(EnlightenedAegis::new),
    ETHEREAL_BULWARK(EtherealBulwark::new),
    FROSTY_SHIELD(FrostyShield::new),
    GALVANIC_BULWARK(GalvanicBulwark::new),
    HALLOWED_AEGIS(HallowedAegis::new),
    LOAMY_BULWARK(LoamyBulwark::new),
    LUCID_BUCKLER(LucidBuckler::new),
    OVERGROWN_BUCKLER(OvergrownBuckler::new),
    SLIPPERY_SHIELD(SlipperyShield::new),
    STALWART_CHAKRAM(StalwartChakram::new),
    THORNY_CHAKRAM(ThornyChakram::new),
    //DELTA
    SOOTHSAYERS_PALMS(SoothsayersPalms::new),
    SAMSONS_FISTS(SamsonsFists::new),
    PENDRAGON_KNUCKLES(PendragonGauntlets::new),
    GARDENING_GLOVES(GardeningGloves::new),
    MULTIPURPOSE_KNUCKLES(MultipurposeKnuckles::new),
    DIABOLICAL_RINGS(DiabolicalRings::new),

    FIREWATER_ALMANAC(FirewaterAlmanac::new), //TODO
    THE_PRESENT_TESTAMENT(ThePresentTestament::new),
    A_GUIDE_TO_MMA(AGuideToMMA::new),
    GHOUL_TOME(PansTome::new),
    SCROLL_OF_UNCERTAINTY(ScrollOfUncertainty::new),
    BRUISED_BOOK(BruisedBook::new),

    HAZARDOUS_BUCKLER(BucklerPiece::new),
    CROSS_NECKLACE_CHARM(CrossNecklaceCharm::new),
    PRIDWENS_BULWARK(PridwensBulwark::new),
    AERIAL_AEGIS(AerialAegis::new),
    SHIELD_OF_SNATCHING(ShieldOfSnatching::new),
    OTHERWORLDLY_AMULET(OtherworldlyAmulet::new),

    //OMEGA
    LILITHS_CLAWS(LilithsClaws::new),
    HANDS_OF_THE_HOLY_CORPSE(HandsOfTheHolyCorpse::new),
    GLASS_KNUCKLES(GlassKnuckles::new),
    NATURES_CLAWS(NaturesClaws::new),
    ROBIN_HOODS_GLOVES(RobinHoodsGloves::new),
    MONA_LISAS_PALMS(MonaLisasPalms::new),

    FLEMING_ALMANAC(FlemingAlmanac::new),
    COMMANDMENT_NO_ELEVEN(CommandmentNoEleven::new),
    SCROLL_OF_SANGUINITY(ScrollOfScripts::new),
    GUIDE_FOR_THE_RIVER_STYX(GuideForTheRiverStyx::new),
    TOME_OF_THEFT(TomeOfTheft::new),
    MYSTICKS_MANUAL_VOL_23_H(MysticksManualVol23H::new),

    ELEMENTAL_SHIELD(ElementalShield::new),
    BREASTPLATE_BUCKLER(BreastplateBuckler::new),
    ATHENIAN_AEGIS(AthenianAegis::new),
    WAXING_BULWARK(CrescentBulwark::new),
    DIRTY_CHAKRAM(ChakramOfBlades::new),
    LOVELY_OMAMORI(LovelyOmamori::new),

    ;

    public static final SpecialItems[] GAMMA_ITEMS = {
            BLAZING_GLOVES,
            ARCHANGELS_FIST,
            BARBARIC_CLAWS,
            VOLTAGE_GAUNTLET,
            TOXIC_KNUCKLES,
            FREEZING_GLOVES,
            CONQUERORS_FIST,
            PATRIOTIC_CLAWS,
            DRIPPING_GLOVES,
            MEDIATORS_FIST,
            EIDOLONIC_CLAWS,
            BIOME_GAUNTLET,
            HERBAL_KNUCKLES,
            SHADOW_GAUNTLET,
            BLUNT_KNUCKLES,
            BOOK_OF_ACTS,
            BOOK_OF_HELAMAN,
            BOOK_OF_NEPHI,
            EXECUTIONERS_ALMANAC,
            GUIDE_TO_JUDO,
            GUIDE_TO_JUJITSU,
            GUIDE_TO_TAI_CHI,
            LIBERATOR_ALMANAC,
            NECROTIC_SCROLL,
            PHYSICIANS_ALMANAC,
            STATIC_SCROLL,
            TERRA_SCROLL,
            TOME_OF_FIRE,
            TOME_OF_ICE,
            TOME_OF_WATER,
            ANOINTED_AEGIS,
            BLOODY_CHAKRAM,
            BURNING_SHIELD,
            CLANDESTINE_BUCKLER,
            ENLIGHTENED_AEGIS,
            ETHEREAL_BULWARK,
            FROSTY_SHIELD,
            GALVANIC_BULWARK,
            HALLOWED_AEGIS,
            LOAMY_BULWARK,
            LUCID_BUCKLER,
            OVERGROWN_BUCKLER,
            SLIPPERY_SHIELD,
            STALWART_CHAKRAM,
            THORNY_CHAKRAM
    };
    public static final SpecialItems[] DELTA_ITEMS = {
            SOOTHSAYERS_PALMS,
            SAMSONS_FISTS,
            PENDRAGON_KNUCKLES,
            GARDENING_GLOVES,
            MULTIPURPOSE_KNUCKLES,
            DIABOLICAL_RINGS,
            FIREWATER_ALMANAC,
            THE_PRESENT_TESTAMENT,
            A_GUIDE_TO_MMA,
            GHOUL_TOME,
            SCROLL_OF_UNCERTAINTY,
            HAZARDOUS_BUCKLER,
            CROSS_NECKLACE_CHARM,
            PRIDWENS_BULWARK,
            AERIAL_AEGIS,
            SHIELD_OF_SNATCHING,
            OTHERWORLDLY_AMULET,
    };
    public static final SpecialItems[] OMEGA_ITEMS = {
            LILITHS_CLAWS,
            HANDS_OF_THE_HOLY_CORPSE,
            GLASS_KNUCKLES,
            NATURES_CLAWS,
            ROBIN_HOODS_GLOVES,
            MONA_LISAS_PALMS,
            FLEMING_ALMANAC,
            COMMANDMENT_NO_ELEVEN,
            SCROLL_OF_SANGUINITY,
            GUIDE_FOR_THE_RIVER_STYX,
            TOME_OF_THEFT,
            MYSTICKS_MANUAL_VOL_23_H,
            ELEMENTAL_SHIELD,
            BREASTPLATE_BUCKLER,
            ATHENIAN_AEGIS,
            WAXING_BULWARK,
            DIRTY_CHAKRAM,
            LOVELY_OMAMORI,
    };

    public final Function<Set<BasicStatPool>, AbstractSpecialItem> create;

    SpecialItems(Function<Set<BasicStatPool>, AbstractSpecialItem> create) {
        this.create = create;
    }

    public AbstractSpecialItem create() {
        AbstractSpecialItem abstractSpecialItem = create.apply(new HashSet<>());
        return create.apply(abstractSpecialItem.getTier().generateStatPool());
    }
}