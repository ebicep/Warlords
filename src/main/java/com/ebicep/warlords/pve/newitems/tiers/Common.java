package com.ebicep.warlords.pve.newitems.tiers;

import com.ebicep.warlords.pve.newitems.NewItemsUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

public class Common extends BaseTier {

    private static final Component STAR_COMPONENT = NewItemsUtils.createStarComponent(NamedTextColor.GREEN, 1);

    @Override
    public TextColor getTextColor() {
        return NamedTextColor.GREEN;
    }

    @Override
    public Component getStarComponent() {
        return STAR_COMPONENT;
    }

    @Override
    public Material getTerracotaMaterial() {
        return Material.GREEN_TERRACOTTA;
    }

    @Override
    public String getConfigFieldName() {
        return "common";
    }

}
