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
import java.util.ArrayList;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

public class GameMenu {
    private static final ItemStack MENU_CLOSE = new ItemBuilder(Material.BARRIER)
            .name("Close")
            .get();
    private static final ItemStack MENU_BACK = new ItemBuilder(Material.ARROW)
            .name("Back")
            .get();


    public static void openMainMenu(Player player) {
        Classes selectedClass = getSelected(player);

        Menu menu = new Menu("Warlords Shop", 9*6);
        ClassesGroup[] values = ClassesGroup.values();
        for(int i = 0; i < values.length; i++) {
            ClassesGroup group = values[i];

            List<String> lore = new ArrayList<>();
            lore.add(group.description);
            lore.add("");
            lore.add(ChatColor.GOLD + "§7Specializations:");
            for (Classes subClass : group.subclasses) {
                lore.add((subClass == selectedClass ? ChatColor.GREEN : ChatColor.RESET) + subClass.name);
            }
            ItemStack item = new ItemBuilder(group.item).name(
                    ChatColor.GOLD + group.name +
                            ChatColor.DARK_GRAY + "[" +
                            ChatColor.GRAY + "Lv00" +
                            ChatColor.DARK_GRAY + "]"
            ).lore(lore).get();
            menu.setItem(
                    9 / 2 - values.length / 2 + i * 2 - 1,
                    1,
                    item,
                    (n,e) -> openClassMenu(player, group)
            );
        }
        menu.setItem(4, 4, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openClassMenu(Player player, ClassesGroup selectedGroup) {
        Classes selectedClass = getSelected(player);
        Menu menu = new Menu(selectedGroup.name, 9*6);
        List<Classes> values = selectedGroup.subclasses;
        for(int i = 0; i < values.size(); i++) {
            Classes subClass = values.get(i);
            ItemBuilder builder = new ItemBuilder(Material.ARROW)
                    .name(
                            ChatColor.GREEN + "§7Specialization: " + subClass.name
                    )
                    .flags(ItemFlag.HIDE_ENCHANTS);
            List<String> lore = new ArrayList<>();
            lore.add(subClass.description);
            lore.add("");
            if(subClass == selectedClass) {
                lore.add(ChatColor.GREEN + ">>> ACTIVE <<<");
                builder.enchant(Enchantment.OXYGEN, 1);
            } else {
                lore.add(ChatColor.YELLOW + ">>> Click to activate <<<");
            }
            builder.lore(lore);
            menu.setItem(
                    9 / 2 - values.size() / 2 + i * 2 - 1,
                    1,
                    builder.get(),
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