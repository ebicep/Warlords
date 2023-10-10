package com.ebicep.warlords.pve.weapons.weapontypes.legendaries;

public interface PassiveCounter {

    /**
     * @return Counter in seconds/is the itemstack amount
     */
    int getCounter();

    /**
     * @return If the counter should constantly update, manually update if false
     */
    default boolean constantlyUpdate() {
        return true;
    }
}
