package com.ebicep.warlords.player.ingame.cooldowns.cooldowns;

import com.ebicep.warlords.player.ingame.AbstractWarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;

import java.util.function.Consumer;

/**
 * This type of cooldown is used for any damage heal instances where an action immediately needs to be performed once,
 * the cooldown is removed after
 */
public class DamageHealCompleteCooldown<T> extends AbstractCooldown<T> {

    public DamageHealCompleteCooldown(String name, String nameAbbreviation, Class<T> cooldownClass, T cooldownObject, AbstractWarlordsEntity from, CooldownTypes cooldownType, Consumer<CooldownManager> onRemove) {
        super(name, nameAbbreviation, cooldownClass, cooldownObject, from, cooldownType, onRemove);
    }

    @Override
    public String getNameAbbreviation() {
        return "";
    }

    @Override
    public void onTick() {

    }

    @Override
    public boolean removeCheck() {
        return false;
    }
}
