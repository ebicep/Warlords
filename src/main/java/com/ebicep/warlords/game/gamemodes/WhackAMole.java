package com.ebicep.warlords.game.gamemodes;

import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.WeaponOption;
import com.ebicep.warlords.game.option.win.WinAfterTimeoutOption;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class WhackAMole implements Mode {

    @Override
    public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = new ArrayList<>();
        options.add(new WinAfterTimeoutOption(60, Team.RED));
        options.add(new WeaponOption());
        return options;
    }

    @Override
    public String getName() {
        return "Wackamole";
    }

    @Override
    public String getAbbreviation() {
        return "Wackamole";
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

