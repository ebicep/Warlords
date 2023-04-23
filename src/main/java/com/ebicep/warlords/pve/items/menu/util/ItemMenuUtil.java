package com.ebicep.warlords.pve.items.menu.util;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.modifiers.ItemBucklerModifier;
import com.ebicep.warlords.pve.items.modifiers.ItemGauntletModifier;
import com.ebicep.warlords.pve.items.modifiers.ItemTomeModifier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.BonusLore;
import com.ebicep.warlords.pve.items.types.ItemType;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.*;
import java.util.function.BiConsumer;

public class ItemMenuUtil {

    public static String getRequirementMetString(boolean requirementMet, String requirement) {
        return (requirementMet ? ChatColor.GREEN + "✔" : ChatColor.RED + "✖") + ChatColor.GRAY + " " + requirement;
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
                    .name(ChatColor.GREEN + "Click to Select Item");
        } else {
            itemBuilder = item.generateItemBuilder()
                              .addLore(
                                      "",
                                      ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to swap this item"
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
                        .name(" ")
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
        List<String> costLore = PvEUtils.getCostLore(cost, false);
        String name = costLore.get(0);
        costLore.remove(0);
        menu.setItem(x, y,
                new ItemBuilder(Material.BOOK)
                        .name(name)
                        .loreLEGACY(costLore)
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
                        .name(" ")
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
                                    .name(" ")
                                    .get(),
                            (m, e) -> {
                            }
                    );
                }
            }
        }
    }

    public static List<String> getTotalBonusLore(List<AbstractItem> equippedItems, boolean skipFirstLine) {
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
        List<String> bonusLore = BasicStatPool.getStatPoolLore(statPool, ChatColor.AQUA + "- ", true);
        List<String> blessCurseLore = new ArrayList<>();
        if (gauntletModifier != 0) {
            blessCurseLore.add(ChatColor.AQUA + "- " + AbstractItem.getModifierCalculatedLore(
                    ItemGauntletModifier.Blessings.VALUES,
                    ItemGauntletModifier.Curses.VALUES,
                    gauntletModifier,
                    true
            ));
        }
        if (tomeModifier != 0) {
            blessCurseLore.add(ChatColor.AQUA + "- " + AbstractItem.getModifierCalculatedLore(
                    ItemTomeModifier.Blessings.VALUES,
                    ItemTomeModifier.Curses.VALUES,
                    tomeModifier,
                    true
            ));
        }
        if (bucklerModifier != 0) {
            blessCurseLore.add(ChatColor.AQUA + "- " + AbstractItem.getModifierCalculatedLore(
                    ItemBucklerModifier.Blessings.VALUES,
                    ItemBucklerModifier.Curses.VALUES,
                    bucklerModifier,
                    true
            ));
        }
        if (!blessCurseLore.isEmpty()) {
            bonusLore.add(ChatColor.AQUA + "Blessings/Curses:");
            bonusLore.addAll(blessCurseLore);
        }
        List<String> bonusLores = equippedItems.stream()
                                               .sorted(Comparator.comparingInt(o -> o.getTier().ordinal()))
                                               .filter(BonusLore.class::isInstance)
                                               .map(BonusLore.class::cast)
                                               .map(BonusLore::getBonusLore)
                                               .filter(Objects::nonNull)
                                               .toList();
        //TODO stack same bonuses
        if (!bonusLores.isEmpty()) {
            bonusLore.add(ChatColor.AQUA + "Special Bonuses:");
            for (String lore : bonusLores) {
                bonusLore.add(ChatColor.AQUA + "- " + lore.replaceAll("\n", "\n     ") + "\n\n");
            }
        }
        List<String> lore = new ArrayList<>();
        if (!skipFirstLine) {
            lore.add(ChatColor.AQUA + "Stat Bonuses:");
        }
        lore.addAll(bonusLore.isEmpty() ? Collections.singletonList(ChatColor.GRAY + "None") : bonusLore);
        return lore;
    }
}
