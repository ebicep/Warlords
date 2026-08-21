package com.ebicep.warlords.game.option.pve.raid;

import com.ebicep.warlords.commands.debugcommands.game.GameStartCommand;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.ebicep.warlords.menu.Menu.*;

public class RaidMenu {

    public static void openRaidMenu(Player player, Raid raid) {
        Menu menu = new Menu("Raid Menu - " + raid.getName(), 9 * 5);

        menu.setItem(4,
                0,
                new ItemBuilder(Material.CRYING_OBSIDIAN)
                        .name(Component.text("Conditions", NamedTextColor.LIGHT_PURPLE))
                        .addLore(WordWrap.wrap(Component.text("- No respawns in between phases\n\n- All players take 5% of their max health as damage every 5 seconds.\n\n- Having more than 2 of a specialization type will result in a permanent 90% damage de-buff for all players.", NamedTextColor.GRAY), 150))
                        .get(),
                ACTION_DO_NOTHING);

        List<Component> normalRewards = new ArrayList<>();
        for (Spendable spendable : raid.getNormalRewards().keySet()) {
            normalRewards.add(spendable.getCostColoredName(raid.getNormalRewards().get(spendable)));
        }
        menu.setItem(
                3,
                2,
                new ItemBuilder(Material.TRIAL_KEY)
                        .name(Component.text("Normal", NamedTextColor.GOLD, TextDecoration.BOLD))
                        .lore(WordWrap.wrap(Component.text(raid.getDescription(), NamedTextColor.DARK_GRAY, TextDecoration.ITALIC), 165))
                        .addLore(List.of(
                                Component.empty(),
                                Component.text("Recommended party size: ", NamedTextColor.GRAY)
                                        .append(Component.text("4-8 Players", NamedTextColor.YELLOW)),
                                Component.empty(),
                                Component.text("All players must have a ", NamedTextColor.GRAY)
                                        .append(Component.text("Legendary ", NamedTextColor.GOLD)),
                                Component.text("Weapon", NamedTextColor.GOLD)
                                        .append(Component.text(" or higher equipped.", NamedTextColor.GRAY))
                        ))
                        .addLore(List.of(
                                Component.empty(),
                                Component.text("Completion rewards:")
                        ))
                        .addLore(normalRewards)
                        .get(),
                (m, e) -> {
                    GameStartCommand.startGamePvERaid(player, queueEntryBuilder ->
                            queueEntryBuilder.setMap(GameMap.RAID_ONE)
                                             .setRequestedGameAddons(GameAddon.PRIVATE_GAME)

                    );
                }
        );

        List<Component> oblivionRewards = new ArrayList<>();
        for (Spendable spendable : raid.getOblivionRewards().keySet()) {
            oblivionRewards.add(spendable.getCostColoredName(raid.getOblivionRewards().get(spendable)));
        }
        menu.setItem(
                5,
                2,
                new ItemBuilder(Material.OMINOUS_TRIAL_KEY)
                        .name(Component.text("Oblivion", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD))
                        .lore(WordWrap.wrap(Component.text("Are you prepared to face your ultimate challenge?", NamedTextColor.DARK_GRAY, TextDecoration.ITALIC), 165))
                        .addLore(List.of(
                                Component.empty(),
                                Component.text("Recommended party size: ", NamedTextColor.GRAY)
                                        .append(Component.text("4-8 Players", NamedTextColor.YELLOW)),
                                Component.empty(),
                                Component.text("All players must have an ", NamedTextColor.GRAY)
                                        .append(Component.text("Ascendant ", NamedTextColor.RED)),
                                Component.text("Weapon", NamedTextColor.RED)
                                        .append(Component.text(" or higher equipped.", NamedTextColor.GRAY))
                        ))
                        .addLore(List.of(
                                Component.empty(),
                                Component.text("Completion rewards:")
                        ))
                        .addLore(oblivionRewards)
                        .get(),
                (m, e) -> {
                    GameStartCommand.startGamePvERaid(player, queueEntryBuilder ->
                            queueEntryBuilder.setMap(GameMap.RAID_ONE)
                                    .setRequestedGameAddons(GameAddon.PRIVATE_GAME)

                    );
                }
        );
        menu.setItem(4, 4, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }
}
