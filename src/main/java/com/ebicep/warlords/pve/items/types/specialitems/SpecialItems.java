package com.ebicep.warlords.pve.items.types.specialitems;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.types.SpecialItem;
import com.ebicep.warlords.pve.items.types.specialitems.gammagauntlet.*;

import java.util.function.Function;

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


    ;

    public final Function<ItemTier, SpecialItem> create;

    SpecialItems(Function<ItemTier, SpecialItem> create) {
        this.create = create;
    }
}