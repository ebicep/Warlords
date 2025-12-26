package com.ebicep.warlords.pve.newitems.setbonus;

import com.ebicep.warlords.pve.newitems.NewItemsSlot;
import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.Pair;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public abstract class BaseSet implements SetBonus {

    private NewItemTier tier;
    private String name;
    private List<NewItemsSlot> slots;
    private Map<NewItemAttribute, Integer> attributes;
    private Map<NewItemAttribute, Pair<Short, Short>> bonusAttributeRanges;

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
    public Map<NewItemAttribute, Integer> getAttributes() {
        return attributes;
    }

    @Override
    public void init() {
        this.tier = getValue("tier", NewItemTier.class);
        this.name = getValue("name", String.class);
        this.slots = getListValue("slots", NewItemsSlot.class);
        Map<NewItemAttribute, Integer> attributeMap = new EnumMap<>(NewItemAttribute.class);
        Map<String, Integer> raw = getMapValue("attributes", Integer.class);
        raw.forEach((key, value) -> {
            NewItemAttribute attribute = NewItemAttribute.getByDatabaseName(key);
            if (attribute != null) {
                attributeMap.put(attribute, value);
            } else {
                ChatUtils.MessageType.NEW_ITEMS.sendErrorMessage(new Throwable("Unknown NewItemAttribute '" + key + "' in set bonus '" + getConfigFieldName()));
            }
        });
        this.attributes = attributeMap;
        Map<NewItemAttribute, Pair<Short, Short>> bonusAttributeRanges = new EnumMap<>(NewItemAttribute.class);
        for (NewItemAttribute bonusAttribute : NewItemAttribute.BONUS_ATTRIBUTES) {
            bonusAttributeRanges.put(bonusAttribute, new Pair<>(
                            getValue("bonusAttributeRanges." + bonusAttribute.getDatabaseName() + ".min", short.class, true),
                            getValue("bonusAttributeRanges." + bonusAttribute.getDatabaseName() + ".max", short.class, true
                            )
                    )
            );
        }
        this.bonusAttributeRanges = bonusAttributeRanges;
    }

}
