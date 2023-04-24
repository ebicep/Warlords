package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weaponaddons.StatsRerollable;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class WeaponRerollMenu {

    public static <T extends AbstractWeapon & StatsRerollable> void openWeaponRerollMenu(Player player, DatabasePlayer databasePlayer, T weapon) {
        Menu menu = new Menu("Confirm Reroll", 9 * 3);

        int rerollCost = weapon.getRerollCost();
        menu.setItem(2, 1,
                new ItemBuilder(Material.GREEN_CONCRETE)
                        .name(Component.text("Confirm", NamedTextColor.GREEN))
                        .lore(Component.text("Reroll this weapon and reset its stats.", NamedTextColor.GRAY))
                        .addLoreC(weapon.getRerollCostLore())
                        .addLore(
                                Component.empty(),
                                Component.textOfChildren(
                                        Component.text("WARNING: ", NamedTextColor.RED),
                                        Component.text("This action cannot be undone.", NamedTextColor.GRAY)
                                )
                        )
                        .get(),
                (m, e) -> {
                    databasePlayer.getPveStats().subtractCurrency(Currencies.COIN, rerollCost);
                    rerollWeapon(player, databasePlayer, weapon);
                    WeaponManagerMenu.openWeaponEditor(player, databasePlayer, weapon);
                }
        );

        menu.setItem(4, 1,
                weapon.generateItemStack(false),
                (m, e) -> {
                }
        );

        menu.setItem(6, 1,
                new ItemBuilder(Material.RED_CONCRETE)
                        .name(Menu.DENY)
                        .lore(WeaponManagerMenu.GO_BACK)
                        .get(),
                (m, e) -> WeaponManagerMenu.openWeaponEditor(player, databasePlayer, weapon)
        );

        menu.openForPlayer(player);
    }

    public static <T extends AbstractWeapon & StatsRerollable> void rerollWeapon(Player player, DatabasePlayer databasePlayer, T weapon) {
        if (weapon == null) {
            return;
        }
        if (databasePlayer.getPveStats().getWeaponInventory().contains(weapon)) {
            Component component = Component.text("Reroll Result: ", NamedTextColor.GRAY)
                                           .append(weapon.getHoverComponent(false));
            weapon.reroll();
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

            player.sendMessage(component.append(Component.text(" to "))
                                        .append(weapon.getHoverComponent(false))
            );
        }
    }

}
