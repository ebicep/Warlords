package com.ebicep.warlords.game.gamemodes;

import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.database.repositories.games.GamesCollections;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught.DatabaseGamePvEOnslaught;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.*;
import com.ebicep.warlords.game.option.freeze.GameFreezeOption;
import com.ebicep.warlords.game.option.pve.BountyOption;
import com.ebicep.warlords.game.option.respawn.DieOnLogoutOption;
import com.ebicep.warlords.game.option.respawn.NoRespawnIfOfflineOption;
import com.ebicep.warlords.game.option.win.WinByAllDeathOption;
import com.ebicep.warlords.menu.PlayerHotBarItemListener;
import com.ebicep.warlords.menu.generalmenu.WarlordsNewHotbarMenu;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import com.ebicep.warlords.util.java.TriFunction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class Onslaught implements Mode {

    @Override
    public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = new ArrayList<>();
        Component base = Component.text("", NamedTextColor.YELLOW, TextDecoration.BOLD);
        options.add(TextOption.Type.CHAT_CENTERED.create(
                Component.text("Warlords", NamedTextColor.WHITE, TextDecoration.BOLD),
                Component.empty(),
                base.append(Component.text("Survive as long as possible while also killing")),
                base.append(Component.text("as many monsters as possible! Every 5 minutes you will")),
                base.append(Component.text("gain special reward pouches.")),
                Component.empty()
        ));
        options.add(TextOption.Type.TITLE.create(
                10,
                Component.text("GO!", NamedTextColor.GREEN),
                Component.text("Let the onslaught begin!", NamedTextColor.YELLOW)
        ));
        options.add(new PreGameItemOption(4, PlayerHotBarItemListener.SELECTION_MENU, (g, p) -> WarlordsNewHotbarMenu.SelectionMenu.openWarlordsMenu(p)));
        options.add(new RecordTimeElapsedOption());
        options.add(new WeaponOption(WeaponOption::showPvEWeapon, WeaponOption::showWeaponStats));
        options.add(new NoRespawnIfOfflineOption());
        options.add(new WinByAllDeathOption(Team.BLUE));
        options.add(new DieOnLogoutOption());
        options.add(new GameFreezeOption());
        options.add(new BasicScoreboardOption());
        options.add(new BountyOption());
        options.add(new PlayerCooldownDisplayOption());

        return options;
    }

    @Override
    public List<String> getNamespaces() {
        return ConfigManager.PVE_NAMESPACES;
    }

    @Override
    public String getName() {
        return "Onslaught";
    }

    @Override
    public String getAbbreviation() {
        return "PVE";
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.ZOMBIE_HEAD);
    }

    @Override
    public boolean isHiddenInMenu() {
        return true;
    }

    @Override
    public TriFunction<Game, WarlordsGameTriggerWinEvent, Boolean, ? extends DatabaseGameBase> getCreateDatabaseGame() {
        return DatabaseGamePvEOnslaught::new;
    }

    @Override
    public GamesCollections getGamesCollections() {
        return GamesCollections.PVE;
    }

    @Override
    public int getMinPlayersToAddToDatabase() {
        return 1;
    }

    @Override
    public float getDropModifier() {
        return .1f;
    }

}

