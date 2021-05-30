package com.ebicep.warlords.menu;

import com.ebicep.warlords.util.Classes;
import com.ebicep.warlords.util.ClassesGroup;
import com.ebicep.warlords.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.util.Classes.getSelected;
import static com.ebicep.warlords.util.Classes.setSelected;

public class GameMenu {
    private static final ItemStack MENU_CLOSE = new ItemBuilder(Material.ARROW)
            .name("Close menu")
            .get();
    private static final ItemStack MENU_BACK = new ItemBuilder(Material.ARROW)
            .name("Back")
            .get();


    public static void openMainMenu(Player player) {
        Menu menu = new Menu("Main warlords menu", 9*6);
        ClassesGroup[] values = ClassesGroup.values();
        for(int i = 0; i < values.length; i++) {
            ClassesGroup group = values[i];
            menu.setItem(
                    9 / 2 - values.length / 2 + i * 2,
                    1,
                    group.item,
                    (n,e) -> openClassMenu(player, group)
            );
        }
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openClassMenu(Player player, ClassesGroup selectedGroup) {
        Classes selectedClass = getSelected(player);
        Menu menu = new Menu("Select class", 9*6);
        List<Classes> values = selectedGroup.subclasses;
        for(int i = 0; i < values.size(); i++) {
            Classes subClass = values.get(i);
            menu.setItem(
                    9 / 2 - values.size() / 2 + i * 2,
                    1,
                    new ItemBuilder(Material.ARROW)
                            .name(subClass == selectedClass ? subClass.name + " (SELECTED)" : subClass.name)
                            .lore(subClass.description)
                            .get(),
                    (n,e) -> {
                        player.sendMessage(ChatColor.BLUE + "Your selected class: §7" + subClass);
                        setSelected(player, subClass);
                        openClassMenu(player, selectedGroup);
                    }
            );
        }

        menu.openForPlayer(player);
    }
}