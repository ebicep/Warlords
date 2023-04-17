package com.ebicep.warlords.game.option.pve.wavedefense.events.modes;

import com.ebicep.warlords.events.game.pve.WarlordsGameWaveRespawnEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsAddVelocityEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.EventGameEndOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public class BoltarosLairOption implements Option, EventGameEndOption {

    @Override
    public void register(@Nonnull Game game) {
        game.registerEvents(new Listener() {

            @EventHandler
            public void onWaveRespawn(WarlordsGameWaveRespawnEvent event) {
                event.setCancelled(true);
            }

            @EventHandler
            public void onVelocity(WarlordsAddVelocityEvent event) {
                if (event.getWarlordsEntity() instanceof WarlordsNPC) {
                    Vector vector = event.getVector();
                    vector.setY(vector.getY() * .75);
                }
            }

        });
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (player instanceof WarlordsPlayer) {
            AbilityTree abilityTree = ((WarlordsPlayer) player).getAbilityTree();
            abilityTree.setMaxMasterUpgrades(6);
            for (AbstractUpgradeBranch<?> upgradeBranch : abilityTree.getUpgradeBranches()) {
                upgradeBranch.setMaxUpgrades(8);
            }
        }
    }

}
