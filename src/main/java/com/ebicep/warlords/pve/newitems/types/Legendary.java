package com.ebicep.warlords.pve.newitems.types;

import com.ebicep.warlords.pve.newitems.NewItemsUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class Legendary extends NewItemType {

    private static final Component STAR_COMPONENT = NewItemsUtils.createStarComponent(NamedTextColor.GOLD, 1);

    @Override
    public TextColor getTextColor() {
        return NamedTextColor.GOLD;
    }

    @Override
    public Component getStarComponent() {
        return STAR_COMPONENT;
    }

    @Override
    public int bonusAttributes() {
        return 3;
    }

}
