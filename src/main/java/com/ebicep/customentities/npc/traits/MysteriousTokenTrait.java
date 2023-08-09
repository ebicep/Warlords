package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public class MysteriousTokenTrait extends WarlordsTrait {
    public MysteriousTokenTrait() {
        super("MysteriousTokenTrait");
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
            openMysteriousTokenMenu(player, databasePlayer);
        });
    }

    private static void openMysteriousTokenMenu(Player player, DatabasePlayer databasePlayer) {
        Menu menu = new Menu(ChatColor.MAGIC + "Mysterious Tokens", 9 * 6);

        Long tokens = databasePlayer.getPveStats().getCurrencyValue(Currencies.MYSTERIOUS_TOKEN);
        String name = "You have " + tokens + " Mysterious Tokens";
        boolean unblur = ThreadLocalRandom.current().nextInt(0, 100) < 5;
        if (unblur) {
            int characterToUnBlur = ThreadLocalRandom.current().nextInt(0, name.length());
            name = name.substring(0,
                    characterToUnBlur
            ) + ChatColor.BLACK + name.charAt(characterToUnBlur) + ChatColor.DARK_GRAY + ChatColor.MAGIC + name.substring(characterToUnBlur + 1);
        }

        menu.setItem(4, 2,
                new ItemBuilder(Material.BEDROCK)
                        .name(Component.text(name, NamedTextColor.DARK_GRAY, TextDecoration.OBFUSCATED))
                        .get(),
                (m, e) -> {

                }
        );

        menu.fillEmptySlots(
                new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                        .name(Component.text(" "))
                        .get(),
                (m, e) -> {
                }
        );

        menu.setItem(4, 5, Menu.MENU_CLOSE, Menu.ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

}
