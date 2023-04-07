package com.ebicep.warlords.pve.items.types.specialitems;

import com.ebicep.warlords.pve.items.types.SpecialItem;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.gamma.*;
import com.ebicep.warlords.pve.items.types.specialitems.tome.gamma.*;

import java.util.function.Supplier;

public enum SpecialItems {
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
    TOME_OF_WATER(TomeOfWater::new);

    public final Supplier<SpecialItem> create;

    SpecialItems(Supplier<SpecialItem> create) {
        this.create = create;
    }
}