package com.ebicep.warlords.game.gamemodes;

import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.WeaponOption;
import com.ebicep.warlords.game.option.pvp.ApplySpecBoostsOption;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.List;

public class Lobby implements Mode {

    @Override
    public List<Option> initMap(GameMap map, com.ebicep.warlords.util.bukkit.LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = Mode.super.initMap(map, loc, addons);
        options.add(new WeaponOption());
        options.add(new ApplySpecBoostsOption(addons.contains(GameAddon.RANDOM_SPEC_BOOST)));
        return options;
    }

    @Override
    public String getName() {
        return "MainLobby";
    }

    @Override
    public String getAbbreviation() {
        return "MainLobby";
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.BEDROCK);
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
