package com.ebicep.warlords.player.general.specboosts;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.menu.generalmenu.WarlordsNewHotbarMenu;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class SpecBoostMenu {

    public static void open(Player player) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
                    Specializations selectedSpec = databasePlayer.getLastSpec();
                    Map<Specializations, Integer> selectedBoosts = databasePlayer.getSpecBoosts();

            Menu menu = new Menu("Spec Boosts", 9 * 4);
            List<SpecBoostManager.SpecBoost<?>> specBoosts = SpecBoostManager.getSpecBoosts(selectedSpec);
                    for (int i = 0; i < specBoosts.size(); i++) {
                        SpecBoostManager.SpecBoost<?> specBoost = specBoosts.get(i);
                        boolean selected = selectedBoosts.computeIfAbsent(selectedSpec, k -> 0) == i;
                        int finalI = i;
                        ItemBuilder itemBuilder = new ItemBuilder(selectedSpec.specType.itemStack)
                                .name(specBoost.getName())
                                .lore(Component.textOfChildren(
                                        Component.text("Difficulty: ", NamedTextColor.GRAY),
                                        specBoost.getDifficulty()
                                ))
                                .addLore(Component.empty())
                                .addLore(specBoost.getDescriptionLore())
                                .glow(selected);
                        if (selected) {
                            itemBuilder.addLore(
                                    Component.empty(),
                                    Component.text("ACTIVE", NamedTextColor.GREEN)
                            );
                        }
                        if (specBoost.isDisabled()) {
                            itemBuilder.addLore(
                                    Component.empty(),
                                    Component.text("DISABLED", NamedTextColor.RED, TextDecoration.BOLD)
                            );
                        }
                        menu.setItem(i + 2, 1,
                                itemBuilder.get(),
                                (m, e) -> {
                                    if (selected) {
                                        return;
                                    }
                                    selectedBoosts.put(selectedSpec, finalI);
                                    player.sendMessage(Component.text("You have selected the ", NamedTextColor.GREEN)
                                                                .append(specBoost.getName())
                                                                .append(Component.text(" spec boost!", NamedTextColor.GREEN))
                                    );
                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                    open(player);
                                }
                        );
                    }

            menu.setItem(4, 3, WarlordsNewHotbarMenu.PvPMenu.MENU_BACK_PVP, (m, e) -> WarlordsNewHotbarMenu.PvPMenu.openPvPMenu(player));
                    menu.openForPlayer(player);
                }
        );
    }

}
