package com.ebicep.warlords.game.option.wavedefense2.mobs2.mobs.mobtypes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public interface BasicMob extends Mob {

    @Override
    default void dropItem() {
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Dropped Basic Item");
    }
}
