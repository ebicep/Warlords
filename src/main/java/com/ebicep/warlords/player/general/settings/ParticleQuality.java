package com.ebicep.warlords.player.general.settings;

import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ParticleQuality {

    VERY_LOW(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name(Component.text("Very Low Quality", NamedTextColor.RED)).get(),
            Component.text("Minimizes the amount of particles you will see.", NamedTextColor.GRAY),
            2
    ),
    LOW(new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE).name(Component.text("Low Quality", NamedTextColor.GOLD)).get(),
            Component.text("Heavily reduces the amount of particles you will see.", NamedTextColor.GRAY),
            5
    ),
    MEDIUM(new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).name(Component.text("Medium Quality", NamedTextColor.YELLOW)).get(),
            Component.text("Reduces the amount of particles seen.", NamedTextColor.GRAY),
            10
    ),
    HIGH(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name(Component.text("High Quality", NamedTextColor.GREEN)).get(),
            Component.text("Lightly reduces the amount of particles you will see.", NamedTextColor.GRAY),
            50
    ),
    VERY_HIGH(new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).name(Component.text("Very High Quality", NamedTextColor.DARK_GREEN)).get(),
            Component.text("Shows nearly all particles for the best experience.", NamedTextColor.GRAY),
            500
    ),

    ;

    public final ItemStack item;
    public final TextComponent description;
    public final int particleReduction;

    ParticleQuality(ItemStack item, TextComponent description, int particleReduction) {
        this.item = item;
        this.description = description;
        this.particleReduction = particleReduction;
    }

}
