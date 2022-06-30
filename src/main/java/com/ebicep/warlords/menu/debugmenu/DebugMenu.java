package com.ebicep.warlords.menu.debugmenu;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.ingame.AbstractWarlordsEntity;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
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

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.menu.Menu.MENU_CLOSE;

public class DebugMenu {

    public static void openDebugMenu(Player player) {
        Menu menu = new Menu("Debug Options", 9 * 4);

        LinkedHashMap<ItemStack, BiConsumer<Menu, InventoryClickEvent>> items = new LinkedHashMap<>();
        items.put(new ItemBuilder(Material.ENDER_PORTAL_FRAME).name(ChatColor.GREEN + "Game Options").get(),
                (m, e) -> DebugMenuGameOptions.openGameMenu(player)
        );

        AbstractWarlordsEntity warlordsPlayer = Warlords.getPlayer(player);
        if (warlordsPlayer != null) {
            Game game = warlordsPlayer.getGame();
            items.put(new ItemBuilder(Material.BOOK)
                            .name(ChatColor.GREEN + "Game - " + game.getGameId())
                            .lore(ChatColor.DARK_GRAY + "Map - " + ChatColor.RED + game.getMap().getMapName(),
                                    ChatColor.DARK_GRAY + "GameMode - " + ChatColor.RED + game.getGameMode(),
                                    ChatColor.DARK_GRAY + "Addons - " + ChatColor.RED + game.getAddons(),
                                    ChatColor.DARK_GRAY + "Players - " + ChatColor.RED + game.playersCount())
                            .enchant(Enchantment.OXYGEN, 1)
                            .flags(ItemFlag.HIDE_ENCHANTS)
                            .get(),
                    (m, e) -> DebugMenuGameOptions.GamesMenu.openGameEditorMenu(player, game)
            );
            items.put(new ItemBuilder(Warlords.getHead(player)).name(ChatColor.GREEN + "Player Options").get(),
                    (m, e) -> DebugMenuPlayerOptions.openPlayerMenu(player, Warlords.getPlayer(player))
            );
            items.put(new ItemBuilder(Material.NOTE_BLOCK).name(ChatColor.GREEN + "Team Options").get(),
                    (m, e) -> {
                        DebugMenuTeamOptions.openTeamMenu(player, game);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (player.getOpenInventory().getTopInventory().getName().equals("Team Options")) {
                                    DebugMenuTeamOptions.openTeamMenu(player, game);
                                } else {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(Warlords.getInstance(), 20, 20);
                    }
            );
        }


        List<ItemStack> itemsArray = new ArrayList<>(items.keySet());
        for (int i = 0; i < items.size(); i++) {
            menu.setItem(i + 1, 1, itemsArray.get(i), items.get(itemsArray.get(i)));
        }

        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

}
