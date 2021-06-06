package com.ebicep.warlords.menu;

import com.ebicep.warlords.util.Classes;
import com.ebicep.warlords.util.ClassesGroup;
import com.ebicep.warlords.util.ClassesSkillBoosts;
import com.ebicep.warlords.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.util.Classes.*;

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
    private static final ItemStack MENU_BOOSTS = new ItemBuilder(Material.BOOKSHELF)
            .name(ChatColor.AQUA + "Weapon Skill Boost")
            .lore("§7Choose which of your skills you\n§7want your equipped weapon to boost.", "", "§eClick to change skill boost!")
            .get();


    public static void openMainMenu(Player player) {
        Classes selectedClass = getSelected(player);

        Menu menu = new Menu("Warlords Shop", 9 * 6);
        ClassesGroup[] values = ClassesGroup.values();
        for (int i = 0; i < values.length; i++) {
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
                            ChatColor.DARK_GRAY + " [" +
                            ChatColor.GRAY + "Lv00" +
                            ChatColor.DARK_GRAY + "]"
            ).lore(lore).get();
            menu.setItem(
                    9 / 2 - values.length / 2 + i * 2 - 1,
                    1,
                    item,
                    (n, e) -> openClassMenu(player, group)
            );
        }
        menu.setItem(4, 3, MENU_BOOSTS, (n, e) -> openSkillBoostMenu(player, selectedClass));
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openClassMenu(Player player, ClassesGroup selectedGroup) {
        Classes selectedClass = getSelected(player);
        Menu menu = new Menu(selectedGroup.name, 9*6);
        List<Classes> values = selectedGroup.subclasses;
        for(int i = 0; i < values.size(); i++) {
            Classes subClass = values.get(i);
            ItemBuilder builder = new ItemBuilder(subClass.icon)
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
                    (n, e) -> {
                        player.sendMessage(ChatColor.WHITE + "Class: §6" + subClass);
                        setSelected(player, subClass);
                        openClassMenu(player, selectedGroup);
                    }
            );
        }
        menu.setItem(4, 3, MENU_BACK, (n, e) -> openMainMenu(player));

        menu.openForPlayer(player);
    }

    public static void openSkillBoostMenu(Player player, Classes selectedGroup) {
        Classes selectedClass = getSelected(player);
        ClassesSkillBoosts selectedBoost = getSelectedBoost(player);
        Menu menu = new Menu("Skill Boost", 9 * 4);
        List<ClassesSkillBoosts> values = selectedGroup.skillBoosts;
        for (int i = 0; i < values.size(); i++) {
            ClassesSkillBoosts subClass = values.get(i);
            ItemBuilder builder = new ItemBuilder(player.getInventory().getItem(0))
                    .name(
                            subClass == selectedBoost ? ChatColor.GREEN + subClass.name + " (" + selectedClass.name + ")" : ChatColor.RED + subClass.name + " (" + selectedClass.name + ")"
                    )
                    .flags(ItemFlag.HIDE_ENCHANTS);
            List<String> lore = new ArrayList<>();
            lore.add(
                    subClass == selectedBoost ? subClass.selectedDescription : subClass.description
            );
            lore.add("");
            if (subClass == selectedBoost) {
                lore.add(ChatColor.GREEN + "Currently selected!");
                builder.enchant(Enchantment.OXYGEN, 1);
            } else {
                lore.add(ChatColor.YELLOW + "Click to select!");
            }
            builder.lore(lore);
            menu.setItem(
                    9 / 2 - values.size() % 2 + i * 2 - 1,
                    1,
                    builder.get(),
                    (n, e) -> {
                        player.sendMessage(ChatColor.GREEN + "Your have changed your weapon boost to: §b" + subClass.name + "!");
                        setSelectedBoost(player, subClass);
                        openSkillBoostMenu(player, selectedGroup);
                    }
            );
        }

        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }
}