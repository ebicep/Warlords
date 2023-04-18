package com.ebicep.warlords.pve;

public class SpendableBuyShop {

    private final int amount;
    private final Spendable spendable;
    private final int stock;
    private final int price;

    public SpendableBuyShop(int amount, Spendable spendable, int stock, int price) {
        this.amount = amount;
        this.spendable = spendable;
        this.stock = stock;
        this.price = price;
    }

    public String getMapName() {
        return amount + "_" + spendable.getName(); //TODO .name();
    }

    public int getAmount() {
        return amount;
    }

    public Spendable getSpendable() {
        return spendable;
    }

    public int getStock() {
        return stock;
    }

    public int getPrice() {
        return price;
    }
}
