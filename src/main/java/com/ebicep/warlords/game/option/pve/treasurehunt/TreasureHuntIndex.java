package com.ebicep.warlords.game.option.pve.treasurehunt;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;
import java.util.Set;

public enum TreasureHuntIndex {

    DUAL_DESCENT(
            "test",
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
            NamedTextColor.AQUA,
            2,
            4,
            5,
            10,
            Set.of(RoomSize.S_16X16X16, RoomSize.S_16X32X16)
    ),
    VAULTBOUND_QUARTET(
            "test",
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
            NamedTextColor.GOLD,
            2,
            4,
            5,
            10,
            Set.of(RoomSize.S_32X32X32, RoomSize.S_32X64X32)
    ),
    ANCIENT_RENEGADE(
            "test",
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
            NamedTextColor.RED,
            2,
            4,
            -1,
            10,
            Set.of(RoomSize.S_32X32X32, RoomSize.S_32X64X32)
    )

    ;

    private final String id;
    private final String name;
    private final List<Component> description;
    private final NamedTextColor huntColor;
    private final int minimumPlayers;
    private final int maximumPlayers;
    private final int floors;
    private final int roomsPerFloor;
    private final Set<RoomSize> allowedSizes;

    TreasureHuntIndex(String id, String name, List<Component> description, NamedTextColor huntColor, int minimumPlayers, int maximumPlayers, int floors, int roomsPerFloor, Set<RoomSize> allowedSizes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.huntColor = huntColor;
        this.minimumPlayers = minimumPlayers;
        this.maximumPlayers = maximumPlayers;
        this.floors = floors;
        this.roomsPerFloor = roomsPerFloor;
        this.allowedSizes = allowedSizes;
    }

    public boolean isEndless() {
        return floors < 0;
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

    public int getMinimumPlayers() {
        return minimumPlayers;
    }

    public int getMaximumPlayers() {
        return maximumPlayers;
    }

    public int getRoomsPerFloor() {
        return roomsPerFloor;
    }

    public Set<RoomSize> getAllowedSizes() {
        return allowedSizes;
    }

    public String getId() {
        return id;
    }
}
