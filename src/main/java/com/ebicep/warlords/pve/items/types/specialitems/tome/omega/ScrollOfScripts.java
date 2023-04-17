package com.ebicep.warlords.pve.items.types.specialitems.tome.omega;

import com.ebicep.warlords.events.player.ingame.pve.drops.WarlordsDropItemEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;

public class ScrollOfScripts extends SpecialOmegaTome implements AppliesToWarlordsPlayer {
    public ScrollOfScripts(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    public ScrollOfScripts() {

    }

    @Override
    public String getName() {
        return "Scroll of Scripts";
    }

    @Override
    public String getBonus() {
        return "For every player with more kills than you, increase your chance of finding Items by 2.5%.";
    }

    @Override
    public String getDescription() {
        return "It's not scripted, I promise.";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onEvent(WarlordsDropItemEvent event) {
                event.addModifier(0.025 * numberOfPlayersMoreKillsThan(warlordsPlayer));
            }
        });
    }

    public static int numberOfPlayersMoreKillsThan(WarlordsPlayer warlordsPlayer) {
        int playerKills = warlordsPlayer.getMinuteStats().total().getKills();
        return warlordsPlayer.getGame()
                             .warlordsPlayers()
                             .mapToInt(player -> player.getMinuteStats().total().getKills() > playerKills && !warlordsPlayer.equals(player) ? 1 : 0)
                             .sum();
    }

}
