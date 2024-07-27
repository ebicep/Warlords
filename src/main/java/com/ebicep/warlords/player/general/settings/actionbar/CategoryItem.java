package com.ebicep.warlords.player.general.settings.actionbar;

import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

interface CategoryItem {

    ItemStack getItem();

    BiConsumer<Menu, InventoryClickEvent> onClick();

    class BooleanCategoryItem implements CategoryItem {

        private final ItemBuilder itemBuilder;
        private final Supplier<Boolean> variableSupplier;
        private final Consumer<Boolean> variableConsumer;

        public BooleanCategoryItem(ItemBuilder itemBuilder, Supplier<Boolean> variableSupplier, Consumer<Boolean> variableConsumer) {
            this.itemBuilder = itemBuilder;
            itemBuilder.prependLore(Component.textOfChildren(
                    Component.text("Currently: ", NamedTextColor.GRAY),
                    variableSupplier.get() ? Component.text("ENABLED", NamedTextColor.GREEN) : Component.text("DISABLED", NamedTextColor.RED)
            ));
            this.variableSupplier = variableSupplier;
            this.variableConsumer = variableConsumer;
        }

        @Override
        public ItemStack getItem() {
            return itemBuilder.get();
        }

        @Override
        public BiConsumer<Menu, InventoryClickEvent> onClick() {
            return (m, e) -> variableConsumer.accept(!variableSupplier.get());
        }

    }
}
