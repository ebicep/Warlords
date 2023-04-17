package com.ebicep.warlords.pve;

import com.ebicep.warlords.commands.debugcommands.game.GameStartCommand;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.menu.Menu.MENU_CLOSE;

public class OnslaughtMenu {

    public static void openMenu(Player player) {
        Menu menu = new Menu("Onslaught Menu", 9 * 4);
        menu.setItem(
                4,
                1,
                new ItemBuilder(Material.BLAZE_POWDER).name(ChatColor.GREEN + "Start an Onslaught game").get(),
                (m, e) -> GameStartCommand.startGamePvE(player, GameMode.ONSLAUGHT, queueEntryBuilder ->
                        queueEntryBuilder.setMap(GameMap.ILLUSION_PHANTOM)
                                .setRequestedGameAddons(GameAddon.PRIVATE_GAME)

                )
        );
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }
}