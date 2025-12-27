package com.ebicep.warlords.pve.newitems.setbonus;

import com.ebicep.warlords.database.repositories.config.ConfigBased;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.newitems.NewItemsSlot;
import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.util.java.Pair;

import java.util.List;
import java.util.Map;

public interface SetBonus extends ConfigBased.ConfigDescription {

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
        return "sets.";
    }

    boolean isNoBonus();

    NewItemTier getTier();

    String getName();

    List<NewItemsSlot> getSlots();

    Map<NewItemAttribute, Float> getAttributes();

    Map<NewItemAttribute, Pair<Float, Float>> bonusAttributeRanges();

    void init();

    Bonus create();

    interface Bonus {

        Bonus NONE = warlordsPlayer -> {
        };

        void apply(WarlordsPlayer warlordsPlayer);

        default void unapply(WarlordsPlayer warlordsPlayer) {

        }

    }

}
