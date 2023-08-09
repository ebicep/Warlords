package com.ebicep.warlords.menu.debugmenu;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
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
        items.put(new ItemBuilder(Material.END_PORTAL_FRAME).name(Component.text("Game Options", NamedTextColor.GREEN)).get(),
                (m, e) -> DebugMenuGameOptions.openGameMenu(player)
        );

        WarlordsEntity warlordsPlayer = Warlords.getPlayer(player);
        if (warlordsPlayer != null) {
            Game game = warlordsPlayer.getGame();
            items.put(new ItemBuilder(Material.BOOK)
                            .name(Component.text("Game - " + game.getGameId(), NamedTextColor.GREEN))
                            .lore(
                                    Component.text("Map - ", NamedTextColor.DARK_GRAY).append(Component.text(game.getMap().getMapName(), NamedTextColor.RED)),
                                    Component.text("GameMode - ", NamedTextColor.DARK_GRAY).append(Component.text(game.getGameMode().name, NamedTextColor.RED)),
                                    Component.text("Addons - ", NamedTextColor.DARK_GRAY).append(Component.text(game.getAddons().toString(), NamedTextColor.RED)),
                                    Component.text("Players - ", NamedTextColor.DARK_GRAY).append(Component.text(String.valueOf(game.playersCount()), NamedTextColor.RED))
                            )
                            .enchant(Enchantment.OXYGEN, 1)
                            .get(),
                    (m, e) -> DebugMenuGameOptions.GamesMenu.openGameEditorMenu(player, game)
            );
            items.put(new ItemBuilder(HeadUtils.getHead(player)).name(Component.text("Player Options", NamedTextColor.GREEN)).get(),
                    (m, e) -> DebugMenuPlayerOptions.openPlayerMenu(player, Warlords.getPlayer(player))
            );
            items.put(new ItemBuilder(Material.NOTE_BLOCK).name(Component.text("Team Options", NamedTextColor.GREEN)).get(),
                    (m, e) -> {
                        DebugMenuTeamOptions.openTeamSelectorMenu(player, game);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (PlainTextComponentSerializer.plainText().serialize(player.getOpenInventory().title()).equals("Team Options")) {
                                    DebugMenuTeamOptions.openTeamSelectorMenu(player, game);
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
