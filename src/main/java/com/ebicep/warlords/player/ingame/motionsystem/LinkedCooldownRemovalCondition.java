package com.ebicep.warlords.player.ingame.motionsystem;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.motionsystem.motionaddon.RemovalCondition;

public class LinkedCooldownRemovalCondition implements RemovalCondition {

    private final WarlordsEntity warlordsEntity;
    private final AbstractCooldown<?> linkedCooldown;

    public LinkedCooldownRemovalCondition(WarlordsEntity warlordsEntity, AbstractCooldown<?> linkedCooldown) {
        this.warlordsEntity = warlordsEntity;
        this.linkedCooldown = linkedCooldown;
    }

    @Override
    public String addonName() {
        return "Linked Cooldown Removal Condition";
    }

    @Override
    public boolean removeAnyMatch() {
        return !warlordsEntity.getCooldownManager().hasCooldown(linkedCooldown);
    }

}
