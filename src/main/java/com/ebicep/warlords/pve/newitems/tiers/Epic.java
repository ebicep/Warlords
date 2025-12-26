package com.ebicep.warlords.pve.newitems.tiers;

import com.ebicep.warlords.pve.newitems.NewItemsUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class Epic extends BaseTier {

    private static final Component STAR_COMPONENT = NewItemsUtils.createStarComponent(NamedTextColor.RED, 1);


    @Override
    public TextColor getTextColor() {
        return NamedTextColor.RED;
    }

    @Override
    public Component getStarComponent() {
        return STAR_COMPONENT;
    }

    @Override
    public String getConfigFieldName() {
        return "epic";
    }

}
