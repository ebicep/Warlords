package com.ebicep.warlords.pve.weapons.weapontypes.legendaries;

import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles.*;
import org.bukkit.Material;

import java.util.UUID;
import java.util.function.Function;

public enum LegendaryTitles {

    NONE("", LegendaryWeapon.class, LegendaryWeapon::new, LegendaryWeapon::new, Material.WHITE_STAINED_GLASS_PANE),
    TITANIC("Titanic", LegendaryTitanic.class, LegendaryTitanic::new, LegendaryTitanic::new, Material.ORANGE_STAINED_GLASS_PANE),
    VIGOROUS("Vigorous", LegendaryVigorous.class, LegendaryVigorous::new, LegendaryVigorous::new, Material.MAGENTA_STAINED_GLASS_PANE),
    SUSPICIOUS("Suspicious", LegendarySuspicious.class, LegendarySuspicious::new, LegendarySuspicious::new, Material.LIGHT_BLUE_STAINED_GLASS_PANE),
    BENEVOLENT("Benevolent", LegendaryBenevolent.class, LegendaryBenevolent::new, LegendaryBenevolent::new, Material.YELLOW_STAINED_GLASS_PANE),
    VORPAL("Vorpal", LegendaryVorpal.class, LegendaryVorpal::new, LegendaryVorpal::new, Material.LIME_STAINED_GLASS_PANE),
    DIVINE("Divine", LegendaryDivine.class, LegendaryDivine::new, LegendaryDivine::new, Material.PINK_STAINED_GLASS_PANE),
    GALE("Gale", LegendaryGale.class, LegendaryGale::new, LegendaryGale::new, Material.GRAY_STAINED_GLASS_PANE),
    FERVENT("Fervent", LegendaryFervent.class, LegendaryFervent::new, LegendaryFervent::new, Material.LIGHT_BLUE_STAINED_GLASS_PANE),
    REQUIEM("Requiem", LegendaryRequiem.class, LegendaryRequiem::new, LegendaryRequiem::new, Material.PURPLE_STAINED_GLASS_PANE),
    STALWART("Stalwart", LegendaryStalwart.class, LegendaryStalwart::new, LegendaryStalwart::new, Material.BLUE_STAINED_GLASS_PANE),
    ENHANCED("Enhanced", LegendaryEnhanced.class, LegendaryEnhanced::new, LegendaryEnhanced::new, Material.BROWN_STAINED_GLASS_PANE),
    GRADIENT("Gradient", LegendaryGradient.class, LegendaryGradient::new, LegendaryGradient::new, Material.GREEN_STAINED_GLASS_PANE),
    JUGGERNAUT("Juggernaut", LegendaryJuggernaut.class, LegendaryJuggernaut::new, LegendaryJuggernaut::new, Material.RED_STAINED_GLASS_PANE),
    ARBALEST("Arbalest", LegendaryArbalest.class, LegendaryArbalest::new, LegendaryArbalest::new, Material.BLACK_STAINED_GLASS_PANE),
    REVERED("Revered", LegendaryRevered.class, LegendaryRevered::new, LegendaryRevered::new, Material.WHITE_STAINED_GLASS_PANE),
    VALIANT("Valiant", LegendaryValiant.class, LegendaryValiant::new, LegendaryValiant::new, Material.ORANGE_STAINED_GLASS_PANE),
    BRILLIANCE("Brilliance", LegendaryBrilliance.class, LegendaryBrilliance::new, LegendaryBrilliance::new, Material.MAGENTA_STAINED_GLASS_PANE),
    RELIQUARY("Reliquary", LegendaryReliquary.class, LegendaryReliquary::new, LegendaryReliquary::new, Material.LIGHT_BLUE_STAINED_GLASS_PANE),
    INCENDIARY("Incendiary", LegendaryIncendiary.class, LegendaryIncendiary::new, LegendaryIncendiary::new, Material.YELLOW_STAINED_GLASS_PANE),
    INANITION("Inanition", LegendaryInanition.class, LegendaryInanition::new, LegendaryInanition::new, Material.LIME_STAINED_GLASS_PANE),
    EGOISM("Egoism", LegendaryEgoism.class, LegendaryEgoism::new, LegendaryEgoism::new, Material.PINK_STAINED_GLASS_PANE),
    FULCRUM("Fulcrum", LegendaryFulcrum.class, LegendaryFulcrum::new, LegendaryFulcrum::new, Material.GRAY_STAINED_GLASS_PANE),
    PARADOX("Paradox", LegendaryParadox.class, LegendaryParadox::new, LegendaryParadox::new, Material.LIGHT_BLUE_STAINED_GLASS_PANE),
    VIBRANT("Vibrant", LegendaryVibrant.class, LegendaryVibrant::new, LegendaryVibrant::new, Material.PURPLE_STAINED_GLASS_PANE),
    EVERLASTING("Everlasting", LegendaryEverlasting.class, LegendaryEverlasting::new, LegendaryEverlasting::new, Material.BLUE_STAINED_GLASS_PANE),
    CHAOTIC("Chaotic", LegendaryChaotic.class, LegendaryChaotic::new, LegendaryChaotic::new, Material.BROWN_STAINED_GLASS_PANE),

    ;


    public static final LegendaryTitles[] VALUES = values();

    public final String name;
    public final Class<?> clazz;
    public final Function<UUID, AbstractLegendaryWeapon> create;
    public final Function<AbstractLegendaryWeapon, AbstractLegendaryWeapon> titleWeapon;
    public final Material glassPane;

    LegendaryTitles(
            String name, Class<?> clazz,
            Function<UUID, AbstractLegendaryWeapon> create,
            Function<AbstractLegendaryWeapon, AbstractLegendaryWeapon> titleWeapon,
            Material glassPane
    ) {
        this.name = name;
        this.clazz = clazz;
        this.create = create;
        this.titleWeapon = titleWeapon;
        this.glassPane = glassPane;
    }

}
