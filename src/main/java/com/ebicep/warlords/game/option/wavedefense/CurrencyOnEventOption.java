package com.ebicep.warlords.game.option.wavedefense;

import com.ebicep.warlords.events.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.WarlordsEntity;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class CurrencyOnEventOption implements Option, Listener {

    private static final int SCOREBOARD_PRIORITY = 15;
    private static final int BASE_CURRENCY_ON_KILL = 100;
    private int currencyToAdd;

    public CurrencyOnEventOption() {
        this(BASE_CURRENCY_ON_KILL);
    }

    public CurrencyOnEventOption(int currencyToAdd) {
        this.currencyToAdd = currencyToAdd;
    }

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(this);
        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(SCOREBOARD_PRIORITY, "currency") {
            @Override
            public List<String> computeLines(@Nullable WarlordsEntity player) {
                return PlayerFilter.playingGame(game)
                        .filter(e -> e instanceof WarlordsPlayer)
                        .stream()
                        .map(e -> e.getName() + ": " + ChatColor.GOLD + e.getCurrency())
                        .collect(Collectors.toList());
            }
        });
    }

    @EventHandler
    public void onKill(WarlordsDeathEvent event) {
        if (event.getKiller() != null) {
            event.getKiller().sendMessage(ChatColor.GOLD + "+100 Currency");
            event.getKiller().addCurrency(currencyToAdd);
        }
    }
}
