package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.game.GameManager;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@CommandAlias("admin")
@CommandPermission("group.administrator")
public class AdminCommand extends BaseCommand {

    public static final Set<DatabasePlayerPvE> BYPASSED_PLAYER_CURRENCIES = new HashSet<>();
    public static final Set<UUID> BYPASS_INTERACT_CANCEL = new HashSet<>();
    public static boolean DISABLE_RESTART_CHECK = false;
    public static boolean DISABLE_SPECTATOR_MESSAGES = false;
    public static boolean NEW_SWIMMING = false;

    @Subcommand("bypasscurrencies")
    @Description("Bypasses player pve currency costs - Prevents any from being added")
    public void bypassCurrencies(Player player) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
            if (BYPASSED_PLAYER_CURRENCIES.contains(databasePlayer.getPveStats())) {
                BYPASSED_PLAYER_CURRENCIES.remove(databasePlayer.getPveStats());
                ChatChannels.sendDebugMessage(player, Component.text("Disabled Bypassing Currencies", NamedTextColor.GREEN));
            } else {
                BYPASSED_PLAYER_CURRENCIES.add(databasePlayer.getPveStats());
                ChatChannels.sendDebugMessage(player, Component.text("Enabled Bypassing Currencies", NamedTextColor.GREEN));
            }
        });
    }

    @Subcommand("disablegames")
    @Description("Prevents games from being started")
    public void disableGames(CommandIssuer issuer) {
        GameManager.gameStartingDisabled = !GameManager.gameStartingDisabled;
        ChatChannels.sendDebugMessage(issuer, Component.text("Disabled Games = " + GameManager.gameStartingDisabled, NamedTextColor.GREEN));
    }

    @Subcommand("disablerestartcheck")
    @Description("Removes restart check that prevents games from being started")
    public void disableRestartCheck(CommandIssuer issuer) {
        DISABLE_RESTART_CHECK = !DISABLE_RESTART_CHECK;
        ChatChannels.sendDebugMessage(issuer, Component.text("Restart Check = " + DISABLE_RESTART_CHECK, NamedTextColor.GREEN));
    }

    @Subcommand("removenearbyentities")
    @Description("Removes all nearby entities in range")
    public void removeEntitiesNearBy(Player player, @Conditions("limits:min=1,max=20") Integer range) {
        player.getWorld()
              .getNearbyEntities(player.getLocation(), range, range, range)
              .stream()
              .filter(entity -> !(entity instanceof Player))
              .forEach(Entity::remove);
    }

    @Subcommand("banspec")
    @Description("Bans a specialization from being used")
    public void banSpec(Player player, Specializations spec) {
        spec.setBanned(true);
        ChatChannels.sendDebugMessage(player, Component.text("Banned " + spec.name, NamedTextColor.GREEN));
    }

    @Subcommand("unbanspec")
    @Description("Unbans a specialization from being used")
    public void unbanSpec(Player player, Specializations spec) {
        spec.setBanned(false);
        ChatChannels.sendDebugMessage(player, Component.text("Unbanned " + spec.name, NamedTextColor.GREEN));
    }

    @Subcommand("disablespectatormessages")
    @Description("Disables spectator messages")
    public void disableSpectatorMessages(CommandIssuer issuer) {
        DISABLE_SPECTATOR_MESSAGES = !DISABLE_SPECTATOR_MESSAGES;
        ChatChannels.sendDebugMessage(issuer, Component.text("Disable Spectator Messages = " + DISABLE_SPECTATOR_MESSAGES, NamedTextColor.GREEN));
    }

    @Subcommand("togglemessages")
    @Description("Toggles MessageType messages")
    public void togglePlayerServiceMessages(CommandIssuer issuer, ChatUtils.MessageType messageType) {
        messageType.setEnabled(!messageType.isEnabled());
        ChatChannels.sendDebugMessage(issuer,
                Component.text((messageType.isEnabled() ? "Enabled" : "Disabled") + " PlayerService messages.", messageType.isEnabled() ? NamedTextColor.GREEN : NamedTextColor.RED)
        );
    }

    @Subcommand("removehorse")
    public void removeHorse(Player player, @Flags("other") Player target) {
        ChatChannels.sendDebugMessage(player,
                Component.text(target.getName(), NamedTextColor.AQUA).append(Component.text(" - Mount = " + target.getVehicle(), NamedTextColor.GREEN))
        );
        ChatChannels.sendDebugMessage(player,
                Component.text(target.getName(), NamedTextColor.AQUA).append(Component.text(" - Leave Mount = " + target.leaveVehicle(), NamedTextColor.GREEN))
        );
        ChatChannels.sendDebugMessage(player,
                Component.text(target.getName(), NamedTextColor.AQUA).append(Component.text(" - Mount = " + target.getVehicle(), NamedTextColor.GREEN))
        );
    }

    @Subcommand("toggleswimming")
    @Description("Toggles swimming")
    public void toggleSwimming(CommandIssuer issuer) {
        NEW_SWIMMING = !NEW_SWIMMING;
        ChatChannels.sendDebugMessage(issuer, Component.text("New Swimming = " + NEW_SWIMMING, NamedTextColor.GREEN));
    }

    @Subcommand("bypassinteract")
    @Description("Bypasses interact cancel")
    public void bypassInteract(CommandIssuer issuer) {
        if (issuer.isPlayer()) {
            Player player = issuer.getIssuer();
            if (BYPASS_INTERACT_CANCEL.contains(player.getUniqueId())) {
                BYPASS_INTERACT_CANCEL.remove(player.getUniqueId());
                ChatChannels.sendDebugMessage(player, Component.text("Disabled Bypassing Interact Cancel", NamedTextColor.GREEN));
            } else {
                BYPASS_INTERACT_CANCEL.add(player.getUniqueId());
                ChatChannels.sendDebugMessage(player, Component.text("Enabled Bypassing Interact Cancel", NamedTextColor.GREEN));
            }
        }
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}