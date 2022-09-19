package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

public class WeaponOption implements Option {

    private final BiConsumer<WarlordsPlayer, Player> leftClick;
    private final BiConsumer<WarlordsPlayer, Player> rightClick;

    public WeaponOption() {
        this(WeaponOption::showMaxWeapon, WeaponOption::showWeaponStats);
    }

    public WeaponOption(BiConsumer<WarlordsPlayer, Player> leftClick, BiConsumer<WarlordsPlayer, Player> rightClick) {
        this.leftClick = leftClick;
        this.rightClick = rightClick;
    }

    public static void showMaxWeapon(WarlordsPlayer wp, Player player) {
        AbstractPlayerClass spec = wp.getSpec();
        player.getInventory().setItem(
                0,
                new ItemBuilder(wp.getCosmeticSettings().getWeaponSkin().getItem())
                        .name(ChatColor.GOLD + "Warlord's " + wp.getCosmeticSettings().getWeaponSkin()
                                .getName() + " of the " + spec.getName())
                        .lore(
                                ChatColor.GRAY + "Damage: " + ChatColor.RED + "132 " + ChatColor.GRAY + "- " + ChatColor.RED + "179",
                                ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + "25%",
                                ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + "200%",
                                "",
                                ChatColor.GREEN + spec.getClassName() + " (" + spec.getClass().getSimpleName() + "):",
                                wp.getSkillBoost().selectedDescription,
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

    public static void showWeaponStats(WarlordsPlayer wp, Player player) {
        AbstractPlayerClass spec = wp.getSpec();
        AbstractAbility weapon = spec.getWeapon();
        player.getInventory().setItem(
                0,
                new ItemBuilder(wp.getCosmeticSettings().getWeaponSkin().getItem())
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

    public static void showPvEWeapon(WarlordsPlayer wp, Player player) {
        AbstractWeapon weapon = wp.getAbstractWeapon();
        if (weapon == null) {
            return;
        }
        player.getInventory().setItem(0, new ItemBuilder(weapon.generateItemStack())
                .addLore(
                        "",
                        ChatColor.YELLOW + ChatColor.BOLD.toString() + "RIGHT-CLICK " + ChatColor.GREEN + "to view " + ChatColor.YELLOW + wp.getSpec()
                                .getWeapon()
                                .getName(),
                        ChatColor.GREEN + "stats!"
                )
                .get());
    }

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(new Listener() {
            @EventHandler
            public void onInvClick(InventoryClickEvent e) {
                if (e.getSlot() == 0) {
                    Player player = (Player) e.getWhoClicked();
                    WarlordsEntity wp = Warlords.getPlayer(player);
                    if (wp instanceof WarlordsPlayer) {
                        if (e.isLeftClick()) {
                            leftClick.accept((WarlordsPlayer) wp, player);
                        } else if (e.isRightClick()) {
                            rightClick.accept((WarlordsPlayer) wp, player);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (player instanceof WarlordsPlayer && player.getEntity() instanceof Player) {
            leftClick.accept((WarlordsPlayer) player, (Player) player.getEntity());
        }
    }

    @Override
    public void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {
        leftClick.accept(warlordsPlayer, player);
    }

    @Override
    public void onSpecChange(@Nonnull WarlordsEntity player) {
        if (player instanceof WarlordsPlayer && player.getEntity() instanceof Player) {
            leftClick.accept((WarlordsPlayer) player, (Player) player.getEntity());
        }
    }
}
