package com.ebicep.warlords.pve;

public record SpendableBuyShopDistinct(int amount, Spendable spendable, int stock, int price, Currencies currency) {

    public String getMapName() {
        return amount + "_" + spendable.getName(); //TODO .name();
    }
}

