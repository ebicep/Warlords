package com.ebicep.warlords.game.option.wavedefense;

import com.ebicep.warlords.events.player.WarlordsDeathEvent;
import com.ebicep.warlords.events.player.pve.WarlordsPlayerAddCurrencyEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CurrencyOnEventOption implements Option, Listener {

    private static final int SCOREBOARD_PRIORITY = 15;
    private static final int BASE_CURRENCY_ON_KILL = 100;
    private int baseCurrencyToAdd;

    public CurrencyOnEventOption() {
        this(BASE_CURRENCY_ON_KILL);
    }

    public CurrencyOnEventOption(int baseCurrencyToAdd) {
        this.baseCurrencyToAdd = baseCurrencyToAdd;
    }

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(this);

        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(SCOREBOARD_PRIORITY, "currency") {
            @Override
            public List<String> computeLines(@Nullable WarlordsEntity player) {
                return Collections.singletonList(player != null ? "Insignia: " + ChatColor.GOLD + "❂ " + player.getCurrency() : "");
            }
        });
    }

    @EventHandler
    public void onKill(WarlordsDeathEvent event) {
        WarlordsEntity mob = event.getPlayer();
        for (WarlordsEntity player : PlayerFilter
                .playingGame(mob.getGame())
                .aliveEnemiesOf(mob)
        ) {
            if (player instanceof WarlordsPlayer && !player.isDead()) {
                AtomicInteger currencyToAdd = new AtomicInteger(baseCurrencyToAdd);
                Bukkit.getPluginManager().callEvent(new WarlordsPlayerAddCurrencyEvent(player, currencyToAdd));
                player.sendMessage(ChatColor.GOLD + "+" + currencyToAdd.get() + " ❂ Insignia");
                player.addCurrency(currencyToAdd.get());
            }
        }
    }
}
