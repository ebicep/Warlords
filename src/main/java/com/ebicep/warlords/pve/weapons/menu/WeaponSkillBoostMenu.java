package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.TextComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;

public class WeaponSkillBoostMenu {

    public static void openWeaponSkillBoostMenu(Player player, DatabasePlayer databasePlayer, AbstractLegendaryWeapon weapon) {
        Menu menu = new Menu("Skill Boost", 9 * 6);

        menu.setItem(
                4,
                1,
                weapon.generateItemStack(),
                (m, e) -> {
                }
        );

        Specializations specializations = weapon.getSpecializations();
        List<SkillBoosts> values = specializations.skillBoosts;
        for (int i = 0; i < values.size(); i++) {
            SkillBoosts skillBoost = values.get(i);
            ItemBuilder builder = new ItemBuilder(specializations.specType.itemStack)
                    .name(ChatColor.GREEN + skillBoost.name)
                    .flags(ItemFlag.HIDE_ENCHANTS);
            List<String> lore = new ArrayList<>();
            boolean selected = skillBoost == weapon.getSelectedSkillBoost();
            if (selected) {
                lore.add(ChatColor.GREEN + "Currently selected!");
                builder.enchant(Enchantment.OXYGEN, 1);
            } else {
                lore.add(ChatColor.YELLOW + "Click to select!");
            }
            builder.lore(lore);
            menu.setItem(
                    i + 2,
                    3,
                    builder.get(),
                    (m, e) -> {
                        if (selected) {
                            player.sendMessage(ChatColor.RED + "You already have this skill boost selected!");
                            return;
                        }
                        if (databasePlayer.getPveStats().getCurrencyValue(Currencies.SKILL_BOOST_MODIFIER) < 1) {
                            player.sendMessage(ChatColor.RED + "You need a " + Currencies.SKILL_BOOST_MODIFIER.getColoredName() +
                                    ChatColor.RED + " to change boosts!");
                            return;
                        }
                        unlockSkillBoost(player, databasePlayer, weapon, skillBoost);
                        openWeaponSkillBoostMenu(player, databasePlayer, weapon);
                    }
            );
        }

        menu.setItem(4, 5, Menu.MENU_BACK, (m, e) -> WeaponManagerMenu.openWeaponEditor(player, databasePlayer, weapon));
        menu.openForPlayer(player);
    }

    public static void unlockSkillBoost(Player player, DatabasePlayer databasePlayer, AbstractLegendaryWeapon weapon, SkillBoosts skillBoost) {
        SkillBoosts oldSkillBoost = weapon.getSelectedSkillBoost();
        weapon.setSelectedSkillBoost(skillBoost);
        databasePlayer.getPveStats().subtractCurrency(Currencies.SKILL_BOOST_MODIFIER, 1);
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

        player.spigot().sendMessage(
                new TextComponentBuilder(ChatColor.GRAY + "Changed ")
                        .getTextComponent(),
                new TextComponentBuilder(weapon.getName())
                        .setHoverItem(weapon.generateItemStack())
                        .getTextComponent(),
                new TextComponentBuilder(ChatColor.GRAY + "'s skill boost from " + ChatColor.GREEN + oldSkillBoost.name + ChatColor.GRAY + " to " + ChatColor.GREEN + skillBoost.name + ChatColor.GRAY + "!")
                        .getTextComponent()
        );
    }

}
