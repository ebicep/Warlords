package com.ebicep.warlords.pve.items.statpool;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.ItemTier;

public interface StatPool {

    default void applyToAbility(AbstractAbility ability, float value, ItemTier highestTier) {

    }

    default void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value, ItemTier highestTier) {

    }

    default BasicStatPool.Operation getOperation() {
        return Operation.MULTIPLY;
    }

    default BasicStatPool.DecimalPlace getDecimalPlace() {
        return DecimalPlace.TENTHS;
    }

    enum Operation {
        ADD(""),
        MULTIPLY("%");

        public final String prepend;

        Operation(String prepend) {
            this.prepend = prepend;
        }
    }

    enum DecimalPlace {

        TENTHS(10),
        ONES(1);

        public final int value;

        DecimalPlace(int value) {
            this.value = value;
        }
    }
}
