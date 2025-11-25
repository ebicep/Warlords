package com.ebicep.warlords.pve.items.statpool;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public enum SpecialStatPool implements StatPool {

    EPS {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            warlordsPlayer.getEnergyPerSec().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Item Special Stat Pool", value);
        }

        @Override
        public Operation getOperation() {
            return Operation.ADD;
        }

    },
    EPH {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            warlordsPlayer.getEnergyPerHit().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Item Special Stat Pool", value / 100f);
        }

    },
    MAX_ENERGY {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            warlordsPlayer.getEnergy().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "Item Special Stat Pool", value / 100f);
        }

    },
    COOLDOWN_REDUCTION {
        @Override
        public void applyToAbility(AbstractAbility ability, float value, ItemTier highestTier) {
            float calculatedValue = 1 - value / 100f;
            ability.getCooldown().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, "Item Special Stat Pool", calculatedValue);
        }

    },
    DAMAGE_RESISTANCE {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            warlordsPlayer.setDamageResistance((int) (warlordsPlayer.getSpec().getDamageResistance() + value));
        }

    },


    ;

    @Override
    public DecimalPlace getDecimalPlace() {
        return DecimalPlace.ONES;
    }

}
