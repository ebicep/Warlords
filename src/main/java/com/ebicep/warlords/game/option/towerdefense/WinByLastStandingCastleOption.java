package com.ebicep.warlords.game.option.towerdefense;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.towerdefense.events.TowerDefenseCastleDestroyEvent;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class WinByLastStandingCastleOption implements Option, Listener {

    private final Map<Team, TowerDefenseCastle> castles = new HashMap<>();
    private Game game;

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        for (Option option : game.getOptions()) {
            if (option instanceof TowerDefenseOption towerDefenseOption) {
                castles.putAll(towerDefenseOption.getCastles());
            }
        }
        game.registerEvents(this);
    }

    @EventHandler
    public void onCastleDestroy(TowerDefenseCastleDestroyEvent event) {
        TowerDefenseCastle destroyedCastle = event.getCastle();
        Team team = destroyedCastle.getTeam();
        TowerDefenseCastle castle = castles.get(team);
        if (castle != destroyedCastle) {
            ChatUtils.MessageType.TOWER_DEFENSE.sendErrorMessage("Destroyed castle does not match the castle in the map");
            return;
        }
        castles.remove(team);
        if (castles.size() == 1) {
            Team winner = castles.keySet().iterator().next();
            Bukkit.getPluginManager().callEvent(new WarlordsGameTriggerWinEvent(game, this, winner));
        }
    }

}
