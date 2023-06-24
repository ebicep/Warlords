package com.ebicep.warlords.player.general;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.ebicep.warlords.player.general.Specializations.*;

public enum Classes {
    MAGE(
            "Mage",
            List.of("mag"),
            new ItemStack(Material.LIGHT_BLUE_DYE),
            "The mage has access to powerful Fire, Ice and Water magic.",
            PYROMANCER, CRYOMANCER, AQUAMANCER
    ),
    WARRIOR(
            "Warrior",
            List.of("war"),
            new ItemStack(Material.CHARCOAL),
            "The Warrior uses brute force to overpower their opponents in melee combat or to defend their allies.",
            BERSERKER, DEFENDER, REVENANT
    ),
    PALADIN(
            "Paladin",
            List.of("pal"),
            new ItemStack(Material.DANDELION),
            "The Paladin's strongest ally is the light. They use it to empower their weapon in order to vanquish foes and protect teammates.",
            AVENGER, CRUSADER, PROTECTOR
    ),
    SHAMAN(
            "Shaman",
            List.of("sha"),
            new ItemStack(Material.GREEN_DYE),
            "The Shaman has an unbreakable bond with nature. This grants them access to devastating abilities that are empowered by the elements.",
            THUNDERLORD, SPIRITGUARD, EARTHWARDEN
    ),
    ROGUE(
            "Rogue",
            List.of("rog"),
            new ItemStack(Material.PINK_DYE),
            "The Rogue is a master of deception. Always looking to gain the upper hand in the shadows.",
            ASSASSIN, VINDICATOR, APOTHECARY
    ),
    ARCANIST(
            "Arcanist",
            List.of("arc"),
            new ItemStack(Material.BEDROCK),
            "The Arcanist is a formidable class with unparalleled mastery over the arcane arts by tapping into a wellspring of raw magical energy. This connection allows them to unleash a multitude of awe-inspiring abilities.",
            CONJURER, SENTINEL, CLERIC
    );

    public static final Classes[] VALUES = values();
    public static final List<String> NAMES = new ArrayList<>();

    static {
        for (Classes c : VALUES) {
            NAMES.add(c.name);
            NAMES.addAll(c.aliases);
        }
    }

    public final String name;
    public final List<String> aliases;
    public final ItemStack item;
    public final String description;
    public final List<Specializations> subclasses;

    Classes(String name, List<String> aliases, ItemStack item, String description, Specializations... subclasses) {
        this.name = name;
        this.aliases = aliases;
        this.item = item;
        this.description = description;
        this.subclasses = List.of(subclasses);
    }

    public static Classes getClassFromNameNullable(String name) {
        if (name == null) {
            return null;
        }

        for (Classes value : VALUES) {
            if (value.name.equalsIgnoreCase(name) || value.aliases.contains(name.toLowerCase())) {
                return value;
            }
        }

        return null;
    }

}