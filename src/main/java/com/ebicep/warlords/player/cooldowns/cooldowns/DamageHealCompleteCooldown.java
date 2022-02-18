package com.ebicep.warlords.player.cooldowns.cooldowns;

import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.cooldowns.CooldownManager;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;

import java.util.function.Consumer;

/**
 * This type of cooldown is used for any damage heal instances where an action immediately needs to be performed once,
 * the cooldown is removed after
 */
public class DamageHealCompleteCooldown<T> extends AbstractCooldown<T> {

    public DamageHealCompleteCooldown(String name, String nameAbbreviation, Class<T> cooldownClass, T cooldownObject, WarlordsPlayer from, CooldownTypes cooldownType, Consumer<CooldownManager> onRemove) {
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
