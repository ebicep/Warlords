package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;

import javax.annotation.Nonnull;

public class MaxedWeaponOption implements Option {

    public static void weaponLeftClick(WarlordsEntity wp, Player player) {
        AbstractPlayerClass spec = wp.getSpec();
        player.getInventory().setItem(
                0,
                new ItemBuilder(wp.getWeaponSkin().getItem())
                        .name(ChatColor.GOLD + "Warlord's " + wp.getWeaponSkin()
                                                                .getName() + " of the " + spec.getName())
                        .lore(
                                ChatColor.GRAY + "Damage: " + ChatColor.RED + "132 " + ChatColor.GRAY + "- " + ChatColor.RED + "179",
                                ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + "25%",
                                ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + "200%",
                                "",
                                ChatColor.GREEN + spec.getClassName() + " (" + spec.getClass()
                                                                                   .getSimpleName() + "):",
                                Warlords.getPlayerSettings(player.getUniqueId())
                                        .getSkillBoostForClass().selectedDescription,
                                "",
                                ChatColor.GRAY + "Health: " + ChatColor.GREEN + "+800",
                                ChatColor.GRAY + "Max Energy: " + ChatColor.GREEN + "+35",
                                ChatColor.GRAY + "Cooldown Reduction: " + ChatColor.GREEN + "+13%",
                                ChatColor.GRAY + "Speed: " + ChatColor.GREEN + "+13%",
                                "",
                                ChatColor.GOLD + "Skill Boost Unlocked",
                                ChatColor.DARK_AQUA + "Crafted",
                                ChatColor.LIGHT_PURPLE + "Void Forged [4/4]",
                                ChatColor.GREEN + "EQUIPPED",
                                ChatColor.AQUA + "BOUND",
                                "",
                                ChatColor.YELLOW + ChatColor.BOLD.toString() + "RIGHT-CLICK " + ChatColor.GREEN + "to view " + ChatColor.YELLOW + spec.getWeapon()
                                                                                                                                                      .getName(),
                                ChatColor.GREEN + "stats!"
                        )
                        .unbreakable()
                        .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                        .get()
        );
    }

    public static void weaponRightClick(WarlordsEntity wp, Player player) {
        AbstractPlayerClass spec = wp.getSpec();
        AbstractAbility weapon = spec.getWeapon();
        player.getInventory().setItem(
                0,
                new ItemBuilder(wp.getWeaponSkin().getItem())
                        .name(ChatColor.GREEN + weapon.getName() + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Right-Click!")
                        .lore(ChatColor.GRAY + "Energy Cost: " + ChatColor.YELLOW + NumberFormat.formatOptionalHundredths(
                                      weapon.getEnergyCost()),
                              ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + NumberFormat.formatOptionalHundredths(
                                      weapon.getCritChance()) + "%",
                              ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + NumberFormat.formatOptionalHundredths(
                                      weapon.getCritMultiplier()) + "%",
                              "",
                              weapon.getDescription(),
                              "",
                              ChatColor.YELLOW + ChatColor.BOLD.toString() + "LEFT-CLICK " + ChatColor.GREEN + "to view weapon stats!"
                        )
                        .unbreakable()
                        .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE)
                        .get()
        );
    }

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(new Listener() {
            @EventHandler
            public void onInvClick(InventoryClickEvent e) {
                if (e.getSlot() == 0) {
                    Player player = (Player) e.getWhoClicked();
                    WarlordsEntity wp = Warlords.getPlayer(player);
                    if (wp != null) {
                        if (e.isLeftClick()) {
                            weaponLeftClick(wp, player);
                        } else if (e.isRightClick()) {
                            weaponRightClick(wp, player);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {
        AbstractPlayerClass playerClass = warlordsPlayer.getSpec();
        player.getInventory()
              .setItem(0, new ItemBuilder(warlordsPlayer.getWeaponSkin().getItem())
                      .name("§cWarlord's Felflame of the " + playerClass.getWeapon()
                                                                        .getName())
                      .lore(
                              "§7Damage: §c132 §7- §c179",
                              "§7Crit Chance: §c25%",
                              "§7Crit Multiplier: §c200%",
                              "",
                              "§a" + playerClass.getClassName(),
                              "§aIncreases the damage you",
                              "§adeal with " + playerClass.getWeapon()
                                                          .getName() + " by §c20%",
                              "",
                              "§7Health: §a+800",
                              "§7Max Energy: §a+35",
                              "§7Cooldown Reduction: §a+13%",
                              "§7Speed: §a+13%",
                              "",
                              "§6Skill Boost Unlocked",
                              "§3Crafted",
                              "§dVoid Forged [4/4]",
                              "§aEQUIPPED",
                              "§bBOUND"
                      )
                      .unbreakable()
                      .get());
        weaponLeftClick(warlordsPlayer, player);
    }

}
