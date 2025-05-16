package com.ebicep.warlords.player.general.settings;

import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum GlowingMode {

    ON(new ItemBuilder(Material.GLOW_INK_SAC)
            .name(Component.text("Glowing Mode", NamedTextColor.GREEN))
            .lore(
                    Component.text("Currently selected ", NamedTextColor.GRAY).append(Component.text("On", NamedTextColor.AQUA)),
                    Component.empty(),
                    Component.text("Toggles whether or not players", NamedTextColor.GRAY),
                    Component.text("are allowed to glow", NamedTextColor.GRAY),
                    Component.empty(),
                    Component.text("Click here to disable glowing.", NamedTextColor.YELLOW)
            )
            .get()
    ),
    OFF(new ItemBuilder(Material.INK_SAC)
            .name(Component.text("Glowing Mode", NamedTextColor.GREEN))
            .lore(
                    Component.text("Currently selected ", NamedTextColor.GRAY).append(Component.text("Off", NamedTextColor.YELLOW)),
                    Component.empty(),
                    Component.text("Toggles whether or not players", NamedTextColor.GRAY),
                    Component.text("are allowed to glow", NamedTextColor.GRAY),
                    Component.empty(),
                    Component.text("Click here to enable glowing.", NamedTextColor.YELLOW)
            )
            .get()
    ),

    ;

    public final ItemStack item;

    GlowingMode(ItemStack item) {
        this.item = item;
    }
}
