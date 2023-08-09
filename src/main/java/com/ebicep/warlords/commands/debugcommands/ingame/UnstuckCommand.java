package com.ebicep.warlords.commands.debugcommands.ingame;


import co.aikar.commands.BaseCommand;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

@CommandAlias("unstuck|stuck")
public class UnstuckCommand extends BaseCommand {

    private static int UNSTUCK_COOLDOWN = 20;
    public static HashMap<UUID, Instant> STUCK_COOLDOWNS = new HashMap<>();

    @Default
    @Description("Unstuck yourself")
    public void unstuck(Player player) {
        if (Warlords.getPlayer(player) != null && Warlords.getPlayer(player).getGame().isFrozen()) {
            throw new ConditionFailedException("You cannot use this command while the game is frozen!");
        }
        if (STUCK_COOLDOWNS.containsKey(player.getUniqueId())) {
            Instant lastUsed = STUCK_COOLDOWNS.get(player.getUniqueId());
            if (lastUsed.plusSeconds(UNSTUCK_COOLDOWN).isAfter(Instant.now())) {
                throw new ConditionFailedException("You need to wait before using this command again!");
            }
        }
        STUCK_COOLDOWNS.put(player.getUniqueId(), Instant.now());
        player.teleport(player.getLocation().add(0, 1, 0));
        player.sendMessage(Component.text("You were teleported 1 block upwards.", NamedTextColor.GREEN));
        ChatChannels.sendDebugMessage(player, Component.text("Used the unstuck command.", NamedTextColor.RED));
    }

}
