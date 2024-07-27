package com.ebicep.warlords.player.general.settings;

import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum HotkeyMode {

    NEW_MODE(new ItemBuilder(Material.REDSTONE)
            .name(Component.text("Hotkey Mode", NamedTextColor.GREEN))
            .lore(
                    Component.text("Currently selected ", NamedTextColor.GRAY).append(Component.text("NEW", NamedTextColor.AQUA)),
                    Component.empty(),
                    Component.text("Click here to enable Classic mode.", NamedTextColor.YELLOW)
            )
            .get()),
    CLASSIC_MODE(new ItemBuilder(Material.SNOWBALL)
            .name(Component.text("Hotkey Mode", NamedTextColor.GREEN))
            .lore(
                    Component.text("Currently selected ", NamedTextColor.GRAY).append(Component.text("Classic", NamedTextColor.YELLOW)),
                    Component.empty(),
                    Component.text("Click here to enable NEW mode.", NamedTextColor.YELLOW)
            )
            .get()),

    ;

    public final ItemStack item;

    HotkeyMode(ItemStack item) {
        this.item = item;
    }

}
