package com.ebicep.warlords.game.option.raid;

import com.ebicep.warlords.commands.debugcommands.game.GameStartCommand;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import static com.ebicep.warlords.menu.Menu.*;

public class RaidMenu {

    public static void openRaidMenu(Player player) {
        Menu menu = new Menu("Raid Menu", 9 * 4);
        Raid[] index = Raid.VALUES;
        for (int i = 0; i < index.length; i++) {
            Raid raid = index[i];
            int finalI = i;
            menu.setItem(
                    i % 7 + 1,
                    1,
                    new ItemBuilder(Material.REDSTONE)
                            .name(Component.text(raid.getName(), NamedTextColor.RED, TextDecoration.BOLD))
                            .lore(WordWrap.wrap(Component.text(raid.getDescription(), NamedTextColor.DARK_GRAY), 150))
                            .addLore(
                                    Component.empty(),
                                    Component.text("Minimum level: ", NamedTextColor.GRAY).append(Component.text(raid.getMinimumClassLevel(), NamedTextColor.RED)),
                                    Component.text("Minimum player requirement: ", NamedTextColor.GRAY).append(Component.text("4", NamedTextColor.GOLD)),
                                    Component.empty(),
                                    Component.text("Completion Rewards:", NamedTextColor.GRAY),
                                    Component.text("+300,000 Class Experience", NamedTextColor.DARK_AQUA),
                                    Currencies.COIN.getCostColoredName(300_000, "+"),
                                    Currencies.SYNTHETIC_SHARD.getCostColoredName(1000, "+"),
                                    Component.text("+1 Raid Insignia", NamedTextColor.RED), //TODO put other rewards into spendable enum
                                    Component.text("+1 Ascendant Fragment", NamedTextColor.DARK_RED),
                                    Component.empty(),
                                    Component.text("Possible Bonus Rewards:", NamedTextColor.GRAY),
                                    Currencies.LEGEND_FRAGMENTS.getCostColoredName(50, "+"),
                                    Component.text("+1 Legendary Weapon", NamedTextColor.GOLD)
                            )
                            .get(),
                    (m, e) -> {
                        GameMap map = null;
                        switch (finalI) {
                            case 0 -> map = GameMap.THE_OBSIDIAN_TRAIL_RAID;
                            case 1, 2, 3, 4, 5, 6 -> player.sendMessage(Component.text("WIP", NamedTextColor.RED));
                        }

                        GameMap finalMap = map;
                        if (finalMap != null) {
                            GameStartCommand.startGamePvERaid(player, queueEntryBuilder ->
                                    queueEntryBuilder.setMap(finalMap)
                                                     .setRequestedGameAddons(GameAddon.PRIVATE_GAME)

                            );
                        }
                    }
            );
            menu.setItem(3, 3, MENU_BACK, (m, e) -> openRaidMenu(player));
            menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        }
        menu.openForPlayer(player);
    }
}
