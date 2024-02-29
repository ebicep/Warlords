package com.ebicep.warlords.pve.mobs.abilities;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;

import javax.annotation.Nullable;

public interface PvEAbility {

    /**
     * Always call this not getPveOption directly
     *
     * @param warlordsEntity the entity that is using the ability
     * @return pveOption
     */
    @Nullable
    default PveOption getPveOption(WarlordsEntity warlordsEntity) {
        PveOption pveOption = getPveOption();
        if (pveOption == null) {
            setPveOption(warlordsEntity.getGame()
                                       .getOptions()
                                       .stream()
                                       .filter(PveOption.class::isInstance)
                                       .map(PveOption.class::cast)
                                       .findFirst()
                                       .orElse(null));
        }
        return getPveOption();
    }

    @Nullable
    PveOption getPveOption();

    void setPveOption(PveOption pveOption);

}
