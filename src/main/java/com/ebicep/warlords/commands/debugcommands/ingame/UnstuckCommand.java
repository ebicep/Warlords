package com.ebicep.warlords.commands.debugcommands.ingame;


import co.aikar.commands.BaseCommand;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.miscellaneouscommands.ChatCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

@CommandAlias("unstuck")
public class UnstuckCommand extends BaseCommand {

    private static int UNSTUCK_COOLDOWN = 2;
    private static HashMap<UUID, Instant> STUCK_COOLDOWNS = new HashMap<>();

    @Default
    @Description("Unstuck yourself")
    public void unstuck(Player player) {
        if (Warlords.getPlayer(player) != null && Warlords.getPlayer(player).getGame().isFrozen()) {
            throw new ConditionFailedException(ChatColor.RED + "You cannot use this command while the game is frozen!");
        }
        if (STUCK_COOLDOWNS.containsKey(player.getUniqueId())) {
            Instant lastUsed = STUCK_COOLDOWNS.get(player.getUniqueId());
            if (lastUsed.plusSeconds(UNSTUCK_COOLDOWN).isAfter(Instant.now())) {
                throw new ConditionFailedException(ChatColor.RED + "You need to wait before using this command again!");
            }
        }
        STUCK_COOLDOWNS.put(player.getUniqueId(), Instant.now());
        player.teleport(player.getLocation().add(0, 1, 0));
        player.sendMessage(ChatColor.GREEN + "You were teleported 1 block upwards.");
        ChatCommand.sendDebugMessage(player, ChatColor.RED + "Used the unstuck command.");
    }

}
