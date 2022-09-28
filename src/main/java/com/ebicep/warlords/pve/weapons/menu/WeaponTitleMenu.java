package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryWeapon;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class WeaponTitleMenu {

    public static void openWeaponTitleMenu(Player player, AbstractLegendaryWeapon weapon) {
        Menu menu = new Menu("Apply Title to Weapon", 9 * 3);

        for (int i = 0; i < LegendaryTitles.VALUES.length; i++) {
            LegendaryTitles title = LegendaryTitles.VALUES[i];
            //TODO check if title is the same
            AbstractLegendaryWeapon titledWeapon = title.titleWeapon.apply(weapon);
            menu.setItem(i * 2 + 1, 1 + ((i / 3) * 2),
                    new ItemBuilder(Material.NAME_TAG)
                            .name(ChatColor.GREEN + title.title)
                            .get(),
                    (m, e) -> {
                    }
            );
        }

        menu.openForPlayer(player);

    }

}
