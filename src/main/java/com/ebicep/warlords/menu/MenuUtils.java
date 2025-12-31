package com.ebicep.warlords.menu;

import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.NamedEnum;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class MenuUtils {

    public static <T extends Enum<T> & NamedEnum> void openEnumSelectorMenu(
            Player player,
            String menuName,
            T[] values,
            EnumSet<T> filter,
            Function<T, ItemStack> itemStackFunction,
            BiConsumer<Menu, InventoryClickEvent> backAction
    ) {
        Menu menu = new Menu(menuName, 5 * 9);

        for (int i = 0; i < values.length; i++) {
            T attribute = values[i];
            boolean filtered = filter.contains(attribute);
            ItemBuilder itemBuilder = new ItemBuilder(itemStackFunction.apply(attribute))
                    .name(Component.text(attribute.getName(), filtered ? NamedTextColor.GREEN : NamedTextColor.RED));
            if (filtered) {
                itemBuilder.enchant(Enchantment.RESPIRATION, 1);
            }
            menu.setItem(i % 7 + 1, i / 7 + 1,
                    itemBuilder
                            .get(),
                    (m, e) -> {
                        if (filtered) {
                            filter.remove(attribute);
                        } else {
                            filter.add(attribute);
                        }
                        openEnumSelectorMenu(
                                player,
                                menuName,
                                values,
                                filter,
                                itemStackFunction,
                                backAction
                        );
                    }
            );
        }

        menu.setItem(4, 4, Menu.MENU_BACK, backAction);
        menu.openForPlayer(player);
    }

}
