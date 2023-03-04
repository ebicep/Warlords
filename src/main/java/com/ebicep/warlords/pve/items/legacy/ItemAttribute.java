package com.ebicep.warlords.pve.items.legacy;

import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ItemAttribute {

    ALPHA("Alpha",
            new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 3)
                    .name(ChatColor.BLUE + "Alpha")
                    .get(),
            2,
            3
    ),
    BETA("Beta",
            new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 5)
                    .name(ChatColor.GREEN + "Beta")
                    .get(),
            2,
            3
    ),
    GAMMA("Gamma",
            new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 6)
                    .name(ChatColor.RED + "Gamma")
                    .get(),
            1,
            2
    ),
    DELTA("Delta",
            new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 4)
                    .name(ChatColor.YELLOW + "Delta")
                    .get(),
            1,
            1
    ),

    ;

    public static final ItemAttribute[] VALUES = values();

    public final String name;
    public final ItemStack itemStack;
    public final int currentEquipped;
    public final int maxEquipped;

    ItemAttribute(String name, ItemStack itemStack, int currentEquipped, int maxEquipped) {
        this.name = name;
        this.itemStack = itemStack;
        this.currentEquipped = currentEquipped;
        this.maxEquipped = maxEquipped;
    }
}
