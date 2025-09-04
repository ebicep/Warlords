package com.ebicep.warlords.game.option.pve.treasurehunt.conditions;

import com.ebicep.warlords.game.option.pve.treasurehunt.TreasureHuntOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;

public class ConditionHealthDrain implements EnvironmentCondition {

    @Override
    public void onFloorStart(TreasureHuntOption option) {
        new GameRunnable(option.getGame()) {
            @Override
            public void run() {
                for (WarlordsEntity we : PlayerFilter.playingGame(option.getGame()).warlordPlayersFirst()) {
                    float hpDamage = we.getMaxHealth() * 0.005f;
                    we.addInstance(InstanceBuilder
                            .damage()
                            .cause("Environment Condition")
                            .value(hpDamage)
                    );
                }
            }
        }.runTaskTimer(100, 100);
    }

    @Override
    public void onMobHit(WarlordsEntity we, TreasureHuntOption option) {

    }

    @Override
    public void onTick(TreasureHuntOption option) {

    }

    @Override
    public Component title() {
        return Component.text("HP Drain");
    }
}
