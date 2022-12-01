package com.ebicep.warlords.menu.generalmenu;

import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabaseSpecialization;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.menu.PlayerHotBarItemListener;
import com.ebicep.warlords.player.general.*;
import com.ebicep.warlords.pve.rewards.RewardInventory;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.menu.WeaponManagerMenu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ebicep.warlords.menu.Menu.*;
import static com.ebicep.warlords.menu.generalmenu.WarlordsShopMenu.openSkillBoostMenu;
import static com.ebicep.warlords.player.general.ExperienceManager.getLevelString;

public class WarlordsNewHotbarMenu {

    public static class SelectionMenu {

        public static void openSelectionMenu(Player player) {
            DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
                Menu menu = new Menu("Rewards Menu", 9 * 6);

                for (Classes value : Classes.VALUES) {
                    Pair<Integer, Integer> menuLocation = ExperienceManager.CLASSES_MENU_LOCATION.get(value);

                    ItemBuilder itemBuilder = new ItemBuilder(value.item)
                            .name(ChatColor.GREEN + value.name)
                            .lore(
                                    value.description,
                                    ""
                            );


                    List<String> specLore = new ArrayList<>();
                    boolean hasRewards = false;
                    for (Specializations spec : value.subclasses) {
                        DatabaseSpecialization databasePlayerSpec = databasePlayer.getSpec(spec);
                        int prestige = databasePlayerSpec.getPrestige();
                        int level = ExperienceManager.getLevelFromExp(databasePlayerSpec.getExperience());
                        long experience = databasePlayerSpec.getExperience();

                        specLore.add((databasePlayer.getLastSpec() == spec ? ChatColor.GREEN : ChatColor.GRAY) + spec.name + ChatColor.DARK_GRAY + " [" + ChatColor.GRAY + getLevelString(
                                level) + ChatColor.DARK_GRAY + "] " + ExperienceManager.getPrestigeLevelString(prestige));
                        specLore.add(ExperienceManager.getProgressStringWithPrestige(experience, level + 1, prestige));
                        specLore.add("");

                        for (int prestigeCheck = 0; prestigeCheck < prestige + 1; prestigeCheck++) {
                            if (prestigeCheck == prestige) {
                                for (int levelCheck = 1; levelCheck <= level; levelCheck++) {
                                    if (!databasePlayerSpec.hasLevelUpReward(levelCheck, prestige)) {
                                        hasRewards = true;
                                        break;
                                    }
                                }
                            } else {
                                for (int levelCheck = 1; levelCheck <= 100; levelCheck++) {
                                    if (!databasePlayerSpec.hasLevelUpReward(levelCheck, prestigeCheck)) {
                                        hasRewards = true;
                                        break;
                                    }
                                }
                            }
                            if (hasRewards) {
                                break;
                            }
                        }
                    }

                    itemBuilder.addLore(specLore);
                    long experience = ExperienceManager.getExperienceForClass(player.getUniqueId(), value);
                    int level = (int) ExperienceManager.calculateLevelFromExp(experience);
                    itemBuilder.addLore(
                            "",
                            ExperienceManager.getProgressString(experience, level + 1),
                            "",
                            ChatColor.YELLOW + "Click here to select a " + value.name + "\n" + ChatColor.YELLOW + "specialization"
                    );
                    if (hasRewards) {
                        itemBuilder.addLore(ChatColor.GREEN + "You have unclaimed rewards!");
                        itemBuilder.enchant(Enchantment.OXYGEN, 1);
                        itemBuilder.flags(ItemFlag.HIDE_ENCHANTS);
                    }
                    menu.setItem(
                            menuLocation.getA(),
                            menuLocation.getB(),
                            itemBuilder.get(),
                            (m, e) -> openLevelingRewardsMenuForClass(player, databasePlayer, value)
                    );
                }

                menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
                menu.openForPlayer(player);
            });
        }

        public static void openLevelingRewardsMenuForClass(Player player, DatabasePlayer databasePlayer, Classes classes) {
            Menu menu = new Menu(classes.name, 9 * 4);

            List<Specializations> values = classes.subclasses;
            for (int i = 0; i < values.size(); i++) {
                Specializations spec = values.get(i);
                DatabaseSpecialization databasePlayerSpec = databasePlayer.getSpec(spec);
                int prestige = databasePlayerSpec.getPrestige();
                int level = ExperienceManager.getLevelFromExp(databasePlayerSpec.getExperience());
                long experience = databasePlayerSpec.getExperience();

                boolean hasRewards = false;
                for (int prestigeCheck = 0; prestigeCheck < prestige + 1; prestigeCheck++) {
                    if (prestigeCheck == prestige) {
                        for (int levelCheck = 1; levelCheck <= level; levelCheck++) {
                            if (!databasePlayerSpec.hasLevelUpReward(levelCheck, prestige)) {
                                hasRewards = true;
                                break;
                            }
                        }
                    } else {
                        for (int levelCheck = 1; levelCheck <= 100; levelCheck++) {
                            if (!databasePlayerSpec.hasLevelUpReward(levelCheck, prestigeCheck)) {
                                hasRewards = true;
                                break;
                            }
                        }
                    }
                    if (hasRewards) {
                        break;
                    }
                }

                ItemBuilder itemBuilder = new ItemBuilder(spec.specType.itemStack)
                        .name(ChatColor.GREEN + spec.name + " " + ChatColor.DARK_GRAY + "[" +
                                ChatColor.GRAY + "Lv" + getLevelString(level) + ChatColor.DARK_GRAY + "] " +
                                ExperienceManager.getPrestigeLevelString(prestige)
                        )
                        .lore(
                                ExperienceManager.getProgressStringWithPrestige(experience, level + 1, prestige),
                                "",
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "LEFT-CLICK" + ChatColor.YELLOW + " to select this specialization",
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK" + ChatColor.YELLOW + " to claim rewards"
                        );
                if (hasRewards) {
                    itemBuilder.addLore("", ChatColor.GREEN + "You have unclaimed rewards!");
                    itemBuilder.enchant(Enchantment.OXYGEN, 1);
                    itemBuilder.flags(ItemFlag.HIDE_ENCHANTS);
                }
                menu.setItem(
                        9 / 2 - values.size() / 2 + i * 2 - 1,
                        1,
                        itemBuilder
                                .get(),
                        (m, e) -> {
                            if (e.isLeftClick()) {
                                player.sendMessage(ChatColor.GREEN + "You have changed your specialization to: §b" + spec.name);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 2);
                                PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player.getUniqueId());
                                playerSettings.setSelectedSpec(spec);
                                if (!player.getWorld().getName().equals("MainLobby")) {
                                    ArmorManager.resetArmor(player);
                                }

                                AbstractPlayerClass apc = spec.create.get();
                                player.getInventory().setItem(1, new ItemBuilder(apc.getWeapon().getItem(playerSettings.getWeaponSkins()
                                        .getOrDefault(spec, Weapons.FELFLAME_BLADE).getItem())).name("§aWeapon Skin Preview")
                                        .lore("")
                                        .get());

                                openLevelingRewardsMenuForClass(player, databasePlayer, classes);
                                databasePlayer.setLastSpec(spec);
                                PlayerHotBarItemListener.updateWeaponManagerItem(player, databasePlayer);
                                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                            } else if (e.isRightClick()) {
                                ExperienceManager.openLevelingRewardsMenuForSpec(player,
                                        databasePlayer, spec,
                                        1,
                                        databasePlayerSpec.getPrestige()
                                );
                            }
                        }
                );
            }

            menu.setItem(3, 3, MENU_BACK, (m, e) -> ExperienceManager.openLevelingRewardsMenu(player));
            menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        }
    }

    public static class PvPMenu {

        private static final ItemStack MENU_SKINS = new ItemBuilder(Material.PAINTING)
                .name(ChatColor.GREEN + "Weapon Skin Selector")
                .lore("§7Change the cosmetic appearance\n§7of your weapon to better suit\n§7your tastes.", "", "§eClick to change weapon skin!")
                .get();
        private static final ItemStack MENU_ARMOR_SETS = new ItemBuilder(Material.DIAMOND_HELMET)
                .name(ChatColor.AQUA + "Armor Sets " + ChatColor.GRAY + "& " + ChatColor.AQUA + "Helmets " + ChatColor.GOLD + "(Cosmetic)")
                .lore("§7Equip your favorite armor\n§7sets or class helmets")
                .get();
        private static final ItemStack MENU_BOOSTS = new ItemBuilder(Material.BOOKSHELF)
                .name(ChatColor.AQUA + "Weapon Skill Boost")
                .lore("§7Choose which of your skills you\n§7want your equipped weapon to boost.",
                        "",
                        "§cWARNING: §7This does not apply to PvE.",
                        "",
                        "§eClick to change skill boost!"
                )
                .get();
        private static final ItemStack MENU_SETTINGS = new ItemBuilder(Material.NETHER_STAR)
                .name(ChatColor.AQUA + "Settings")
                .lore("§7Allows you to toggle different settings\n§7options.", "", "§eClick to edit your settings.")
                .get();
        private static final ItemStack MENU_SETTINGS_PARTICLE_QUALITY = new ItemBuilder(Material.NETHER_STAR)
                .name(ChatColor.GREEN + "Particle Quality")
                .lore("§7Allows you to control, or\n§7disable, particles and the\n§7amount of them.")
                .get();
        private static final ItemStack MENU_ABILITY_DESCRIPTION = new ItemBuilder(Material.BOOK)
                .name(ChatColor.GREEN + "Class Information")
                .lore("§7Preview of your ability \ndescriptions and specialization \nstats.")
                .get();

        public static void openPvPMenu(Player player) {
            Menu menu = new Menu("Warlords Shop", 9 * 4);

            menu.setItem(1, 1, MENU_SKINS, (m, e) -> WarlordsShopMenu.openWeaponMenu(player, 1));
            menu.setItem(2, 1, MENU_ARMOR_SETS, (m, e) -> WarlordsShopMenu.openArmorMenu(player, 1));
            menu.setItem(3, 1, MENU_BOOSTS, (m, e) -> openSkillBoostMenu(player, PlayerSettings.getPlayerSettings(player.getUniqueId()).getSelectedSpec()));

            menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);

            menu.openForPlayer(player);
        }

    }

    public static class PvEMenu {

        private static final ItemStack WEAPONS_MENU = new ItemBuilder(Material.DIAMOND_SWORD).name("§aWeapons").get();
        private static final ItemStack REWARD_INVENTORY_MENU = new ItemBuilder(Material.ENDER_CHEST).name("§aReward Inventory").get();


        public static void openPvEMenu(Player player) {
            DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
                Menu menu = new Menu("Warlords Shop", 9 * 4);

                List<AbstractWeapon> weapons = databasePlayer.getPveStats().getWeaponInventory();
                Optional<AbstractWeapon> optionalWeapon = weapons.stream()
                        .filter(AbstractWeapon::isBound)
                        .filter(abstractWeapon -> abstractWeapon.getSpecializations() == databasePlayer.getLastSpec())
                        .findFirst();
                ItemStack itemStack = WEAPONS_MENU;
                if (optionalWeapon.isPresent()) {
                    itemStack = optionalWeapon.get().generateItemStack(false);
                }

                menu.setItem(1, 1, itemStack, (m, e) -> WeaponManagerMenu.openWeaponInventoryFromExternal(player));
                menu.setItem(2, 1, REWARD_INVENTORY_MENU, (m, e) -> RewardInventory.openRewardInventory(player, 1));

                menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);

                menu.openForPlayer(player);
            });
        }

    }

}
