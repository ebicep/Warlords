package com.ebicep.warlords.menu.debugmenu;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.*;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.ItemBuilder;
import com.ebicep.warlords.util.WordWrap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BiConsumer;

import static com.ebicep.warlords.menu.Menu.*;
import static com.ebicep.warlords.util.Utils.woolSortedByColor;

public class DebugMenu {

    public static void openDebugMenu(Player player) {
        Menu menu = new Menu("Debug Options", 9 * 4);

        LinkedHashMap<ItemStack, BiConsumer<Menu, InventoryClickEvent>> items = new LinkedHashMap<>();
        items.put(new ItemBuilder(Material.ENDER_PORTAL_FRAME).name(ChatColor.GREEN + "Game Options").get(),
                (m, e) -> DebugMenuGameOptions.openGameMenu(player)
        );
        items.put(new ItemBuilder(Warlords.getHead(player)).name(ChatColor.GREEN + "Player Options").get(),
                (m, e) -> DebugMenuPlayerOptions.openPlayerMenu(player, Warlords.getPlayer(player))
        );
        items.put(new ItemBuilder(Material.NOTE_BLOCK).name(ChatColor.GREEN + "Team Options").get(),
                (m, e) -> {
                    DebugMenuPlayerOptions.openTeamMenu(player);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (player.getOpenInventory().getTopInventory().getName().equals("Team Options")) {
                                DebugMenuPlayerOptions.openTeamMenu(player);
                            } else {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(Warlords.getInstance(), 20, 20);
                }
        );

        List<ItemStack> itemsArray = new ArrayList<>(items.keySet());
        for (int i = 0; i < items.size(); i++) {
            menu.setItem(i + 1, 1, itemsArray.get(i), items.get(itemsArray.get(i)));
        }

        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

}
