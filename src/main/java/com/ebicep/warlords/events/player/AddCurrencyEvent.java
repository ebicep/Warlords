package com.ebicep.warlords.events.player;

import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.pve.Currencies;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import javax.annotation.Nonnull;

public class AddCurrencyEvent extends Event {

    private static final HandlerList handlers = new HandlerList();


    private final DatabasePlayerPvE databasePlayerPvE;
    private final Currencies currency;
    private final long amount;

    public AddCurrencyEvent(DatabasePlayerPvE databasePlayerPvE, Currencies currency, long amount) {
        this.databasePlayerPvE = databasePlayerPvE;
        this.currency = currency;
        this.amount = amount;
    }

    public DatabasePlayerPvE getDatabasePlayerPvE() {
        return databasePlayerPvE;
    }

    public Currencies getCurrency() {
        return currency;
    }

    public long getAmount() {
        return amount;
    }

    @Nonnull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
