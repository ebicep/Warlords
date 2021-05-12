package com.ebicep.BountifulAPI;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TabTitleSendEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private String header;
    private String footer;
    private boolean cancelled = false;

    public TabTitleSendEvent(Player player, String header, String footer) {
        this.player = player;
        this.header = header;
        this.footer = footer;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getHeader() {
        return this.header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getFooter() {
        return this.footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
