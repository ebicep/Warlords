package com.ebicep.warlords.game.option.pve;

import com.ebicep.warlords.events.EventFlags;
import com.ebicep.warlords.events.game.pve.WarlordsGameWaveClearEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsAddCurrencyEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.flags.DynamicFlags;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class CurrencyOnEventOption implements Option, Listener {

    public static final int SCOREBOARD_PRIORITY = 15;
    private final ConcurrentHashMap<Integer, Integer> currencyPerXWaveClear = new ConcurrentHashMap<>() {{
        put(5, 4000);
        put(1, 1000);
    }};
    private final HashMap<Mob, Integer> perMobKill = new HashMap<>();
    private int currencyOnKill = 100;
    private int startingCurrency = 0;
    private boolean scaleWithPlayerCount = false;
    private boolean disableGuildBonus = false;
    private Function<WarlordsEntity, Component> currentCurrencyDisplay = warlordsEntity ->
            Component.text("Insignia: ").append(Component.text("❂ " + NumberFormat.addCommas(warlordsEntity.getCurrency()), NamedTextColor.GOLD));
    private CurrencyRate currencyRate;
    private int ticksElapsed = 0;

    public static class CurrencyRate {
        private int period = -1;
        private Function<WarlordsEntity, Float> playerRate = null;
        private Function<WarlordsEntity, Component> currencyRateDisplay = null;
        private Function<Integer, Component> nextCurrencyDisplay = null;

        public CurrencyRate() {
        }

        public CurrencyRate(
                int period,
                Function<WarlordsEntity, Float> playerRate,
                Function<WarlordsEntity, Component> currencyRateDisplay,
                Function<Integer, Component> nextCurrencyDisplay
        ) {
            this.period = period;
            this.playerRate = playerRate;
            this.currencyRateDisplay = currencyRateDisplay;
            this.nextCurrencyDisplay = nextCurrencyDisplay;
        }
    }

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

    public CurrencyOnEventOption onPerMobKill(Mob mob, int points) {
        perMobKill.put(mob, points);
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

    public CurrencyOnEventOption disableGuildBonus() {
        this.disableGuildBonus = true;
        return this;
    }

    public CurrencyOnEventOption setCurrentCurrencyDisplay(Function<WarlordsEntity, Component> currentCurrencyDisplay) {
        this.currentCurrencyDisplay = currentCurrencyDisplay;
        return this;
    }

    public CurrencyOnEventOption setCurrencyRate(CurrencyRate currencyRate) {
        this.currencyRate = currencyRate;
        return this;
    }

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(this);

        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(SCOREBOARD_PRIORITY, "currency") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                List<Component> lines = new ArrayList<>();
                if (player != null) {
                    lines.add(currentCurrencyDisplay.apply(player));
                    if (currencyRate != null) {
                        if (currencyRate.playerRate != null) {
                            lines.add(currencyRate.currencyRateDisplay.apply(player));
                        }
                        int period = currencyRate.period;
                        if (period != -1) {
                            lines.add(currencyRate.nextCurrencyDisplay.apply((period - (ticksElapsed % period) + 20) / 20));
                        }
                    }
                }
                return lines;
            }
        });
    }

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {
            @Override
            public void run() {
                if (game.getState(EndState.class).isPresent()) {
                    this.cancel();
                    return;
                }
                if (currencyRate != null) {
                    if (ticksElapsed % currencyRate.period == 0) {
                        game.forEachOnlineWarlordsPlayer(warlordsPlayer -> {
                            Float insignia = currencyRate.playerRate.apply(warlordsPlayer);
                            if (insignia == 0) {
                                return;
                            }
                            warlordsPlayer.addCurrency(insignia, false);
                        });
                    }
                }
                ticksElapsed++;
            }
        }.runTaskTimer(0, 0L);
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (startingCurrency == 0 || !(player instanceof WarlordsPlayer)) {
            return;
        }
        player.addCurrency(startingCurrency);
    }

    @EventHandler
    public void onKill(WarlordsDeathEvent event) {
        WarlordsEntity mob = event.getWarlordsEntity();
        if (currencyOnKill == 0 || !(mob instanceof WarlordsNPC warlordsNPC)) {
            return;
        }
        if (warlordsNPC.getMob().getDynamicFlags().contains(DynamicFlags.NO_INSIGNIA)) {
            return;
        }

        for (WarlordsEntity player : PlayerFilter
                .playingGame(mob.getGame())
                .aliveEnemiesOf(mob)
        ) {
            if (!(player instanceof WarlordsPlayer) || player.isDead()) {
                continue;
            }
            int currency = perMobKill.getOrDefault(warlordsNPC.getMob().getMobRegistry(), currencyOnKill);
            int playerCount = (int) player.getGame().warlordsPlayers().count();
            if (scaleWithPlayerCount && playerCount > 2) {
                currency = currencyOnKill - (20 * playerCount);
            }
            player.addCurrency(currency);
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCurrencyAdd(WarlordsAddCurrencyEvent event) {
        if (disableGuildBonus) {
            event.getEventFlags().remove(EventFlags.GUILD);
        }
    }
}
