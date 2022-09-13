package com.ebicep.warlords.player.general;

import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Settings {

    public enum HotkeyMode {

        NEW_MODE(new ItemBuilder(Material.REDSTONE)
                .name(ChatColor.GREEN + "Hotkey Mode")
                .lore(ChatColor.AQUA + "Currently selected " + ChatColor.YELLOW + "NEW", "", ChatColor.YELLOW + "Click here to enable Classic mode.")
                .get()),
        CLASSIC_MODE(new ItemBuilder(Material.SNOW_BALL)
                .name(ChatColor.GREEN + "Hotkey Mode")
                .lore(ChatColor.YELLOW + "Currently selected " + ChatColor.AQUA + "Classic", "", ChatColor.YELLOW + "Click here to enable NEW mode.")
                .get()),

        ;

        public final ItemStack item;

        HotkeyMode(ItemStack item) {
            this.item = item;
        }

    }

    public enum ParticleQuality {

        LOW(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 1).name(ChatColor.GOLD + "Low Quality").get(),
                ChatColor.GRAY + "Heavily reduces the amount of\n" + ChatColor.GRAY + "particles you will see.",
                2
        ),
        MEDIUM(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 4).name(ChatColor.YELLOW + "Medium Quality").get(),
                ChatColor.GRAY + "Reduces the amount of particles\n" + ChatColor.GRAY + "seem.",
                4
        ),
        HIGH(new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (short) 5).name(ChatColor.GREEN + "High Quality").get(),
                ChatColor.GRAY + "Shows all particles for the best\n" + ChatColor.GRAY + "experience.",
                100000
        ),

        ;

        public final ItemStack item;
        public final String description;
        public final int particleReduction;

        ParticleQuality(ItemStack item, String description, int particleReduction) {
            this.item = item;
            this.description = description;
            this.particleReduction = particleReduction;
        }

    }

    public enum FlagMessageMode {

        RELATIVE(new ItemBuilder(Material.COMPASS)
                .name(ChatColor.GREEN + "Flag Message Mode")
                .lore(
                        ChatColor.AQUA + "Currently selected " + ChatColor.YELLOW + "Relative",
                        ChatColor.GRAY + "Prints out flag messages with 'YOUR/ENEMY'",
                        "",
                        ChatColor.YELLOW + "Click here to enable Absolute mode."
                )
                .get()
        ),
        ABSOLUTE(new ItemBuilder(Material.WOOL)
                .name(ChatColor.GREEN + "Flag Message Mode")
                .lore(
                        ChatColor.AQUA + "Currently selected " + ChatColor.YELLOW + "Absolute",
                        ChatColor.GRAY + "Prints out flag messages with team names",
                        "",
                        ChatColor.YELLOW + "Click here to enable Relative mode."
                )
                .get()
        ),

        ;

        public final ItemStack item;

        FlagMessageMode(ItemStack item) {
            this.item = item;
        }
    }

}
