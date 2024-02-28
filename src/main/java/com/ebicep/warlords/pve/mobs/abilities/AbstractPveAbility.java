package com.ebicep.warlords.pve.mobs.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class AbstractPveAbility extends AbstractAbility {

    protected PveOption pveOption;

    public AbstractPveAbility(String name, float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost);
    }

    public AbstractPveAbility(String name, float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost, boolean startNoCooldown) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, startNoCooldown);
    }

    public AbstractPveAbility(String name, float cooldown, float energyCost) {
        super(name, cooldown, energyCost);
    }

    public AbstractPveAbility(String name, float cooldown, float energyCost, boolean startNoCooldown) {
        super(name, cooldown, energyCost, startNoCooldown);
    }

    public AbstractPveAbility(String name, float cooldown, float energyCost, float startCooldown) {
        super(name, cooldown, energyCost, startCooldown);
    }

    public AbstractPveAbility(String name, float minDamageHeal, float maxDamageHeal, float cooldown, float energyCost, float critChance, float critMultiplier) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
    }

    public AbstractPveAbility(
            String name,
            float minDamageHeal,
            float maxDamageHeal,
            float cooldown,
            float energyCost,
            float critChance,
            float critMultiplier,
            float startCooldown
    ) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, startCooldown);
    }

    @Override
    public void updateDescription(Player player) {

    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        if (timesUsed > 1) { // used more than once means cached pveOption is null
            return onPveActivate(wp, pveOption);
        }
        if (pveOption != null) {
            return onPveActivate(wp, pveOption);
        }
        PveOption pve = wp.getGame()
                          .getOptions()
                          .stream()
                          .filter(PveOption.class::isInstance)
                          .map(PveOption.class::cast)
                          .findFirst().orElse(null);
        if (pve == null) {
            return false;
        }
        pveOption = pve;
        return onPveActivate(wp, pve);
    }

    public abstract boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption);
}
