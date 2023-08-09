package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@CommandAlias("sudo")
@CommandPermission("group.administrator")
public class SudoCommand extends BaseCommand {

    @Default
    public void sudo(CommandIssuer issuer, @Flags("other") Player player, String toSay) {
        ChatChannels.sendDebugMessage(issuer,
                Component.text("Sudo say ", NamedTextColor.GREEN)
                         .append(Component.text(player.getName(), NamedTextColor.AQUA))
                         .append(Component.text(" - ", NamedTextColor.GRAY))
                         .append(Component.text(toSay, NamedTextColor.WHITE))
        );
        new BukkitRunnable() {
            @Override
            public void run() {
                player.chat(toSay);
            }
        }.runTaskLaterAsynchronously(Warlords.getInstance(), 10);
    }


}
