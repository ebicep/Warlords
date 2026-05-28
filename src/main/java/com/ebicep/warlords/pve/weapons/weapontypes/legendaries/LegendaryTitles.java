package com.ebicep.warlords.pve.weapons.weapontypes.legendaries;

import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles.*;
import org.bukkit.Material;

import java.util.UUID;
import java.util.function.Function;

public enum LegendaryTitles {

    NONE("", LegendaryWeapon.class, LegendaryWeapon::new, LegendaryWeapon::new, Material.WHITE_STAINED_GLASS_PANE, true),
    TITANIC("Titanic", LegendaryTitanic.class, LegendaryTitanic::new, LegendaryTitanic::new, Material.ORANGE_STAINED_GLASS_PANE, true),
    VIGOROUS("Vigorous", LegendaryVigorous.class, LegendaryVigorous::new, LegendaryVigorous::new, Material.MAGENTA_STAINED_GLASS_PANE, true),
    SUSPICIOUS("Suspicious", LegendarySuspicious.class, LegendarySuspicious::new, LegendarySuspicious::new, Material.LIGHT_BLUE_STAINED_GLASS_PANE, true),
    BENEVOLENT("Benevolent", LegendaryBenevolent.class, LegendaryBenevolent::new, LegendaryBenevolent::new, Material.YELLOW_STAINED_GLASS_PANE, true),
    VORPAL("Vorpal", LegendaryVorpal.class, LegendaryVorpal::new, LegendaryVorpal::new, Material.ORANGE_STAINED_GLASS_PANE, true),
    DIVINE("Divine", LegendaryDivine.class, LegendaryDivine::new, LegendaryDivine::new, Material.PINK_STAINED_GLASS_PANE, true),
    GALE("Gale", LegendaryGale.class, LegendaryGale::new, LegendaryGale::new, Material.GRAY_STAINED_GLASS_PANE, true),
    FERVENT("Fervent", LegendaryFervent.class, LegendaryFervent::new, LegendaryFervent::new, Material.LIGHT_BLUE_STAINED_GLASS_PANE, true),
    REQUIEM("Requiem", LegendaryRequiem.class, LegendaryRequiem::new, LegendaryRequiem::new, Material.PURPLE_STAINED_GLASS_PANE, true),
    STALWART("Stalwart", LegendaryStalwart.class, LegendaryStalwart::new, LegendaryStalwart::new, Material.BLUE_STAINED_GLASS_PANE, true),
    ENHANCED("Enhanced", LegendaryEnhanced.class, LegendaryEnhanced::new, LegendaryEnhanced::new, Material.ORANGE_STAINED_GLASS_PANE, true),
    GRADIENT("Gradient", LegendaryGradient.class, LegendaryGradient::new, LegendaryGradient::new, Material.GREEN_STAINED_GLASS_PANE, true),
    JUGGERNAUT("Juggernaut", LegendaryJuggernaut.class, LegendaryJuggernaut::new, LegendaryJuggernaut::new, Material.RED_STAINED_GLASS_PANE, true),
    ARBALEST("Arbalest", LegendaryArbalest.class, LegendaryArbalest::new, LegendaryArbalest::new, Material.BLACK_STAINED_GLASS_PANE, true),
    REVERED("Revered", LegendaryRevered.class, LegendaryRevered::new, LegendaryRevered::new, Material.WHITE_STAINED_GLASS_PANE, true),
    VALIANT("Valiant", LegendaryValiant.class, LegendaryValiant::new, LegendaryValiant::new, Material.ORANGE_STAINED_GLASS_PANE, true),
    BRILLIANCE("Brilliance", LegendaryBrilliance.class, LegendaryBrilliance::new, LegendaryBrilliance::new, Material.MAGENTA_STAINED_GLASS_PANE, true),
    RELIQUARY("Reliquary", LegendaryReliquary.class, LegendaryReliquary::new, LegendaryReliquary::new, Material.LIGHT_BLUE_STAINED_GLASS_PANE, true),
    INCENDIARY("Incendiary", LegendaryIncendiary.class, LegendaryIncendiary::new, LegendaryIncendiary::new, Material.YELLOW_STAINED_GLASS_PANE, true),
    INANITION("Inanition", LegendaryInanition.class, LegendaryInanition::new, LegendaryInanition::new, Material.ORANGE_STAINED_GLASS_PANE, true),
    EGOISM("Egoism", LegendaryEgoism.class, LegendaryEgoism::new, LegendaryEgoism::new, Material.PINK_STAINED_GLASS_PANE, true),
    FULCRUM("Fulcrum", LegendaryFulcrum.class, LegendaryFulcrum::new, LegendaryFulcrum::new, Material.GRAY_STAINED_GLASS_PANE, true),
    PARADOX("Paradox", LegendaryParadox.class, LegendaryParadox::new, LegendaryParadox::new, Material.LIGHT_BLUE_STAINED_GLASS_PANE, true),
    VIBRANT("Vibrant", LegendaryVibrant.class, LegendaryVibrant::new, LegendaryVibrant::new, Material.PURPLE_STAINED_GLASS_PANE, true),
    EVERLASTING("Everlasting", LegendaryEverlasting.class, LegendaryEverlasting::new, LegendaryEverlasting::new, Material.BLUE_STAINED_GLASS_PANE, true),
    CHAOTIC("Chaotic", LegendaryChaotic.class, LegendaryChaotic::new, LegendaryChaotic::new, Material.ORANGE_STAINED_GLASS_PANE, true),
    REVOLT("Revolt", LegendaryRevolt.class, LegendaryRevolt::new, LegendaryRevolt::new, Material.GREEN_STAINED_GLASS_PANE, true),
    HUNTSMAN("Huntsman", LegendaryHuntsman.class, LegendaryHuntsman::new, LegendaryHuntsman::new, Material.RED_STAINED_GLASS_PANE, true),
    MOMENTUM("Momentum", LegendaryMomentum.class, LegendaryMomentum::new, LegendaryMomentum::new, Material.BLACK_STAINED_GLASS_PANE, true),
    AEGIS("Aegis", LegendaryAegis.class, LegendaryAegis::new, LegendaryAegis::new, Material.WHITE_STAINED_GLASS_PANE, true),
    ORACLE("Oracle", LegendaryOracle.class, LegendaryOracle::new, LegendaryOracle::new, Material.ORANGE_STAINED_GLASS_PANE, true),
    ANCHOR("Anchor", LegendaryAnchor.class, LegendaryAnchor::new, LegendaryAnchor::new, Material.MAGENTA_STAINED_GLASS_PANE, true),
    CONDUIT("Conduit", LegendaryConduit.class, LegendaryConduit::new, LegendaryConduit::new, Material.LIGHT_BLUE_STAINED_GLASS_PANE, true),
    FLUX("Flux", LegendaryFlux.class, LegendaryFlux::new, LegendaryFlux::new, Material.YELLOW_STAINED_GLASS_PANE, true),
    BASTION("Bastion", LegendaryBastion.class, LegendaryBastion::new, LegendaryBastion::new, Material.ORANGE_STAINED_GLASS_PANE, true),
    FRACTURED("Fractured", LegendaryFractured.class, LegendaryFractured::new, LegendaryFractured::new, Material.RED_STAINED_GLASS_PANE, true),
    OVERGROWTH("Overgrowth", LegendaryOvergrowth.class, LegendaryOvergrowth::new, LegendaryOvergrowth::new, Material.GREEN_STAINED_GLASS_PANE, true),
    AFTERSHOCK("Aftershock", LegendaryAftershock.class, LegendaryAftershock::new, LegendaryAftershock::new, Material.MAGENTA_STAINED_GLASS_PANE),

    ;


    public static final LegendaryTitles[] VALUES = values();

    public final String name;
    public final Class<?> clazz;
    public final Function<UUID, AbstractLegendaryWeapon> create;
    public final Function<AbstractLegendaryWeapon, AbstractLegendaryWeapon> titleWeapon;
    public final Material glassPane;
    public final boolean isEnabled;

    LegendaryTitles(
            String name, Class<?> clazz,
            Function<UUID, AbstractLegendaryWeapon> create,
            Function<AbstractLegendaryWeapon, AbstractLegendaryWeapon> titleWeapon,
            Material glassPane, boolean isEnabled
    ) {
        this.name = name;
        this.clazz = clazz;
        this.create = create;
        this.titleWeapon = titleWeapon;
        this.glassPane = glassPane;
        this.isEnabled = isEnabled;
    }

}
