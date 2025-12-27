package com.ebicep.warlords.pve.newitems.setbonus;

import com.ebicep.warlords.pve.newitems.NewItemsSlot;
import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.pve.newitems.setbonus.sets.IlluminatedPrism;
import com.ebicep.warlords.pve.newitems.setbonus.sets.RandomCommon;
import com.ebicep.warlords.pve.newitems.setbonus.sets.RandomEpic;
import com.ebicep.warlords.pve.newitems.setbonus.sets.RandomRare;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;

import java.util.List;
import java.util.Map;
import java.util.Set;

public enum NewItemsSetBonus implements SetBonus {

    ILLUMINATED_PRISM(new IlluminatedPrism()),
    RANDOM_EPIC(new RandomEpic()),
    RANDOM_RARE(new RandomRare()),
    RANDOM_COMMON(new RandomCommon());

    public static final NewItemsSetBonus[] VALUES = values();
    public static Map<NewItemTier, Set<NewItemsSetBonus>> BY_TIER;
    private final SetBonus setBonus;

    NewItemsSetBonus(SetBonus setBonus) {
        this.setBonus = setBonus;
    }

    @Override
    public boolean isNoBonus() {
        return setBonus.isNoBonus();
    }

    @Override
    public NewItemTier getTier() {
        return setBonus.getTier();
    }

    @Override
    public String getName() {
        return setBonus.getName();
    }

    @Override
    public List<NewItemsSlot> getSlots() {
        return setBonus.getSlots();
    }

    @Override
    public Map<NewItemAttribute, Float> getAttributes() {
        return setBonus.getAttributes();
    }

    @Override
    public void init() {
        setBonus.init();
    }

    @Override
    public Bonus create() {
        return setBonus.create();
    }

    @Override
    public String getConfigFieldName() {
        return setBonus.getConfigFieldName();
    }

    @Override
    public List<Object> getVariables() {
        return setBonus.getVariables();
    }

    public SetBonus getSetBonus() {
        return setBonus;
    }
}
