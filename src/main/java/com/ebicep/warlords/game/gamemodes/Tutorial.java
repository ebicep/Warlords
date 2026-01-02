package com.ebicep.warlords.game.gamemodes;

import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.WeaponOption;
import com.ebicep.warlords.game.option.pve.tutorial.TutorialOption;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class Tutorial implements Mode {

    @Override
    public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = new ArrayList<>();
        options.add(new WeaponOption());
        options.add(new TutorialOption());
        return options;
    }

    @Override
    public List<String> getNamespaces() {
        return ConfigManager.DEFAULT_NAMESPACES;
    }

    @Override
    public String getName() {
        return "Tutorial";
    }

    @Override
    public String getAbbreviation() {
        return "Tutorial";
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.BOOK);
    }

    @Override
    public int getMinPlayersToAddToDatabase() {
        return Integer.MAX_VALUE;
    }


}

