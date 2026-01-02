package com.ebicep.warlords.game.gamemodes;

import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.option.*;
import com.ebicep.warlords.game.option.freeze.GameFreezeOption;
import com.ebicep.warlords.game.option.respawn.NoRespawnIfOfflineOption;
import com.ebicep.warlords.game.option.towerdefense.WinByLastStandingCastleOption;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class TowerDefense implements Mode {

    @Override
    public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = new ArrayList<>();

        Component base = Component.text("", NamedTextColor.YELLOW, TextDecoration.BOLD);
        options.add(TextOption.Type.CHAT_CENTERED.create(
                Component.text("Warlords", NamedTextColor.WHITE, TextDecoration.BOLD),
                Component.empty(),
                Component.empty()
        ));
        options.add(TextOption.Type.TITLE.create(
                10,
                Component.text("GO!", NamedTextColor.GREEN),
                Component.text("", NamedTextColor.YELLOW)
        ));

        options.add(new GameFreezeOption());
        options.add(new NoRespawnIfOfflineOption());
        options.add(new WeaponOption());
        options.add(new RecordTimeElapsedOption());

        options.add(new WinByLastStandingCastleOption());

        for (Option option : options) {
            if (option instanceof FlyOption flyOption) {
                flyOption.setFlyEnabled(true);
                break;
            }
        }

        options.add(new PlayerCooldownDisplayOption());
        return options;
    }

    @Override
    public List<String> getNamespaces() {
        return ConfigManager.TD_NAMESPACES;
    }

    @Override
    public String getName() {
        return "Tower Defense";
    }

    @Override
    public String getAbbreviation() {
        return "TD";
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.OAK_PLANKS);
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

