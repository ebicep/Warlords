package com.ebicep.warlords.pve.newitems.tiers;

import com.ebicep.warlords.database.repositories.config.ConfigBased;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.pve.newitems.NewItemRerollCost;
import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

public interface ItemTier extends ConfigBased, NewItemRerollCost {

    @Override
    default ConfigManager.Config getConfig() {
        return ConfigManager.NEW_ITEMS_CONFIG;
    }

    @Override
    default List<String> getConfigNamespaces() {
        return ConfigManager.PVE_NAMESPACES;
    }

    @Override
    default String getPrefix() {
        return "tiers.";
    }

    void init();

    TextColor getTextColor();

    Component getStarComponent();

    Material getTerracotaMaterial();

    String getName();

    int getWeight();

    int bonusAttributes();

    Map<NewItemAttribute, Pair<Float, Float>> bonusAttributeRanges();

}
