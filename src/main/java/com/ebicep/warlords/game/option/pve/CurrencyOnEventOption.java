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
import java.util.Collections;
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
    private Function<WarlordsEntity, Integer> currencyPerSecond = warlordsEntity -> 0;

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

    public CurrencyOnEventOption setPerSecond(Function<WarlordsEntity, Integer> currencyPerSecond) {
        this.currencyPerSecond = currencyPerSecond;
        return this;
    }

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(this);

        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(SCOREBOARD_PRIORITY, "currency") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                if (player != null) {
                    return Collections.singletonList(
                            Component.text("Insignia: ")
                                     .append(Component.text("❂ " + NumberFormat.addCommas(player.getCurrency()), NamedTextColor.GOLD)));
                }
                return Collections.singletonList(Component.empty());
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
                game.forEachOnlineWarlordsPlayer(warlordsPlayer -> {
                    Integer insignia = currencyPerSecond.apply(warlordsPlayer);
                    if (insignia == 0) {
                        return;
                    }
                    warlordsPlayer.addCurrency(insignia, true);
                });
            }
        }.runTaskTimer(0, 20L);
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
