package com.ebicep.warlords.pve.items.menu.util;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.addons.ItemAddonClassBonus;
import com.ebicep.warlords.pve.items.modifiers.ItemBucklerModifier;
import com.ebicep.warlords.pve.items.modifiers.ItemGauntletModifier;
import com.ebicep.warlords.pve.items.modifiers.ItemTomeModifier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.BonusLore;
import com.ebicep.warlords.pve.items.types.ItemType;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.*;
import java.util.function.BiConsumer;

public class ItemMenuUtil {

    public static Component getRequirementMetString(boolean requirementMet, String requirement) {
        return Component.textOfChildren(
                requirementMet ? Component.text("✔ ", NamedTextColor.GREEN) : Component.text("✖ ", NamedTextColor.RED),
                Component.text(requirement, NamedTextColor.GRAY)
        );
    }

    public static void addItemTierRequirement(
            Menu menu,
            ItemTier tier,
            AbstractItem item,
            int x,
            int y,
            BiConsumer<Menu, InventoryClickEvent> onClick
    ) {
        ItemBuilder itemBuilder;
        if (item == null) {
            itemBuilder = new ItemBuilder(tier.clayBlock)
                    .name(Component.text("Click to Select Item", NamedTextColor.GREEN));
        } else {
            itemBuilder = item.generateItemBuilder()
                              .addLore(
                                      Component.empty(),
                                      Component.textOfChildren(
                                              Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                              Component.text(" to swap this item", NamedTextColor.GREEN)
                                      )
                              );
        }
        menu.setItem(x, y,
                itemBuilder.get(),
                onClick
        );
        addPaneRequirement(menu, x + 1, y, item != null);
    }

    public static void addPaneRequirement(Menu menu, int x, int y, boolean requirementMet) {
        menu.setItem(x, y,
                new ItemBuilder(requirementMet ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE)
                        .name(Component.text(" "))
                        .get(),
                (m, e) -> {
                }
        );
    }

    public static void addSpendableCostRequirement(
            DatabasePlayer databasePlayer,
            Menu menu,
            LinkedHashMap<Spendable, Long> cost,
            int x,
            int y
    ) {
        List<Component> costLore = PvEUtils.getCostLore(cost, false);
        Component name = costLore.get(0);
        costLore.remove(0);
        menu.setItem(x, y,
                new ItemBuilder(Material.BOOK)
                        .name(name)
                        .lore(costLore)
                        .get(),
                (m, e) -> {
                }
        );
        boolean hasRequiredCosts = cost
                .entrySet()
                .stream()
                .allMatch(spendableLongEntry -> spendableLongEntry.getKey().getFromPlayer(databasePlayer) >= spendableLongEntry.getValue());
        menu.setItem(x + 1, y,
                new ItemBuilder(hasRequiredCosts ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE)
                        .name(Component.text(" "))
                        .get(),
                (m, e) -> {
                }
        );
    }

    public static void addItemConfirmation(
            Menu menu,
            Runnable onCenterClick
    ) {
        for (int i = 5; i < 8; i++) {
            for (int j = 1; j < 4; j++) {
                if (i == 6 && j == 2) {
                    onCenterClick.run();
                } else {
                    menu.setItem(i, j,
                            new ItemBuilder(Material.IRON_BARS)
                                    .name(Component.text(" "))
                                    .get(),
                            (m, e) -> {
                            }
                    );
                }
            }
        }
    }

    public static List<Component> getTotalBonusLore(List<AbstractItem> equippedItems, boolean skipFirstLine) {
        HashMap<BasicStatPool, Integer> statPool = new HashMap<>();
        float gauntletModifier = 0;
        float tomeModifier = 0;
        float bucklerModifier = 0;
        for (AbstractItem equippedItem : equippedItems) {
            ItemType type = equippedItem.getType();
            equippedItem.getStatPool().forEach((stat, tier) -> statPool.merge(stat, tier, Integer::sum));
            switch (type) {
                case GAUNTLET -> gauntletModifier += equippedItem.getModifierCalculated();
                case TOME -> tomeModifier += equippedItem.getModifierCalculated();
                case BUCKLER -> bucklerModifier += equippedItem.getModifierCalculated();
            }
        }
        List<Component> bonusLore = BasicStatPool.getStatPoolLore(statPool, Component.text("- ", NamedTextColor.AQUA), true, null);
        List<Component> blessCurseLore = new ArrayList<>();
        if (gauntletModifier != 0) {
            List<Component> lore = AbstractItem.getModifierCalculatedLore(
                    ItemGauntletModifier.Blessings.VALUES,
                    ItemGauntletModifier.Curses.VALUES,
                    gauntletModifier,
                    true
            );
            addBlessCurseLore(blessCurseLore, lore);
        }
        if (tomeModifier != 0) {
            List<Component> lore = AbstractItem.getModifierCalculatedLore(
                    ItemTomeModifier.Blessings.VALUES,
                    ItemTomeModifier.Curses.VALUES,
                    tomeModifier,
                    true
            );
            addBlessCurseLore(blessCurseLore, lore);
        }
        if (bucklerModifier != 0) {
            List<Component> lore = AbstractItem.getModifierCalculatedLore(
                    ItemBucklerModifier.Blessings.VALUES,
                    ItemBucklerModifier.Curses.VALUES,
                    bucklerModifier,
                    true
            );
            addBlessCurseLore(blessCurseLore, lore);
        }
        if (!blessCurseLore.isEmpty()) {
            bonusLore.add(Component.text("Blessings/Curses:", NamedTextColor.AQUA));
            bonusLore.addAll(blessCurseLore);
        }
        HashMap<Classes, LinkedHashSet<List<Component>>> bonuses = new HashMap<>();
        equippedItems.stream()
                     .sorted(Comparator.comparingInt(o -> o.getTier().ordinal()))
                     .filter(BonusLore.class::isInstance)
                     .filter(item -> ((BonusLore) item).getBonusLore() != null)
                     .forEach(item -> {
                         BonusLore bonus = (BonusLore) item;
                         if (item instanceof ItemAddonClassBonus classBonus) {
                             bonuses.computeIfAbsent(classBonus.getClasses(), k -> new LinkedHashSet<>()).add(bonus.getBonusLore());
                         } else {
                             bonuses.computeIfAbsent(null, k -> new LinkedHashSet<>()).add(bonus.getBonusLore());
                         }
                     });
        if (!bonuses.isEmpty()) {
            bonusLore.add(Component.text("Special Bonuses:", NamedTextColor.AQUA));
            bonuses.entrySet()
                   .stream()
                   .sorted((o1, o2) -> {
                       if (o1.getKey() == null) {
                           return -1;
                       } else if (o2.getKey() == null) {
                           return 1;
                       } else {
                           return o1.getKey().compareTo(o2.getKey());
                       }
                   })
                   .forEachOrdered(entry -> {
                       Classes classes = entry.getKey();
                       LinkedHashSet<List<Component>> lists = entry.getValue();
                       bonusLore.add(Component.textOfChildren(
                               Component.text("- ", NamedTextColor.AQUA),
                               Component.text(classes == null ? "General" : classes.name, NamedTextColor.GREEN)
                       ));
                       if (classes == null) {
                           lists.forEach(bonusLores -> {
                               for (int i = 1; i < bonusLores.size(); i++) {
                                   Component lore = bonusLores.get(i);
                                   if (i == 1) {
                                       bonusLore.add(Component.textOfChildren(
                                               Component.text("    "),
                                               Component.text("- ", NamedTextColor.AQUA),
                                               lore
                                       ));
                                   } else {
                                       bonusLore.add(Component.text("       ").append(lore));
                                   }
                               }
                           });
                       } else {
                           lists.forEach(bonusLores -> {
                               for (int i = 1; i < bonusLores.size(); i++) {
                                   Component lore = bonusLores.get(i);
                                   if (i == 1) {
                                       bonusLore.add(Component.textOfChildren(
                                               Component.text("    "),
                                               Component.text("- ", NamedTextColor.AQUA),
                                               lore
                                       ));
                                   } else {
                                       bonusLore.add(Component.text("       ").append(lore));
                                   }
                               }
                           });
                       }
                   });
        }
        List<Component> lore = new ArrayList<>();
        if (!skipFirstLine) {
            lore.add(Component.text("Stat Bonuses:", NamedTextColor.AQUA));
        }
        lore.addAll(bonusLore.isEmpty() ? Collections.singletonList(Component.text("None", NamedTextColor.GRAY)) : bonusLore);
        return lore;
    }

    private static void addBlessCurseLore(List<Component> blessCurseLore, List<Component> lore) {
        for (int i = 0; i < lore.size(); i++) {
            Component component = lore.get(i);
            if (i == 0) {
                blessCurseLore.add(Component.textOfChildren(
                        Component.text("- ", NamedTextColor.AQUA),
                        component
                ));
            } else {
                blessCurseLore.add(component);
            }
        }
    }
}
