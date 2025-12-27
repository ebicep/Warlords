package com.ebicep.warlords.pve.newitems;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.newitems.setbonus.NewItemsSetBonus;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.Comparator;

@CommandAlias("newitems")
@CommandPermission("group.administrator")
public class NewItemsCommand extends BaseCommand {

    @Default
    @Subcommand("menu")
    public void menu(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);

    }

    @CommandAlias("generate")
    public static class GenerateItem extends BaseCommand {

        @Subcommand("random")
        public void generate(Player player, @Default("1") @Conditions("limits:min=1,max=10") Integer amount) {
            for (int i = 0; i < amount; i++) {
                NewItem item = NewItemsUtils.generateRandomItem();
                sendItemGenerateMessage(player, item);
            }
        }

        private static void sendItemGenerateMessage(Player player, NewItem item) {
            player.getInventory().addItem(item.getItemBuilder().get());
            ChatChannels.playerSendMessage(player, ChatChannels.DEBUG,
                    Component.text("Generated new item: ", NamedTextColor.GRAY)
                             .append(item.getName().hoverEvent(item.getItemBuilder().get().asHoverEvent()))
            );
        }

        @Subcommand("tier")
        public void generate(Player player, NewItemTier tier, @Default("1") @Conditions("limits:min=1,max=10") Integer amount) {
            for (int i = 0; i < amount; i++) {
                NewItem item = NewItemsUtils.generateRandomItem(tier);
                sendItemGenerateMessage(player, item);
            }
        }

        @Subcommand("set")
        public void generate(Player player, NewItemsSetBonus setBonus, @Default("1") @Conditions("limits:min=1,max=10") Integer amount) {
            for (int i = 0; i < amount; i++) {
                NewItem item = new NewItem(setBonus);
                sendItemGenerateMessage(player, item);
            }
        }

    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}
