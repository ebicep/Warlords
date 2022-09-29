package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.TextComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.Collections;
import java.util.List;

import static com.ebicep.warlords.menu.Menu.MENU_BACK;
import static com.ebicep.warlords.pve.weapons.menu.WeaponManagerMenu.openWeaponEditor;

public class WeaponTitleMenu {

    public static void openWeaponTitleMenu(Player player, AbstractLegendaryWeapon weapon) {
        Menu menu = new Menu("Apply Title to Weapon", 9 * 6);

        menu.setItem(
                4,
                0,
                weapon.generateItemStack(),
                (m, e) -> {
                }
        );

        for (int i = 0; i < LegendaryTitles.VALUES.length; i++) {
            LegendaryTitles title = LegendaryTitles.VALUES[i];
            AbstractLegendaryWeapon titledWeapon = title.titleWeapon.apply(weapon);

            ItemBuilder itemBuilder = new ItemBuilder(titledWeapon.generateItemStack());
            boolean equals = weapon.getClass().equals(title.clazz);
            if (equals) {
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
                itemBuilder.flags(ItemFlag.HIDE_ENCHANTS);
            }
            menu.setItem(i * 3 + 1, 1 + (i / 3),
                    itemBuilder.get(),
                    (m, e) -> {
                        if (!equals) {
                            Menu.openConfirmationMenu(
                                    player,
                                    "Apply Title",
                                    3,
                                    Collections.singletonList(ChatColor.GRAY + "Apply " + ChatColor.GREEN + title.title + ChatColor.GRAY + " title"),
                                    Collections.singletonList(ChatColor.GRAY + "Go back"),
                                    (m2, e2) -> {
                                        AbstractLegendaryWeapon newTitledWeapon = titleWeapon(player, weapon, title);
                                        if (newTitledWeapon != null) {
                                            openWeaponTitleMenu(player, newTitledWeapon);
                                        }
                                    },
                                    (m2, e2) -> openWeaponTitleMenu(player, weapon),
                                    (m2) -> {
                                    }
                            );
                        } else {
                            player.sendMessage(ChatColor.RED + "You already have this title on your weapon!");
                        }
                    }
            );
        }

        menu.setItem(4, 5, MENU_BACK, (m, e) -> openWeaponEditor(player, weapon));
        menu.openForPlayer(player);
    }

    public static AbstractLegendaryWeapon titleWeapon(Player player, AbstractLegendaryWeapon weapon, LegendaryTitles title) {
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        if (databasePlayer == null) {
            return null;
        }
        AbstractLegendaryWeapon titledWeapon = title.titleWeapon.apply(weapon);
        List<AbstractWeapon> weaponInventory = databasePlayer.getPveStats().getWeaponInventory();
        weaponInventory.remove(weapon);
        weaponInventory.add(titledWeapon);
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

        player.spigot().sendMessage(
                new TextComponent(ChatColor.GRAY + "Titled Weapon: "),
                new TextComponentBuilder(weapon.getName())
                        .setHoverItem(weapon.generateItemStack())
                        .getTextComponent(),
                new TextComponent(ChatColor.GRAY + " and it became "),
                new TextComponentBuilder(titledWeapon.getName())
                        .setHoverItem(titledWeapon.generateItemStack())
                        .getTextComponent(),
                new TextComponent(ChatColor.GRAY + "!")
        );

        return titledWeapon;
    }

}
