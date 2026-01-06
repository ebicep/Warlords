package com.ebicep.warlords.game.option.pve.effigytrails;

import java.util.Arrays;
import java.util.Iterator;

public class EffigyChargeManager {

    private final Iterator<Integer> chargeIterator;
    private Integer currentCharge;

    public EffigyChargeManager(int... chargesNeeded) {
        this.chargeIterator = Arrays.stream(chargesNeeded).boxed().iterator();
    }

    public boolean advance() {
        if (chargeIterator.hasNext()) {
            currentCharge = chargeIterator.next();
            return true;
        }
        return false;
    }

}
