package com.ebicep.warlords.maps.option;

import com.ebicep.warlords.events.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.events.WarlordsPointsChangedEvent;
import com.ebicep.warlords.maps.Game;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class WinByPointsOption implements Option, Listener {
    public static final int DEFAULT_POINT_LIMIT = 1000;
    
    private int pointLimit;
    private boolean hasActivated = false;

    public WinByPointsOption() {
        this(DEFAULT_POINT_LIMIT);
    }
    public WinByPointsOption(int pointLimit) {
        this.pointLimit = pointLimit;
    }     

    @Override
    public void register(Game game) {
        game.registerEvents(this);
    }

    void setLimit(int pointLimit) {
        
    }
    
    @EventHandler
    public void onEvent(WarlordsPointsChangedEvent event) {
        if(!hasActivated && event.getNewPoints() >= pointLimit) {
            WarlordsGameTriggerWinEvent e = new WarlordsGameTriggerWinEvent(event.getGame(), this, null);
            Bukkit.getPluginManager().callEvent(e);
            if (!e.isCancelled()) {
                hasActivated = true;
            }
        }
    }
}
