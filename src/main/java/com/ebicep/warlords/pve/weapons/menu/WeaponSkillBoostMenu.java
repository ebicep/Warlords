package com.ebicep.warlords.pve.weapons.menu;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WeaponSkillBoostMenu {

    public static LinkedHashMap<Currencies, Long> cost = new LinkedHashMap<>() {{
        put(Currencies.COIN, 10000L);
        put(Currencies.SKILL_BOOST_MODIFIER, 1L);
    }};

    public static List<Component> costLore = PvEUtils.getCostLore(cost, true);

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
                    .name(Component.text(skillBoost.name, NamedTextColor.GREEN));
            List<Component> lore = new ArrayList<>();
            boolean selected = skillBoost == weapon.getSelectedSkillBoost();
            if (selected) {
                lore.add(Component.text("Currently selected!", NamedTextColor.GREEN));
                builder.enchant(Enchantment.OXYGEN, 1);
            } else {
                lore.add(Component.text("Click to select!", NamedTextColor.YELLOW));
                if (!weapon.getUnlockedSkillBoosts().contains(skillBoost)) {
                    lore.addAll(WeaponSkillBoostMenu.costLore);
                }
            }
            builder.lore(lore);
            menu.setItem(
                    i + 2,
                    3,
                    builder.get(),
                    (m, e) -> {
                        if (selected) {
                            player.sendMessage(Component.text("You already have this skill boost selected!", NamedTextColor.RED));
                            return;
                        }
                        List<Component> confirmLore = new ArrayList<>();
                        confirmLore.add(Component.textOfChildren(
                                Component.text("Change Skill Boost to ", NamedTextColor.GRAY),
                                Component.text(skillBoost.name, NamedTextColor.GREEN)
                        ));
                        if (!weapon.getUnlockedSkillBoosts().contains(skillBoost)) {
                            confirmLore.addAll(costLore);
                        }
                        if (!weapon.getUnlockedSkillBoosts().contains(skillBoost)) {
                            for (Map.Entry<Currencies, Long> currenciesLongEntry : cost.entrySet()) {
                                Currencies currency = currenciesLongEntry.getKey();
                                long value = currenciesLongEntry.getValue();
                                if (databasePlayer.getPveStats().getCurrencyValue(currency) < value) {
                                    player.sendMessage(Component.text("You need ", NamedTextColor.RED)
                                                                .append(currency.getCostColoredName(value))
                                                                .append(Component.text(" to unlock this skill boost."))
                                    );
                                    return;
                                }
                            }
                        }
                        Menu.openConfirmationMenu(
                                player,
                                "Change Skill Boost",
                                3,
                                confirmLore,
                                Menu.GO_BACK,
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

        player.sendMessage(Component.text("Changed ", NamedTextColor.GRAY)
                                    .append(weapon.getHoverComponent(false))
                                    .append(Component.text("'s skill boost from "))
                                    .append(Component.text(oldSkillBoost.name, NamedTextColor.GREEN))
                                    .append(Component.text(" to "))
                                    .append(Component.text(skillBoost.name))
                                    .append(Component.text("!")));

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 500, 2);
    }

}
