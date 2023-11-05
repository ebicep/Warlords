package com.ebicep.warlords.player.ingame.cooldowns.instances;

import com.ebicep.warlords.abilities.internal.AbstractAbility;


/**
 * <p>NOT CURRENTLY USED</p>
 * Modifies the new cooldown of an ability, cooldown = (CD + additive) * multiplicative
 */
@Deprecated
public interface AbilityCooldownInstance {

    /**
     * Ex. 0.25f = .25s
     *
     * @return How many seconds to add to the additive amount
     */
    default float getAbilityAdditiveCooldown(AbstractAbility ability) {
        return 0;
    }

    /**
     * Ex. -0.1f = -10%
     *
     * @return How many seconds to ADD to the multiplicative amount
     */
    default float getAbilityMultiplicativeCooldownAdd(AbstractAbility ability) {
        return 0;
    }

    /**
     * Ex. 0.1f = 90% cdr
     *
     * @return How many seconds to MULTIPLY to the multiplicative amount
     */
    default float getAbilityMultiplicativeCooldownMult(AbstractAbility ability) {
        return 1;
    }

}
