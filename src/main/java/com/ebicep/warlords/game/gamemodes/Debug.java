package com.ebicep.warlords.game.gamemodes;

import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.option.*;
import com.ebicep.warlords.game.option.pvp.ApplySpecBoostsOption;
import com.ebicep.warlords.game.option.pvp.HorseOption;
import com.ebicep.warlords.game.option.pvp.ctf.FlagOption;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.List;

public class Debug implements Mode {

    @Override
    public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = Mode.super.initMap(map, loc, addons);

        options.add(TextOption.Type.TITLE.create(
                3,
                Component.text("GO!", NamedTextColor.GREEN)
        ));
        options.add(CompassOption.flagOption());
        options.add(new FlagOption());
        options.add(new WeaponOption());
        options.add(new HorseOption());
        options.add(new PlayerCooldownDisplayOption());
        options.add(new ApplySpecBoostsOption(addons.contains(GameAddon.RANDOM_SPEC_BOOST)));

        return options;
    }

    @Override
    public String getName() {
        return "Sandbox";
    }

    @Override
    public String getAbbreviation() {
        return "Sandbox";
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.SAND);
    }

    @Override
    public int getMinPlayersToAddToDatabase() {
        return Integer.MAX_VALUE;
    }

}

