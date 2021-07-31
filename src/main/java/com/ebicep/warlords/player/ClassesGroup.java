package com.ebicep.warlords.player;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.ebicep.warlords.player.Classes.*;

public enum ClassesGroup {
    MAGE(
            "Mage",
            new ItemStack(Material.INK_SACK, 1, (short) 12),
            "§7The mage has access to powerful\n§7Arcane, Fire, Ice and Water magic.",
            PYROMANCER, CRYOMANCER, AQUAMANCER
    ),
    WARRIOR(
            "Warrior",
            new ItemStack(Material.COAL, 1, (short) 1),
            "§7The Warrior uses brute force to\n§7overpower his opponents in melee\n§7combat or to defend his allies.",
            BERSERKER, DEFENDER, REVENANT
    ),
    PALADIN(
            "Paladin",
            new ItemStack(Material.INK_SACK, 1, (short) 11),
            "§7The Paladin's strongest ally is the\n§7light. He uses it to empower his\n§7weapon in order to vanquish foes and\n§7protect teammates.",
            AVENGER, CRUSADER, PROTECTOR
    ),
    SHAMAN(
            "Shaman",
            new ItemStack(Material.INK_SACK, 1, (short) 2),
            "§7The Shaman has an unbreakable bond\n§7with nature. This grants him access to\n§7devastating abilities that are\n§7empowered by the elements.",
            THUNDERLORD, SPIRITGUARD, EARTHWARDEN
    ),
    ;
    public final String name;
    public final ItemStack item;
    public final String description;
    public final List<Classes> subclasses;

    ClassesGroup(String name, ItemStack item, String description, Classes... subclasses) {
        this.name = name;
        this.item = item;
        this.description = description;
        this.subclasses = Collections.unmodifiableList(Arrays.asList(subclasses));
    }
}