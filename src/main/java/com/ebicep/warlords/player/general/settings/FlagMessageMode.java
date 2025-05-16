package com.ebicep.warlords.player.general.settings;

import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum FlagMessageMode {

    RELATIVE(new ItemBuilder(Material.COMPASS)
            .name(Component.text("Flag Message Mode", NamedTextColor.GREEN))
            .lore(
                    Component.text("Currently selected ", NamedTextColor.GRAY).append(Component.text("Relative", NamedTextColor.AQUA)),
                    Component.empty(),
                    Component.text("Prints out flag messages with 'YOUR/ENEMY'", NamedTextColor.GRAY),
                    Component.empty(),
                    Component.text("Click here to enable Absolute mode.", NamedTextColor.YELLOW)
            )
            .get()
    ),
    ABSOLUTE(new ItemBuilder(Material.WHITE_WOOL)
            .name(Component.text("Flag Message Mode", NamedTextColor.GREEN))
            .lore(
                    Component.text("Currently selected ", NamedTextColor.GRAY).append(Component.text("Absolute", NamedTextColor.YELLOW)),
                    Component.empty(),
                    Component.text("Prints out flag messages with team names", NamedTextColor.GRAY),
                    Component.empty(),
                    Component.text("Click here to enable Relative mode.", NamedTextColor.YELLOW)
            )
            .get()
    ),

    ;

    public final ItemStack item;

    FlagMessageMode(ItemStack item) {
        this.item = item;
    }
}
