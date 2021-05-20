package com.ebicep.warlords.util;

import static com.ebicep.warlords.util.Classes.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ClassesGroup {
    MAGE(
            "Mage",
            Material.BARRIER,
            "Mage description",
            PYROMANCER, CRYOMANCER, AQUAMANCER
    ),
    WARRIOR(
            "Warrion",
            Material.BARRIER,
            "Mage description",
            BERSERKER, DEFENDER, REVENANT
    ),
    PALADIN(
            "Paladin",
            Material.BARRIER,
            "Paladin description",
            AVENGER, CRUSADER, PROTECTOR
    ),
    SHAMAN(
            "Shaman",
            Material.BARRIER,
            "Shaman description",
            THUNDERLORD, SPIRITGUARD, EARTHWARDEN
    ),
    ;
    public final String name;
    public final ItemStack item;
    public final String description;
    public final List<Classes> subclasses;

    ClassesGroup(String name, Material material, String description, Classes ... subclasses) {
        this.name = name;
        this.description = description;
        this.subclasses = Collections.unmodifiableList(Arrays.asList(subclasses));
        List<String> lore = new ArrayList<>();
        lore.add(description);
        lore.add("");
        lore.add("Subclasses:");
        for(Classes subClass : subclasses) {
            lore.add(subClass.name);
        }
        this.item = new ItemBuilder(material).name(name).lore(lore).get();
    }
}