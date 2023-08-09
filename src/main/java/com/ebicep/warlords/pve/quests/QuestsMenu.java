package com.ebicep.warlords.pve.quests;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class QuestsMenu {

    public static void openQuestMenu(Player player) {
        if (!QuestCommand.isQuestsEnabled) {
            player.sendMessage(Component.text("Quests are currently disabled.", NamedTextColor.RED));
            return;
        }
        Menu menu = new Menu("Quests", 9 * 6);

        AtomicInteger row = new AtomicInteger(0);
        AtomicInteger col = new AtomicInteger(1);
        Quests previousQuest = Quests.VALUES[0];
        for (Quests quest : Quests.VALUES) {
            if (quest.time == null) {
                continue;
            }
            if (quest.expireOn != null && quest.expireOn.isBefore(Instant.now())) {
                continue;
            }
            if (previousQuest.time != quest.time || quest == Quests.VALUES[0]) {
                row.getAndIncrement();
                col.set(1);
                menu.setItem(col.get(), row.get(),
                        new ItemBuilder(Material.WRITABLE_BOOK)
                                .name(Component.text((quest.expireOn != null ? "Limited" : quest.time.name) + " Quests", NamedTextColor.GREEN))
                                .get(),
                        (m, e) -> {
                        }
                );
                col.incrementAndGet();
            }
            DatabaseManager.getPlayer(player.getUniqueId(), quest.time, databasePlayer -> {
                menu.setItem(col.get(), row.get(),
                        quest.getItemStack(databasePlayer, databasePlayer.getPveStats().getQuestsCompleted().containsKey(quest)),
                        (m, e) -> {
                        }
                );
                col.getAndIncrement();
                if (row.get() > 7) {
                    row.incrementAndGet();
                    col.set(2);
                }
            }, () -> {
                menu.setItem(col.get(), row.get(),
                        quest.getItemStack(null, false),
                        (m, e) -> {
                        }
                );
                col.getAndIncrement();
                if (row.get() > 7) {
                    row.incrementAndGet();
                    col.set(2);
                }
            });
            previousQuest = quest;
        }

        menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

}
