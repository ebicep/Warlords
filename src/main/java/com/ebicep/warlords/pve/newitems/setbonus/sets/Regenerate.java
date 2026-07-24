package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.events.player.ingame.pve.WarlordsAddCurrencyEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class Regenerate extends BaseSet {

    @Override
    public void init() {
        super.init();
    }

    @Override
    public String getConfigFieldName() {
        return "regenerate";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of();
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            new GameRunnable(warlordsPlayer.getGame()) {
                @Override
                public void run() {
                    if (warlordsPlayer.isDead()) {
                        return;
                    }
                    if (warlordsPlayer.getRegenTickTimer() > 1) {
                        warlordsPlayer.setRegenTickTimer(1);
                    }
                }
            }.runTaskTimer(0, 0);
         }
    }
}