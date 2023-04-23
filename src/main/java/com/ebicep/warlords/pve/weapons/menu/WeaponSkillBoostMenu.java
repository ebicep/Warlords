package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.*;

public class WeaponSkillBoostMenu {

    public static LinkedHashMap<Currencies, Long> cost = new LinkedHashMap<>() {{
        put(Currencies.COIN, 10000L);
        put(Currencies.SKILL_BOOST_MODIFIER, 1L);
    }};

    public static List<String> costLore = new ArrayList<>() {{
        add("");
        add(ChatColor.AQUA + "Cost: ");
        cost.forEach((currency, amount) -> add(ChatColor.GRAY + " - " + currency.getCostColoredName(amount)));
    }};

    public static void openWeaponSkillBoostMenu(Player player, DatabasePlayer databasePlayer, AbstractLegendaryWeapon weapon) {
        Menu menu = new Menu("Skill Boost", 9 * 6);

        menu.setItem(
                4,
                1,
                weapon.generateItemStack(false),
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
                if (!weapon.getUnlockedSkillBoosts().contains(skillBoost)) {
                    lore.addAll(WeaponSkillBoostMenu.costLore);
                }
            }
            builder.loreLEGACY(lore);
            menu.setItem(
                    i + 2,
                    3,
                    builder.get(),
                    (m, e) -> {
                        if (selected) {
                            player.sendMessage(ChatColor.RED + "You already have this skill boost selected!");
                            return;
                        }
                        List<String> confirmLore = new ArrayList<>();
                        confirmLore.add(ChatColor.GRAY + "Change Skill Boost to " + ChatColor.GREEN + skillBoost.name);
                        if (!weapon.getUnlockedSkillBoosts().contains(skillBoost)) {
                            confirmLore.addAll(costLore);
                        }
                        if (!weapon.getUnlockedSkillBoosts().contains(skillBoost)) {
                            for (Map.Entry<Currencies, Long> currenciesLongEntry : cost.entrySet()) {
                                Currencies currency = currenciesLongEntry.getKey();
                                long value = currenciesLongEntry.getValue();
                                if (databasePlayer.getPveStats().getCurrencyValue(currency) < value) {
                                    player.sendMessage(ChatColor.RED + "You need " + currency.getCostColoredName(value) + ChatColor.RED + " to unlock this skill boost.");
                                    return;
                                }
                            }
                        }
                        Menu.openConfirmationMenu(
                                player,
                                "Change Skill Boost",
                                3,
                                confirmLore,
                                Collections.singletonList(ChatColor.GRAY + "Go back"),
                                (m2, e2) -> {
                                    unlockSkillBoost(player, databasePlayer, weapon, skillBoost);
                                    openWeaponSkillBoostMenu(player, databasePlayer, weapon);
                                },
                                (m2, e2) -> openWeaponSkillBoostMenu(player, databasePlayer, weapon),
                                (m2) -> {
                                }
                        );
                    }
            );
        }

        menu.setItem(4, 5, Menu.MENU_BACK, (m, e) -> WeaponManagerMenu.openWeaponEditor(player, databasePlayer, weapon));
        menu.openForPlayer(player);
    }

    public static void unlockSkillBoost(Player player, DatabasePlayer databasePlayer, AbstractLegendaryWeapon weapon, SkillBoosts skillBoost) {
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        SkillBoosts oldSkillBoost = weapon.getSelectedSkillBoost();
        weapon.setSelectedSkillBoost(skillBoost);
        if (!weapon.getUnlockedSkillBoosts().contains(skillBoost)) {
            cost.forEach(pveStats::subtractCurrency);
            weapon.getUnlockedSkillBoosts().add(skillBoost);
        }
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);

        player.sendMessage(Component.text(ChatColor.GRAY + "Changed ")
                                    .append(weapon.getHoverComponent(false))
                                    .append(Component.text(ChatColor.GRAY + "'s skill boost from " + ChatColor.GREEN + oldSkillBoost.name + ChatColor.GRAY + " to " + ChatColor.GREEN + skillBoost.name + ChatColor.GRAY + "!")));

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2);
    }

}
