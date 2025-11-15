package com.ebicep.warlords.player.general.settings;

import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum AdvancedHoverMessages {

    ON(new ItemBuilder(Material.GOLD_INGOT)
            .name(Component.text("Advanced Hover Messages", NamedTextColor.GREEN))
            .lore(
                    Component.text("Currently selected ", NamedTextColor.GRAY).append(Component.text("On", NamedTextColor.AQUA)),
                    Component.empty()
            )
            .addLore(WordWrap.wrap(Component.text("Toggles whether or not you will see an advanced breakdown when you hover over damage or healing events.",
                    NamedTextColor.GRAY
            ), 150))
            .addLore(
                    Component.empty(),
                    Component.text("Click here to disable advanced hover messages.", NamedTextColor.YELLOW)
            )
            .get()
    ),
    OFF(new ItemBuilder(Material.NETHERITE_INGOT)
            .name(Component.text("Advanced Hover Messages", NamedTextColor.GREEN))
            .lore(
                    Component.text("Currently selected ", NamedTextColor.GRAY).append(Component.text("Off", NamedTextColor.YELLOW)),
                    Component.empty()
            )
            .addLore(WordWrap.wrap(Component.text("Toggles whether or not you will see an advanced breakdown when you hover over damage or healing events.",
                    NamedTextColor.GRAY
            ), 150))
            .addLore(
                    Component.empty(),
                    Component.text("Click here to enable fast advanced hover messages.", NamedTextColor.YELLOW)
            )
            .get()
    ),

    ;

    public final ItemStack item;

    AdvancedHoverMessages(ItemStack item) {
        this.item = item;
    }
}
