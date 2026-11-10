package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.marker.GameMarker;
import com.ebicep.warlords.game.option.marker.WeaponDisplayMarker;
import com.ebicep.warlords.player.general.AbstractPlayerClass;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class WeaponOption implements Option {

    public static void showPvEWeapon(WarlordsPlayer wp, Player player) {
        AbstractWeapon weapon = wp.getWeapon();
        if (weapon == null || weapon.getSelectedWeaponSkin() == null) {
            return;
        }
        player.getInventory().setItem(0, new ItemBuilder(weapon.generateItemStack(false))
                .addLore(
                        Component.empty(),
                        Component.textOfChildren(
                                Component.text("RIGHT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                Component.text("to view ", NamedTextColor.GREEN),
                                Component.text(wp.getSpec().getWeapon().getName(), NamedTextColor.YELLOW)
                        ),
                        Component.text("stats!", NamedTextColor.GREEN)
                )
                .get()
        );
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

    public static <T extends GameMarker> void showMaxWeapon(WarlordsPlayer wp, Player player) {
        AbstractPlayerClass spec = wp.getSpec();
        Weapons weaponSkin = wp.getCosmeticSettings().getWeaponSkin();
        if (weaponSkin == null) {
            return;
        }
        List<Component> description = new ArrayList<>();
        for (WeaponDisplayMarker weaponDisplayMarker : wp.getGame()
                                                         .getMarkers(WeaponDisplayMarker.class)
                                                         .stream()
                                                         .sorted((o1, o2) -> {
                                                             if (o1.weaponDisplayPriority() == o2.weaponDisplayPriority()) {
                                                                 return 0;
                                                             }
                                                             return o1.weaponDisplayPriority() > o2.weaponDisplayPriority() ? -1 : 1;
                                                         })
                                                         .toList()
        ) {
            @Nullable List<Component> component = weaponDisplayMarker.leftClickDescription(wp, player);
            if (component != null) {
                description = component;
            }
        }
        player.getInventory().setItem(
                0,
                new ItemBuilder(weaponSkin.getItem())
                        .name(Component.text("Warlord's " + weaponSkin.getName() + " of the " + spec.getName(),
                                NamedTextColor.GOLD
                        ))
                        .lore(
                                Component.text("Damage: ", NamedTextColor.GRAY)
                                         .append(Component.text("132 ", NamedTextColor.RED))
                                         .append(Component.text("-"))
                                         .append(Component.text(" 179", NamedTextColor.RED)),
                                Component.text("Crit Chance: ", NamedTextColor.GRAY)
                                         .append(Component.text("25%", NamedTextColor.RED)),
                                Component.text("Crit Multiplier: ", NamedTextColor.GRAY)
                                         .append(Component.text("200%", NamedTextColor.RED)),
                                Component.text("")
                        )
                        .addLore(description)
                        .addLore(
                                Component.text(""),
                                Component.textOfChildren(
                                        Component.text("RIGHT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                        Component.text("to view ", NamedTextColor.GREEN),
                                        Component.text(wp.getSpec().getWeapon().getName(), NamedTextColor.YELLOW)
                                ),
                                Component.text("stats!", NamedTextColor.GREEN)
                        )
                        .unbreakable()
                        .get()
        );
    }

    public static void showWeaponStats(WarlordsPlayer wp, Player player) {
        AbstractPlayerClass spec = wp.getSpec();
        AbstractAbility weapon = spec.getWeapon();
        Weapons weaponSkin = wp.getCosmeticSettings().getWeaponSkin();
        if (weaponSkin == null) {
            return;
        }
        ItemBuilder itemBuilder = new ItemBuilder(weapon.getItem(weaponSkin.getItem()))
                .name(Component.text(weapon.getName(), NamedTextColor.GREEN)
                               .append(Component.text(" - ", NamedTextColor.GRAY))
                               .append(Component.text("Right-Click!", NamedTextColor.YELLOW)))
                .unbreakable();

        itemBuilder.addLore(Component.empty());
        itemBuilder.addLore(Component.textOfChildren(
                        Component.text("LEFT-CLICK ", NamedTextColor.YELLOW, TextDecoration.BOLD),
                        Component.text("to view weapon stats!", NamedTextColor.GREEN)
                )
        );

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
        new GameRunnable(warlordsPlayer.getGame()) {

            @Override
            public void run() {
                rightClick.accept(warlordsPlayer, player);
            }
        }.runTaskLater(1);
    }

    @Override
    public void onSpecChange(@Nonnull WarlordsEntity player, Specializations oldSpec) {
        if (player instanceof WarlordsPlayer && player.getEntity() instanceof Player) {
            new GameRunnable(player.getGame()) {

                @Override
                public void run() {
                    rightClick.accept((WarlordsPlayer) player, (Player) player.getEntity());
                }
            }.runTaskLater(1);
        }
    }

}
