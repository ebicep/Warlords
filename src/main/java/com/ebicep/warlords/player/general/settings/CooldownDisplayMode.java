package com.ebicep.warlords.player.general.settings;

import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum CooldownDisplayMode {

    ON(new ItemBuilder(Material.RED_DYE)
            .name(Component.text("Cooldown Display", NamedTextColor.GREEN))
            .lore(
                    Component.text("Currently selected ", NamedTextColor.GRAY).append(Component.text("On", NamedTextColor.AQUA)),
                    Component.empty(),
                    Component.text("Toggles whether or not you", NamedTextColor.GRAY),
                    Component.text("can see teammates' cooldowns", NamedTextColor.GRAY),
                    Component.empty(),
                    Component.text("Click here to disable cooldown display.", NamedTextColor.YELLOW)
            )
            .get()
    ),
    OFF(new ItemBuilder(Material.GRAY_DYE)
            .name(Component.text("Cooldown Display", NamedTextColor.GREEN))
            .lore(
                    Component.text("Currently selected ", NamedTextColor.GRAY).append(Component.text("Off", NamedTextColor.YELLOW)),
                    Component.empty(),
                    Component.text("Toggles whether or not you", NamedTextColor.GRAY),
                    Component.text("can see teammates' cooldowns", NamedTextColor.GRAY),
                    Component.empty(),
                    Component.text("Click here to enable cooldown display.", NamedTextColor.YELLOW)
            )
            .get()
    ),

    ;

    public final ItemStack item;

    CooldownDisplayMode(ItemStack item) {
        this.item = item;
    }
}
