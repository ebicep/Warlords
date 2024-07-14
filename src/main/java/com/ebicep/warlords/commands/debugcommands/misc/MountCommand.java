package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CommandAlias("mount")
@CommandPermission("group.administrator")
public class MountCommand extends BaseCommand {

    public static final Map<UUID, EntityType> PLAYER_MOUNT_TYPE = new HashMap<>();
    public static final Map<EntityType, Float> SPEEDS = new HashMap<>();

    static {
        SPEEDS.put(EntityType.HORSE, 1.6f);
    }

    public static float getSpeed(EntityType entityType) {
        return SPEEDS.getOrDefault(entityType, 3.2f);
    }

    @Subcommand("custom")
    @CommandCompletion("@players")
    @Description("Toggles custom mounts")
    public void custom(CommandIssuer issuer, @Flags("other") Player player, EntityType entityType) {
        PLAYER_MOUNT_TYPE.put(player.getUniqueId(), entityType);
        ChatChannels.sendDebugMessage(issuer, Component.text("Set mount type for " + player.getName() + " to " + entityType.name(), NamedTextColor.GREEN));
    }

    @Subcommand("set")
    @CommandCompletion("@players @enabledisable")
    @Description("Sets player mount type")
    public void set(CommandIssuer issuer, @Flags("other") Player player, @Values("@enabledisable") String option) {
        if (option.equals("enable")) {
            PLAYER_MOUNT_TYPE.put(player.getUniqueId(), EntityType.HORSE);
            ChatChannels.sendDebugMessage(issuer, Component.text("Set mount type for " + player.getName() + " to HORSE", NamedTextColor.GREEN));
        } else {
            PLAYER_MOUNT_TYPE.remove(player.getUniqueId());
            ChatChannels.sendDebugMessage(issuer, Component.text("Removed mount type for " + player.getName(), NamedTextColor.GREEN));
        }
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}
