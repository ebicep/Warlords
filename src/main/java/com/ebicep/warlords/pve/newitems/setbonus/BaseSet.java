package com.ebicep.warlords.pve.newitems.setbonus;

import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.newitems.NewItemsSlot;
import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.Pair;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public abstract class BaseSet implements SetBonus {

    private boolean noBonus;
    private NewItemTier tier;
    private String name;
    private List<NewItemsSlot> slots;
    private Map<NewItemAttribute, Float> attributes;
    private Map<NewItemAttribute, Pair<Float, Float>> bonusAttributeRanges;
    private Map<Integer, Map<Spendable, Long>> rerollCost;
    private Map<Integer, Map<Spendable, Long>> lockScrollRerollCost;

    @Override
    public boolean isNoBonus() {
        return noBonus;
    }

    @Override
    public NewItemTier getTier() {
        return tier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<NewItemsSlot> getSlots() {
        return slots;
    }

    @Override
    public Map<NewItemAttribute, Float> getAttributes() {
        return attributes;
    }

    @Override
    public Map<NewItemAttribute, Pair<Float, Float>> getBonusAttributeRanges() {
        return bonusAttributeRanges;
    }

    @Override
    public void setBonusAttributeRanges(Map<NewItemAttribute, Pair<Float, Float>> bonusAttributeRanges) {
        this.bonusAttributeRanges = bonusAttributeRanges;
    }

    @Override
    public void init() {
        this.noBonus = getValue("noBonus", boolean.class, true);
        this.tier = getValue("tier", NewItemTier.class);
        this.name = getValue("name", String.class);
        this.slots = getListValue("slots", NewItemsSlot.class);
        Map<NewItemAttribute, Float> attributeMap = new EnumMap<>(NewItemAttribute.class);
        Map<String, Float> raw = getMapValue("attributes", Float.class);
        raw.forEach((key, value) -> {
            NewItemAttribute attribute = NewItemAttribute.getByDatabaseName(key);
            if (attribute != null) {
                attributeMap.put(attribute, value);
            } else {
                ChatUtils.MessageType.NEW_ITEMS.sendErrorMessage(new Throwable("Unknown NewItemAttribute '" + key + "' in set bonus '" + getConfigFieldName()));
            }
        });
        this.attributes = attributeMap;
        init(this);
    }

    @Override
    public Map<Integer, Map<Spendable, Long>> rerollCost() {
        return rerollCost;
    }

    @Override
    public void setRerollCost(Map<Integer, Map<Spendable, Long>> rerollCost) {
        this.rerollCost = rerollCost;
    }

    @Override
    public Map<Integer, Map<Spendable, Long>> lockScrollRerollCost() {
        return lockScrollRerollCost;
    }

    @Override
    public void setLockScrollRerollCost(Map<Integer, Map<Spendable, Long>> lockScrollRerollCost) {
        this.lockScrollRerollCost = lockScrollRerollCost;
    }

}
