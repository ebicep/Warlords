package com.ebicep.warlords.player.general.settings;

import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum FastWaveMode {

    ON(new ItemBuilder(Material.DIAMOND_HORSE_ARMOR)
            .name(Component.text("Fast Wave Mode", NamedTextColor.GREEN))
            .lore(
                    Component.text("Currently selected ", NamedTextColor.GRAY).append(Component.text("On", NamedTextColor.AQUA)),
                    Component.empty()
            )
            .addLore(WordWrap.wrap(Component.text("Toggles whether or not waves should start twice as fast. All players must have this on for it to take effect.",
                    NamedTextColor.GRAY
            ), 150))
            .addLore(
                    Component.empty(),
                    Component.text("Click here to disable fast wave mode.", NamedTextColor.YELLOW)
            )
            .get()
    ),
    OFF(new ItemBuilder(Material.LEATHER_HORSE_ARMOR)
            .name(Component.text("Fast Wave Mode", NamedTextColor.GREEN))
            .lore(
                    Component.text("Currently selected ", NamedTextColor.GRAY).append(Component.text("Off", NamedTextColor.YELLOW)),
                    Component.empty()
            )
            .addLore(WordWrap.wrap(Component.text(
                    "Toggles whether or not waves should start twice as fast. All players in the game must have this on for it to take effect. Only applicable to Wave Defense.",
                    NamedTextColor.GRAY
            ), 150))
            .addLore(
                    Component.empty(),
                    Component.text("Click here to enable fast wave mode.", NamedTextColor.YELLOW)
            )
            .get()
    ),

    ;

    public final ItemStack item;

    FastWaveMode(ItemStack item) {
        this.item = item;
    }
}
