package com.ebicep.warlords.pve.newitems.menu;

import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.newitems.NewItemLoreCreator;
import com.ebicep.warlords.pve.newitems.setbonus.NewItemsSetBonus;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.menu.Menu.MENU_CLOSE;

public class NewItemSetsMenu {

    private static final int SETS_PER_PAGE = 9 * 5;

    public static void open(Player player) {
        open(player, 1);
    }

    public static void open(Player player, int page) {
        Menu menu = new Menu("Item Sets", 9 * 6);
        NewItemsSetBonus[] setBonuses = NewItemsSetBonus.VALUES;

        for (int i = 0; i < SETS_PER_PAGE; i++) {
            int setIndex = i + (page - 1) * SETS_PER_PAGE;
            if (setIndex >= setBonuses.length) {
                break;
            }
            NewItemsSetBonus setBonus = setBonuses[setIndex];
            try {
                List<Component> lore = new NewItemLoreCreator.Builder(setBonus)
                        .addStarComponent()
                        .addBasicAttributes()
                        .addBonusAttributes()
                        .addSetBonus()
                        .build();
                menu.setItem(
                        i % 9, i / 9,
                        new ItemBuilder(setBonus.getSlots().getFirst().getMaterial())
                                .name(Component.text(setBonus.getName(), setBonus.getTier().getTextColor()))
                                .lore(lore)
                                .get(),
                        (m, e) -> {}
                );
            } catch (Exception e) {
                ChatUtils.MessageType.NEW_ITEMS.sendErrorMessage(new Throwable("Error opening New Item Sets Menu for " + setBonus));
            }
        }

        if (page > 1) {
            menu.setItem(
                    0, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Previous Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page - 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> open(player, page - 1)
            );
        }
        if (setBonuses.length > page * SETS_PER_PAGE) {
            menu.setItem(
                    8, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Next Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page + 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> open(player, page + 1)
            );
        }

        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

}
