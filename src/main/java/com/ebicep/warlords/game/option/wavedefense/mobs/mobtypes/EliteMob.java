package com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes;

import com.ebicep.warlords.game.option.wavedefense.mobs.Mob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public interface EliteMob extends Mob {

    @Override
    default void dropItem() {
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Dropped Elite Item");
    }
}
