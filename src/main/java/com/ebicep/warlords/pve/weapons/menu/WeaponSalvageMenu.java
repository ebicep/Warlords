package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.pve.weapons.weapontypes.Salvageable;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.TextComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class WeaponSalvageMenu {

    public static void openWeaponSalvageConfirmMenu(Player player, AbstractWeapon weapon) {
        Menu menu = new Menu("Confirm salvage", 9 * 3);

        menu.setItem(2, 1,
                new ItemBuilder(Material.STAINED_CLAY, 1, (short) 13)
                        .name(ChatColor.GREEN + "Confirm")
                        .lore(
                                ChatColor.GRAY + "Salvage this weapon and claim its materials.",
                                "",
                                ChatColor.RED + "WARNING: " + ChatColor.GRAY + "This action cannot be undone."
                        )
                        .get(),
                (m, e) -> {
                    salvageWeapon(player, weapon);
                    WeaponManagerMenu.openWeaponInventoryFromInternal(player);
                }
        );

        menu.setItem(4, 1,
                weapon.generateItemStack(),
                (m, e) -> {
                }
        );

        menu.setItem(6, 1,
                new ItemBuilder(Material.STAINED_CLAY, 1, (short) 14)
                        .name(ChatColor.RED + "Deny")
                        .lore(ChatColor.GRAY + "Go back.")
                        .get(),
                (m, e) -> WeaponManagerMenu.openWeaponEditor(player, weapon)
        );

        menu.openForPlayer(player);
    }

    public static void salvageWeapon(Player player, AbstractWeapon weapon) {
        if (!(weapon instanceof Salvageable)) {
            return;
        }
        Salvageable salvageable = (Salvageable) weapon;
        int salvageAmount = salvageable.getSalvageAmount();

        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        if (databasePlayer.getPveStats().getWeaponInventory().contains(weapon)) {
            databasePlayer.getPveStats().getWeaponInventory().remove(weapon);
            databasePlayer.getPveStats().addSyntheticAlloy(salvageAmount);
            DatabaseManager.updatePlayerAsync(databasePlayer);

            player.spigot().sendMessage(
                    new TextComponent(ChatColor.GRAY + "You received " + ChatColor.AQUA + salvageAmount + " Synthetic Shard" + (salvageAmount == 1 ? "" : "s") + ChatColor.GRAY + " from salvaging "),
                    new TextComponentBuilder(WeaponsPvE.getWeapon(weapon).getGeneralName())
                            .setHoverItem(weapon.generateItemStack())
                            .getTextComponent());
        }
    }
}
