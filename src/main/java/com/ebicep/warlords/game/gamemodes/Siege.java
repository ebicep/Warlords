package com.ebicep.warlords.game.gamemodes;

import com.ebicep.warlords.database.repositories.games.GamesCollections;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.siege.DatabaseGameSiege;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.option.*;
import com.ebicep.warlords.game.option.freeze.GameFreezeOption;
import com.ebicep.warlords.game.option.pvp.ApplySpecBoostsOption;
import com.ebicep.warlords.game.option.pvp.HorseOption;
import com.ebicep.warlords.game.option.respawn.NoRespawnIfOfflineOption;
import com.ebicep.warlords.game.option.respawn.RespawnProtectionOption;
import com.ebicep.warlords.game.option.respawn.RespawnWaveOption;
import com.ebicep.warlords.game.option.win.WinByPointsOption;
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

public class Siege implements Mode {

    @Override
    public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = new ArrayList<>();

        Component base = Component.text("", NamedTextColor.YELLOW, TextDecoration.BOLD);
        options.add(TextOption.Type.CHAT_CENTERED.create(
                Component.text("Warlords", NamedTextColor.WHITE, TextDecoration.BOLD),
                Component.empty(),
                base.append(Component.text("Gain a point by either")),
                base.append(Component.text("capturing the point, escorting the payload,")),
                base.append(Component.text("or defending the payload!")),
                Component.empty()
        ));
        options.add(TextOption.Type.TITLE.create(
                10,
                Component.text("GO!", NamedTextColor.GREEN),
                Component.text("Siege!", NamedTextColor.YELLOW)
        ));

        options.add(new WinByPointsOption(4));

        options.add(new GameFreezeOption());
        options.add(new NoRespawnIfOfflineOption());
        options.add(new WeaponOption());
        options.add(new ApplySpecBoostsOption(addons.contains(GameAddon.RANDOM_SPEC_BOOST)));
        options.add(new HorseOption());

        options.add(new RespawnWaveOption()); // timers handled by siegeoption
        options.add(new RespawnProtectionOption(5, 10, false));
        options.add(new GraveOption());

        options.add(new BasicScoreboardOption());

        options.add(new GlowingTeamOption());
        options.add(new SwapSpecOption());
        options.add(new PlayerCooldownDisplayOption());
        return options;
    }

    @Override
    public String getName() {
        return "Siege";
    }

    @Override
    public String getAbbreviation() {
        return "Siege";
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.SCULK);
    }

    @Override
    public TriFunction<Game, WarlordsGameTriggerWinEvent, Boolean, ? extends DatabaseGameBase> getCreateDatabaseGame() {
        return DatabaseGameSiege::new;
    }

    @Override
    public GamesCollections getGamesCollections() {
        return GamesCollections.SIEGE;
    }

    @Override
    public int getMinPlayersToAddToDatabase() {
        return 6;
    }

}

