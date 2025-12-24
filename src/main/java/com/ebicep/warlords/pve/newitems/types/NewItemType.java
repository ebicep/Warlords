package com.ebicep.warlords.pve.newitems.types;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public abstract class NewItemType {

    public static final NewItemType COMMON = new Common();
    public static final NewItemType RARE = new Rare();
    public static final NewItemType EPIC = new Epic();
    public static final NewItemType SOVEREIGN = new Sovereign();
    public static final NewItemType LEGENDARY = new Legendary();
    public static final NewItemType ASCENDANT = new Ascendant();

    public abstract TextColor getTextColor();

    public abstract Component getStarComponent();

    public abstract int bonusAttributes();

}
