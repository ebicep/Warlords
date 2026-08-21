package com.ebicep.warlords.game.gamemodes;

import com.ebicep.warlords.database.repositories.games.GamesCollections;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.interception.DatabaseGameInterception;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.*;
import com.ebicep.warlords.game.option.pvp.ApplySpecBoostsOption;
import com.ebicep.warlords.game.option.pvp.GameOvertimeOption;
import com.ebicep.warlords.game.option.pvp.HorseOption;
import com.ebicep.warlords.game.option.pvp.interception.InterceptionRespawnOption;
import com.ebicep.warlords.game.option.respawn.NoRespawnIfOfflineOption;
import com.ebicep.warlords.game.option.respawn.RespawnProtectionOption;
import com.ebicep.warlords.game.option.respawn.RespawnWaveOption;
import com.ebicep.warlords.game.option.win.WinAfterTimeoutOption;
import com.ebicep.warlords.game.option.win.WinByPointsOption;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import com.ebicep.warlords.util.java.TriFunction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class Interception implements Mode {

    @Override
    public List<Option> initMap(GameMap map, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = Mode.super.initMap(map, loc, addons);
        int points = 1500;

        Component base = Component.text("", NamedTextColor.YELLOW, TextDecoration.BOLD);
        options.add(TextOption.Type.CHAT_CENTERED.create(
                Component.text("Warlords", NamedTextColor.WHITE, TextDecoration.BOLD),
                Component.empty(),
                base.append(Component.text("Capture the marked points to")),
                base.append(Component.text("earn points! The first team with a")),
                base.append(Component.text("score of "))
                    .append(Component.text(points, NamedTextColor.AQUA, TextDecoration.BOLD))
                    .append(base.append(Component.text(" wins!"))),
                Component.empty()
        ));
        options.add(TextOption.Type.TITLE.create(
                10,
                Component.text("GO!", NamedTextColor.GREEN),
                Component.text("Capture the marked points!", NamedTextColor.YELLOW)
        ));

        options.add(CompassOption.pointInformationOption());

        options.add(new NoRespawnIfOfflineOption());
        options.add(new WeaponOption());
        options.add(new ApplySpecBoostsOption(addons.contains(GameAddon.RANDOM_SPEC_BOOST)));
        options.add(new HorseOption());

        options.add(new AbstractScoreOnEventOption.OnInterceptionCapture(25));
        AbstractScoreOnEventOption.OnInterceptionTimer scoreOnEventOption = new AbstractScoreOnEventOption.OnInterceptionTimer(1);
        options.add(scoreOnEventOption);
        options.add(new WinByPointsOption(points) {
            @Override
            protected Component modifyScoreboardLine(Team team, Component component) {
                Map<Team, Integer> cachedTeamScoreIncrease = scoreOnEventOption.getCachedTeamScoreIncrease();
                Integer increase = cachedTeamScoreIncrease.get(team);
                if (increase != null) {
                    return component.append(Component.text(" +" + increase, NamedTextColor.AQUA)
                                                     .append(Component.text("/s", NamedTextColor.GOLD)));
                }
                return component;
            }
        });
        if (addons.contains(GameAddon.DOUBLE_TIME)) {
            options.add(new WinAfterTimeoutOption(2400));
        } else {
            options.add(new WinAfterTimeoutOption(1200));
        }
        options.add(new GameOvertimeOption(100, 90));
        options.add(new RespawnWaveOption(0, 17, 8));
        options.add(new RespawnProtectionOption());
        options.add(new InterceptionRespawnOption());
        options.add(new GraveOption());

        options.add(new BasicScoreboardOption());
        options.add(new PlayerCooldownDisplayOption());
        return options;
    }

    @Override
    public String getName() {
        return "Interception";
    }

    @Override
    public String getAbbreviation() {
        return "INTER";
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(Material.BEACON);
    }

    @Override
    public TriFunction<Game, WarlordsGameTriggerWinEvent, Boolean, ? extends DatabaseGameBase> getCreateDatabaseGame() {
        return DatabaseGameInterception::new;
    }

    @Override
    public GamesCollections getGamesCollections() {
        return GamesCollections.INTERCEPTION;
    }

    @Override
    public int getMinPlayersToAddToDatabase() {
        return 16;
    }

}

