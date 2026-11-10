package com.ebicep.warlords.pve.newitems.attributes;

/**
 * Thorns damage before it is turned into an instance.
 * Crown of Thorns is the only multiplier accepted here. The instance is flagged to ignore other source damage boosts.
 */
public final class ThornsDamage {

    public static final int BASE_CAP = 500;

    private ThornsDamage() {
    }

    public static float calculate(float incomingDamage, float thornsFraction, float crownDamageMultiplier, int cap) {
        float damage = incomingDamage * thornsFraction * crownDamageMultiplier;
        if (damage > cap) {
            return cap;
        }
        return damage;
    }

}
