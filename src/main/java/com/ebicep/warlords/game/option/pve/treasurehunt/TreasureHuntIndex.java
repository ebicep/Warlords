package com.ebicep.warlords.game.option.pve.treasurehunt;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;

public enum TreasureHuntIndex {

    DUAL_DESCENT(
            "Dual Descent",
            List.of(
                    Component.empty(),
                    Component.text("Max players: ", NamedTextColor.GRAY)
                            .append(Component.text("2", NamedTextColor.YELLOW)),
                    Component.empty(),
                    Component.text("Entry Requirements:", NamedTextColor.GRAY),
                    Component.text("- None ", NamedTextColor.GRAY),
                    Component.empty(),
                    Component.text("Traverse the hidden rooms as a duo", NamedTextColor.GRAY),
                    Component.text("to find the secrets from the last floor.", NamedTextColor.GRAY)
            ),
            NamedTextColor.AQUA
    ),
    VAULTBOUND_QUARTET(
            "Vaultbound Quartet",
            List.of(
                    Component.empty(),
                    Component.text("Max players: ", NamedTextColor.GRAY)
                            .append(Component.text("2-4", NamedTextColor.YELLOW)),
                    Component.empty(),
                    Component.text("Entry Requirements:", NamedTextColor.GRAY),
                    Component.text("- All players must have an ", NamedTextColor.GRAY)
                            .append(Component.text("Epic ", NamedTextColor.DARK_PURPLE)),
                    Component.text("Weapon", NamedTextColor.DARK_PURPLE)
                            .append(Component.text(" or higher equipped.", NamedTextColor.GRAY)),
                    Component.empty(),
                    Component.text("Traverse the hidden rooms as a duo", NamedTextColor.GRAY),
                    Component.text("to find the secrets from the last floor.", NamedTextColor.GRAY)
            ),
            NamedTextColor.GOLD
    ),
    ANCIENT_RENEGADE(
            "Ancient Renegades",
            List.of(
                    Component.empty(),
                    Component.text("Max players: ", NamedTextColor.GRAY)
                            .append(Component.text("4-8", NamedTextColor.YELLOW)),
                    Component.empty(),
                    Component.text("Entry Requirements:", NamedTextColor.GRAY),
                    Component.text("- All players must have a ", NamedTextColor.GRAY)
                            .append(Component.text("Legendary ", NamedTextColor.GOLD)),
                    Component.text("Weapon", NamedTextColor.GOLD)
                            .append(Component.text(" or higher equipped.", NamedTextColor.GRAY)),
                    Component.empty(),
                    Component.text("Unearth the forgotten history of this"),
                    Component.text("enigmatic realm, where each level holds"),
                    Component.text("its own tales of lost treasures and"),
                    Component.text("legendary artifacts from ages past.")
            ),
            NamedTextColor.RED
    )

    ;

    private final String name;
    private final List<Component> description;
    private final NamedTextColor huntColor;

    TreasureHuntIndex(String name, List<Component> description, NamedTextColor huntColor) {
        this.name = name;
        this.description = description;
        this.huntColor = huntColor;
    }

    public String getName() {
        return name;
    }

    public List<Component> getDescription() {
        return description;
    }

    public NamedTextColor getHuntColor() {
        return huntColor;
    }
}
