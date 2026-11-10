package com.ebicep.warlords.game.gamemodes;

import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.PlayerCooldownDisplayOption;
import com.ebicep.warlords.game.option.PreGameItemOption;
import com.ebicep.warlords.game.option.RecordTimeElapsedOption;
import com.ebicep.warlords.game.option.TextOption;
import com.ebicep.warlords.game.option.WeaponOption;
import com.ebicep.warlords.game.option.freeze.GameFreezeOption;
import com.ebicep.warlords.game.option.respawn.DieOnLogoutOption;
import com.ebicep.warlords.game.option.respawn.NoRespawnIfOfflineOption;
import com.ebicep.warlords.game.option.win.WinByAllDeathOption;
import com.ebicep.warlords.menu.PlayerHotBarItemListener;
import com.ebicep.warlords.menu.generalmenu.WarlordsNewHotbarMenu;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class DarkDescent implements Mode {

    @Override
    public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = new ArrayList<>();
        options.add(TextOption.Type.CHAT_CENTERED.create(
                Component.text("Dark Descent", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                Component.empty(),
                Component.text("Descend through 20 floors and claim their chests.", NamedTextColor.YELLOW, TextDecoration.BOLD),
                Component.empty()
        ));
        options.add(TextOption.Type.TITLE.create(
                10,
                Component.text("DESCEND!", NamedTextColor.DARK_PURPLE),
                Component.text("Reach the 20th floor.", NamedTextColor.YELLOW)
        ));
        options.add(new PreGameItemOption(4, PlayerHotBarItemListener.SELECTION_MENU, (g, p) -> WarlordsNewHotbarMenu.SelectionMenu.openWarlordsMenu(p)));
        options.add(new RecordTimeElapsedOption());
        options.add(new WeaponOption(WeaponOption::showPvEWeapon, WeaponOption::showWeaponStats));
        options.add(new NoRespawnIfOfflineOption());
        options.add(new WinByAllDeathOption(Team.BLUE));
        options.add(new DieOnLogoutOption());
        options.add(new GameFreezeOption());
        options.add(new PlayerCooldownDisplayOption());
        return options;
    }

    @Override
    public List<String> getNamespaces() {
        return ConfigManager.PVE_NAMESPACES;
    }

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
        return new ItemStack(Material.SCULK_CATALYST);
    }

    @Override
    public boolean isHiddenInMenu() {
        return true;
    }

    @Override
    public int getMinPlayersToAddToDatabase() {
        return Integer.MAX_VALUE;
    }

    /**
     * Games are not stored, so floor chests are the only rewards
     */
    @Override
    public float getDropModifier() {
        return 0;
    }

}
