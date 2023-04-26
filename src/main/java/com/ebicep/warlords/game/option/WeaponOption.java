package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;

public class WeaponOption implements Option {

    public static void showPvEWeapon(WarlordsPlayer wp, Player player) {
        AbstractWeapon weapon = wp.getWeapon();
        if (weapon == null) {
            return;
        }
        player.getInventory().setItem(0, new ItemBuilder(weapon.generateItemStack(false))
                .addLore(
                        "",
                        ChatColor.YELLOW + ChatColor.BOLD.toString() + "RIGHT-CLICK " + ChatColor.GREEN + "to view " + ChatColor.YELLOW + wp.getSpec()
                                                                                                                                            .getWeapon()
                                                                                                                                            .getName(),
                        ChatColor.GREEN + "stats!"
                )
                .get());
    }

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
                        .loreLEGACY(
                                ChatColor.GRAY + "Damage: " + ChatColor.RED + "132 " + ChatColor.GRAY + "- " + ChatColor.RED + "179",
                                ChatColor.GRAY + "Crit Chance: " + ChatColor.RED + "25%",
                                ChatColor.GRAY + "Crit Multiplier: " + ChatColor.RED + "200%",
                                "",
                                ChatColor.GREEN + spec.getClassName() + " (" + spec.getClass().getSimpleName() + "):",
                                WordWrap.wrapWithNewline(wp.getSkillBoost().selectedDescription, 150),
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
        ItemBuilder itemBuilder = new ItemBuilder(wp.getCosmeticSettings().getWeaponSkin().getItem())
                .name(Component.text(weapon.getName(), NamedTextColor.GREEN)
                               .append(Component.text(" - ", NamedTextColor.GRAY))
                               .append(Component.text(" Right-Click!", NamedTextColor.YELLOW)))
                .unbreakable()
                .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

        itemBuilder.addLore(Component.text("Energy Cost: ", NamedTextColor.GRAY)
                                     .append(Component.text(NumberFormat.formatOptionalHundredths(weapon.getEnergyCost()), NamedTextColor.GRAY)));
        itemBuilder.addLore(Component.text("Crit Chance: ", NamedTextColor.GRAY)
                                     .append(Component.text(NumberFormat.formatOptionalHundredths(weapon.getCritChance()) + "%", NamedTextColor.RED)));
        itemBuilder.addLore(Component.text("Crit Multiplier: ", NamedTextColor.GRAY)
                                     .append(Component.text(NumberFormat.formatOptionalHundredths(weapon.getCritMultiplier()) + "%", NamedTextColor.RED)));
        itemBuilder.addLore(Component.empty());
        itemBuilder.addLoreC(weapon.getDescription());
        itemBuilder.addLore(Component.empty());
        itemBuilder.addLore(Component.text("LEFT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD)
                                     .append(Component.text("to view weapon stats!", NamedTextColor.GREEN)));

        player.getInventory().setItem(0, itemBuilder.get());
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
                        if (!wp.getGame().equals(game)) {
                            return;
                        }
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
