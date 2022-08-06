package com.ebicep.warlords.game.option.wavedefense;

import com.ebicep.warlords.events.player.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
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
    }

    @EventHandler
    public void onKill(WarlordsDeathEvent event) {
        if (event.getKiller() != null) {
            event.getKiller().sendMessage(ChatColor.AQUA + "+" + currencyToAdd + " ❂ Insignia");
            event.getKiller().addCurrency(currencyToAdd);
        }

        for (WarlordsEntity we : event.getPlayer().getHitBy().keySet()) {
            if (we instanceof WarlordsPlayer) {
                we.sendMessage(ChatColor.AQUA + "+" + currencyToAdd + " ❂ Insignia");
                we.addCurrency(currencyToAdd);
            }
        }
    }
}
