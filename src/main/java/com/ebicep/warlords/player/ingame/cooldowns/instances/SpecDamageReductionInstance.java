package com.ebicep.warlords.player.ingame.cooldowns.instances;

/**
 * Modifies the damage reduction the warlords entity when damage is being applied to them. Applies to fall damage
 */
public interface SpecDamageReductionInstance {

    default float modifyDamageReduction(float damageReduction) {
        return damageReduction;
    }

}
