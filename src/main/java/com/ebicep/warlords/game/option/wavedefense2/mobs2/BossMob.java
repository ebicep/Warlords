package com.ebicep.warlords.game.option.wavedefense2.mobs2;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public interface BossMob extends Mob {

    @Override
    default void dropItem() {
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Dropped Boss Item");
    }
}
