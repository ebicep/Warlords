package com.ebicep.warlords.game.gamemodes;

import org.bukkit.inventory.ItemStack;

public class DarkDescent implements Mode {

    @Override
    public String getName() {
        return "Dark Descent";
    }

    @Override
    public String getAbbreviation() {
        return "DD";
    }

    @Override
    public ItemStack getItemStack() {
        return null;
    }

    @Override
    public boolean isHiddenInMenu() {
        return true;
    }

    @Override
    public int getMinPlayersToAddToDatabase() {
        return Integer.MAX_VALUE;
    }

}
