package com.ebicep.warlords.pve.items.statpool;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.ItemTier;

public enum SpecialStatPool implements StatPool {

    EPS {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            AbstractPlayerClass playerClass = warlordsPlayer.getSpec();
            playerClass.setEnergyPerSec(playerClass.getEnergyPerSec() + value);
        }

        @Override
        public Operation getOperation() {
            return Operation.ADD;
        }

    },
    EPH {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            float calculatedValue = 1 + value / 100f;
            AbstractPlayerClass playerClass = warlordsPlayer.getSpec();
            playerClass.setEnergyPerHit(playerClass.getEnergyPerHit() * calculatedValue);
        }

    },
    MAX_ENERGY {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            float calculatedValue = 1 + value / 100f;
            AbstractPlayerClass playerClass = warlordsPlayer.getSpec();
            playerClass.setMaxEnergy((int) (playerClass.getMaxEnergy() * calculatedValue));
        }

    },
    COOLDOWN_REDUCTION {
        @Override
        public void applyToAbility(AbstractAbility ability, float value, ItemTier highestTier) {
            float calculatedValue = 1 - value / 100f;
            ability.setCooldown(ability.getCooldown() * calculatedValue);
        }

    },
    DAMAGE_RESISTANCE {
        @Override
        public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {
            AbstractPlayerClass playerClass = warlordsPlayer.getSpec();
            playerClass.setDamageResistance((int) (playerClass.getDamageResistance() + value));
        }

    },


    ;

    @Override
    public Operation getOperation() {
        return Operation.MULTIPLY;
    }

    @Override
    public DecimalPlace getDecimalPlace() {
        return DecimalPlace.ONES;
    }

}
