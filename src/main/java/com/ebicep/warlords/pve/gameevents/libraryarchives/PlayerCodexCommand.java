package com.ebicep.warlords.pve.gameevents.libraryarchives;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.events.pojos.GameEvents;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.modes.libraryarchives.DatabasePlayerPvEEventLibraryArchivesDifficultyStats;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.Comparator;

@CommandAlias("playercodex")
@CommandPermission("group.administrator")
public class PlayerCodexCommand extends BaseCommand {

    @Subcommand("give")
    @Description("Gives player codex")
    public void add(CommandIssuer issuer, PlayerCodex playerCodex, @Flags("other") Player player) {
        if (!DatabaseGameEvent.eventIsActive()) {
            ChatChannels.playerSendMessage(player,
                    ChatChannels.DEBUG,
                    Component.text("There is no active event", NamedTextColor.RED)
            );
            return;
        }
        DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
        GameEvents event = currentGameEvent.getEvent();
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            EventMode eventMode = event.eventsStatsFunction.apply(databasePlayer.getPveStats().getEventStats()).get(currentGameEvent.getStartDateSecond());
            if (eventMode == null) {
                ChatChannels.playerSendMessage(player,
                        ChatChannels.DEBUG,
                        Component.text("Player has not played this event", NamedTextColor.RED)
                );
                return;
            }
            if (!(eventMode instanceof DatabasePlayerPvEEventLibraryArchivesDifficultyStats stats)) {
                ChatChannels.playerSendMessage(player,
                        ChatChannels.DEBUG,
                        Component.text("EventMode not DatabasePlayerPvEEventLibraryArchivesDifficultyStats", NamedTextColor.RED)
                );
                return;
            }
            stats.getCodexesEarned().put(playerCodex, stats.getCodexesEarned().getOrDefault(playerCodex, 0) + 1);
            ChatChannels.playerSendMessage(player,
                    ChatChannels.DEBUG,
                    Component.text("Gave ", NamedTextColor.GREEN)
                             .append(Permissions.getPrefixWithColor(player, true))
                             .append(Component.text(" " + playerCodex.name, NamedTextColor.YELLOW))
            );
        });
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}
