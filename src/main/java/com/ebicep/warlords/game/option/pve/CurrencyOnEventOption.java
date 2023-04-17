package com.ebicep.warlords.game.option.pve;

import com.ebicep.warlords.events.game.pve.WarlordsGameWaveClearEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CurrencyOnEventOption implements Option, Listener {

    public static final int SCOREBOARD_PRIORITY = 15;
    private final ConcurrentHashMap<Integer, Integer> currencyPerXWaveClear = new ConcurrentHashMap<>() {{
        put(5, 4000);
        put(1, 1000);
    }};
    private int currencyOnKill = 100;
    private int startingCurrency = 0;
    private boolean scaleWithPlayerCount = false;

    public CurrencyOnEventOption() {
    }

    public CurrencyOnEventOption onKill(int currencyOnKill) {
        this.currencyOnKill = currencyOnKill;
        return this;
    }

    public CurrencyOnEventOption onKill(int currencyOnKill, boolean scaleWithPlayerCount) {
        this.currencyOnKill = currencyOnKill;
        this.scaleWithPlayerCount = scaleWithPlayerCount;
        return this;
    }

    public CurrencyOnEventOption startWith(int startingCurrency) {
        this.startingCurrency = startingCurrency;
        return this;
    }

    public CurrencyOnEventOption onPerWaveClear(int wave, int currency) {
        currencyPerXWaveClear.put(wave, currency);
        return this;
    }

    public CurrencyOnEventOption setPerWaveClear(int wave, int currency) {
        currencyPerXWaveClear.clear();
        currencyPerXWaveClear.put(wave, currency);
        return this;
    }

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(this);

        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(SCOREBOARD_PRIORITY, "currency") {
            @Nonnull
            @Override
            public List<String> computeLines(@Nullable WarlordsPlayer player) {
                return Collections.singletonList(player != null ? "Insignia: " + ChatColor.GOLD + "❂ " + NumberFormat.addCommas(player.getCurrency()) : "");
            }
        });
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (startingCurrency == 0) {
            return;
        }
        player.addCurrency(startingCurrency);
    }

    @EventHandler
    public void onKill(WarlordsDeathEvent event) {
        if (currencyOnKill == 0) {
            return;
        }

        WarlordsEntity mob = event.getWarlordsEntity();
        for (WarlordsEntity player : PlayerFilter
                .playingGame(mob.getGame())
                .aliveEnemiesOf(mob)
        ) {
            if (player instanceof WarlordsPlayer && !player.isDead() && !mob.getName().equals("Tormented Soul")) {
                int playerCount = (int) player.getGame().warlordsPlayers().count();
                if (scaleWithPlayerCount && playerCount > 2) {
                    int finalCurrency = currencyOnKill - (20 * playerCount);
                    player.addCurrency(finalCurrency);
                } else {
                    player.addCurrency(currencyOnKill);
                }
            }
        }
    }

    @EventHandler
    public void onWaveClear(WarlordsGameWaveClearEvent event) {
        if (currencyPerXWaveClear.isEmpty()) {
            return;
        }
        int waveCleared = event.getWaveCleared();
        currencyPerXWaveClear
                .keySet()
                .stream()
                .filter(integer -> waveCleared % integer == 0)
                .max(Comparator.naturalOrder())
                .ifPresent(wave -> event.getGame().forEachOnlineWarlordsPlayer(warlordsPlayer -> warlordsPlayer.addCurrency(currencyPerXWaveClear.get(wave))));
    }
}
