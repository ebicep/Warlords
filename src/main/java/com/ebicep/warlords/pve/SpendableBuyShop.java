package com.ebicep.warlords.pve;

public record SpendableBuyShop(int amount, Spendable spendable, int stock, int price) {

    public String getMapName() {
        return amount + "_" + spendable.getName(); //TODO .name();
    }
}
