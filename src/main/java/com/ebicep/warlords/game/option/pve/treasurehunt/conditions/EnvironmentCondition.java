package com.ebicep.warlords.game.option.pve.treasurehunt.conditions;

import com.ebicep.warlords.game.option.pve.treasurehunt.TreasureHuntOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.kyori.adventure.text.Component;

public interface EnvironmentCondition {

    void onFloorStart(TreasureHuntOption option);

    void onMobHit(WarlordsEntity we, TreasureHuntOption option);

    void onTick(TreasureHuntOption option);

    Component title(); // show as title card
}
