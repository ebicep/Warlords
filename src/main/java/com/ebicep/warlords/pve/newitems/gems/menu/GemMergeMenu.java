package com.ebicep.warlords.pve.newitems.gems.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.newitems.NewItemsUtils;
import com.ebicep.warlords.pve.newitems.gems.Gem;
import com.ebicep.warlords.pve.newitems.gems.GemTier;
import com.ebicep.warlords.pve.newitems.gems.GemType;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class GemMergeMenu {

    public static void open(Player player) {
        open(player, null);
    }

    /**
     * @param onBack where the back button leads, or null to close the menu instead
     */
    public static void open(Player player, Runnable onBack) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        Menu menu = new Menu("Merge Gems", 9 * 6);

        for (int typeIndex = 0; typeIndex < GemType.VALUES.length; typeIndex++) {
            GemType type = GemType.VALUES[typeIndex];
            for (int tierIndex = 0; tierIndex < GemTier.VALUES.length; tierIndex++) {
                Gem gem = Gem.of(type, GemTier.VALUES[tierIndex]);
                menu.setItem(2 + tierIndex, typeIndex, gemItem(databasePlayer, gem), (m, e) -> {
                    LinkedHashMap<Spendable, Long> cost = gem.getMergeCost();
                    if (cost == null) {
                        player.sendMessage(Component.text(gem.getName() + " is already the highest tier!", NamedTextColor.RED));
                        return;
                    }
                    if (!PvEUtils.hasEnough(player, databasePlayer, cost, "to merge this gem")) {
                        return;
                    }
                    openConfirmation(player, databasePlayer, gem, cost, onBack);
                });
            }
        }

        if (onBack == null) {
            menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        } else {
            menu.setItem(4, 5, Menu.MENU_BACK, (m, e) -> onBack.run());
        }
        menu.openForPlayer(player);
    }

    private static ItemStack gemItem(DatabasePlayer databasePlayer, Gem gem) {
        Gem result = gem.getMergeResult();
        long owned = databasePlayer.getPveStats().getGems(gem);

        List<Component> lore = new ArrayList<>();
        lore.add(gem.getAttributeComponent());
        lore.add(Component.empty());
        lore.add(Component.text("Owned: ", NamedTextColor.GRAY).append(Component.text(owned, NamedTextColor.YELLOW)));
        lore.add(Component.empty());
        if (result == null) {
            lore.add(Component.text("Highest tier - cannot be merged.", NamedTextColor.DARK_GRAY));
        } else {
            lore.addAll(WordWrap.wrap(Component.text("Merge into ", NamedTextColor.GRAY)
                                               .append(result.getColoredName())
                                               .append(Component.text(".", NamedTextColor.GRAY)), 160));
            lore.addAll(PvEUtils.getCostLore(gem.getMergeCost(), true));
            lore.add(Component.empty());
            lore.add(Component.text("Click to merge", NamedTextColor.YELLOW));
        }

        return new ItemBuilder(gem.getItem())
                .name(gem.getColoredName())
                .lore(lore)
                .amount((int) Math.max(1, Math.min(64, owned)))
                .get();
    }

    private static void openConfirmation(
            Player player,
            DatabasePlayer databasePlayer,
            Gem gem,
            LinkedHashMap<Spendable, Long> cost,
            Runnable onBack
    ) {
        Gem result = gem.getMergeResult();
        List<Component> confirmLore = new ArrayList<>(WordWrap.wrap(
                Component.text("Merge into ", NamedTextColor.GRAY)
                         .append(result.getColoredName())
                         .append(Component.text(".", NamedTextColor.GRAY)),
                160
        ));
        confirmLore.addAll(PvEUtils.getCostLore(cost, true));

        Menu.openConfirmationMenu(player,
                "Confirm Merge",
                3,
                confirmLore,
                Menu.GO_BACK,
                (m, e) -> {
                    if (!PvEUtils.hasEnough(player, databasePlayer, cost, "to merge this gem")) {
                        return;
                    }
                    cost.forEach((spendable, amount) -> spendable.subtractFromPlayer(databasePlayer, amount));
                    result.addToPlayer(databasePlayer, 1);
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

                    NewItemsUtils.sendItemMessage(player, Component.text("You merged ", NamedTextColor.GRAY)
                                                                   .append(Component.text(GemTier.MERGE_AMOUNT + "x ", NamedTextColor.GRAY))
                                                                   .append(gem.getColoredName())
                                                                   .append(Component.text(" into ", NamedTextColor.GRAY))
                                                                   .append(result.getColoredName())
                    );
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2);
                    open(player, onBack);
                },
                (m, e) -> open(player, onBack),
                (m) -> m.setItem(4, 1, result.getDisplayItem(), Menu.ACTION_DO_NOTHING)
        );
    }

}
