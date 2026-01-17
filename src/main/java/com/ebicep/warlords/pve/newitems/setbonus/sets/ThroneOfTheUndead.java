package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.events.player.ingame.WarlordsRespawnEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsGiveRespawnEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Objects;

public class ThroneOfTheUndead extends BaseSet {

    private int respawnTimeReductionPercent;
    private int respawnHealthPercent;
    private int respawnEnergy;

    @Override
    public void init() {
        super.init();
        this.respawnTimeReductionPercent = getValue("respawnTimeReductionPercent", int.class);
        this.respawnHealthPercent = getValue("respawnHealthPercent", int.class);
        this.respawnEnergy = getValue("respawnEnergy", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "throneOfTheUndead";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(respawnTimeReductionPercent, respawnHealthPercent, respawnEnergy);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getGame().registerEvents(new Listener() {
                @EventHandler
                public void onEvent(WarlordsGiveRespawnEvent event) {
                    if (!Objects.equals(event.getWarlordsEntity(), warlordsPlayer)) {
                        return;
                    }
                    event.getRespawnTimer().set((int) (event.getRespawnTimer().get() * (respawnTimeReductionPercent / 100f)));
                }

                @EventHandler
                public void onRespawn(WarlordsRespawnEvent event) {
                    if (!Objects.equals(event.getWarlordsEntity(), warlordsPlayer)) {
                        return;
                    }
                    event.getWarlordsEntity().setCurrentHealth(event.getWarlordsEntity().getMaxHealth() * (respawnHealthPercent / 100f));
                    event.getWarlordsEntity().setCurrentEnergy(respawnEnergy);
                }

            });
        }

    }

}