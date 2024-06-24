package com.ebicep.warlords.player.general.specboosts;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class SpecBoostMenu {

    public static void open(Player player) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
            Specializations selectedSpec = databasePlayer.getLastSpec();
            Map<Specializations, Integer> selectedBoosts = databasePlayer.getSpecBoosts();

            Menu menu = new Menu("Spec Boosts", 9 * 5);
            List<SpecBoost> specBoosts = SpecBoost.getSpecBoosts(selectedSpec);
            for (int i = 0; i < specBoosts.size(); i++) {
                SpecBoost specBoost = specBoosts.get(i);
                int finalI = i;
                menu.setItem(i + 1, 1,
                        new ItemBuilder(selectedSpec.specType.itemStack)
                                .name(Component.text(specBoost.getName(), NamedTextColor.GREEN))
                                .lore(WordWrap.wrap(specBoost.getDescription(), 150))
                                .get(),
                        (m, e) -> {
                            if (selectedBoosts.computeIfAbsent(selectedSpec, k -> 0) == finalI) {
                                return;
                            }
                            selectedBoosts.put(selectedSpec, finalI);
                            player.sendMessage(Component.text("You have selected the " + specBoost.getName(), NamedTextColor.GREEN)
                                                        .append(Component.text(specBoost.getName(), NamedTextColor.AQUA))
                                                        .append(Component.text(" spec boost!", NamedTextColor.GRAY))
                            );
                        }
                );
            }

            menu.openForPlayer(player);
        });
    }

}
