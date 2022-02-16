package com.ebicep.warlords.player.cooldowns.cooldowns;

import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.cooldowns.CooldownManager;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;

import java.util.function.Consumer;

public class DamageHealExpiringCooldown<T> extends AbstractCooldown<T> {

    public DamageHealExpiringCooldown(String name, String nameAbbreviation, Class<T> cooldownClass, T cooldownObject, WarlordsPlayer from, CooldownTypes cooldownType, Consumer<CooldownManager> onRemove) {
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
