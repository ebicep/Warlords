package com.ebicep.warlords.pve.mobs;

import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import static com.ebicep.warlords.menu.Menu.*;

public class MobMenu {

    public static void openMobMenu(Player player) {
        Menu menu = new Menu("Mobs", 9 * 4);

        Mob.MobGroup[] values = Mob.MobGroup.VALUES;
        for (int i = 0; i < values.length; i++) {
            Mob.MobGroup mobGroup = values[i];
            if (mobGroup == Mob.MobGroup.ALL) {
                continue;
            }
            menu.setItem(i + 1, 1,
                    new ItemBuilder(mobGroup.head)
                            .name(Component.text(mobGroup.name + " Mobs", mobGroup.textColor))
                            .get(),
                    (m, e) -> {
                        openMobGroupMenu(player, mobGroup);
                    }
            );
        }

        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openMobGroupMenu(Player player, Mob.MobGroup mobGroup) {
        Menu menu = new Menu(mobGroup.name + " Mobs", 9 * 6);

        for (int i = 0; i < mobGroup.mobs.length; i++) {
            Mob mob = mobGroup.mobs[i];
            String name = mob.name;
            if (name == null) {
                name = mob.name();
            }
            menu.setItem(i % 7 + 1, i / 7 + 1,
                    new ItemBuilder(mob.getHead())
                            .name(Component.text(name, NamedTextColor.GREEN))
                            .lore(
                                    Component.text("Health: ", NamedTextColor.GRAY)
                                             .append(Component.text(NumberFormat.addCommaAndRound(mob.maxHealth), NamedTextColor.GREEN)),
                                    Component.text("Walk Speed: ", NamedTextColor.GRAY)
                                             .append(Component.text(NumberFormat.formatOptionalHundredths(mob.walkSpeed), NamedTextColor.GREEN)),
                                    Component.text("Damage Resistance: ", NamedTextColor.GRAY)
                                             .append(Component.text(mob.damageResistance, NamedTextColor.GREEN)),
                                    Component.text("Melee Damage: ", NamedTextColor.GRAY)
                                             .append(Component.text(NumberFormat.addCommaAndRound(mob.minMeleeDamage) + " - " + NumberFormat.addCommaAndRound(mob.maxMeleeDamage),
                                                     NamedTextColor.GREEN
                                             ))
                            )
                            .get(),
                    (m, e) -> {
                    }
            );
        }

        menu.setItem(4, 5, MENU_BACK, (m, e) -> openMobMenu(player));
        menu.openForPlayer(player);
    }

}
