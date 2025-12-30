package com.ebicep.warlords.pve.newitems;

import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.pve.newitems.setbonus.NewItemsSetBonus;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NewItemLoreCreator {

    private static final Pair<Float, Float> ZERO_RANGE = new Pair<>(0f, 0f);

    private static void addBasicAttributes(NewItemsSetBonus setBonus, List<Component> lore) {
        Map<NewItemAttribute, Float> basicAttributes = setBonus.getAttributes();
        boolean onlyHealth = basicAttributes.size() == 1 && basicAttributes.containsKey(NewItemAttribute.HEALTH);
        if (basicAttributes.containsKey(NewItemAttribute.HEALTH)) {
            lore.add(NewItemAttribute.HEALTH.formatValue(basicAttributes.get(NewItemAttribute.HEALTH), "+"));
            lore.add(Component.empty());
        }
        if (!basicAttributes.isEmpty() && !onlyHealth) {
            lore.add(Component.text("Basic Attributes:", NamedTextColor.GRAY));
            for (NewItemAttribute basicAttribute : NewItemAttribute.BASIC_ATTRIBUTES) {
                if (basicAttribute == NewItemAttribute.HEALTH) {
                    continue;
                }
                Float value = basicAttributes.get(basicAttribute);
                if (value != null) {
                    lore.add(basicAttribute.formatValue(value, "+"));
                }
            }
            lore.add(Component.empty());
        }
    }

    private static void addBonusAttributes(List<Component> lore, Map<NewItemAttribute, Float> bonusAttributeValues) {
        if (!bonusAttributeValues.isEmpty()) {
            lore.add(Component.text("Bonus Attributes:", NamedTextColor.GRAY));
            for (NewItemAttribute bonusAttribute : NewItemAttribute.BONUS_ATTRIBUTES) {
                Float value = bonusAttributeValues.get(bonusAttribute);
                if (value != null) {
                    lore.add(bonusAttribute.formatValue(value, "+"));
                }
            }
            lore.add(Component.empty());
        }
    }

    private static void addSetBonus(NewItemsSetBonus setBonus, @Nullable NewItemsManager itemsManager, @Nullable NewItemLoadout loadout, List<Component> lore, NewItemTier tier) {
        if (!setBonus.isNoBonus()) {
            List<NewItemsSlot> slots = setBonus.getSlots();
            String suffix = "";
            if (itemsManager != null && loadout != null) {
                List<NewItem> appliedItems = loadout.getActualItems(itemsManager);
                Map<NewItemsSetBonus, Set<NewItemsSlot>> activeSets = NewItemsUtils.getActiveSets(appliedItems);
                suffix = " [" + activeSets.get(setBonus).size() + "/" + slots.size() + "]";
            }

            lore.add(Component.text(setBonus.getName() + " Set" + suffix, NamedTextColor.GRAY));
            for (NewItemsSlot newItemsSlot : slots) {
                lore.add(Component.text(" - " + setBonus.getName() + " " + newItemsSlot.getName(), tier.getTextColor()));
            }
            lore.add(Component.empty());
            lore.add(Component.text("Set Bonus:", NamedTextColor.GRAY));
            lore.addAll(setBonus.getDescriptionLore());
            lore.add(Component.empty());
        }
    }

    private static void addBonusAttributes(List<Component> lore, NewItemsSetBonus setBonus) {
        Map<NewItemAttribute, Pair<Float, Float>> attributeRanges = setBonus.getBonusAttributeRanges();
        lore.add(Component.text("Bonus Attributes:", NamedTextColor.GRAY));
        for (NewItemAttribute bonusAttribute : NewItemAttribute.BONUS_ATTRIBUTES) {
            Pair<Float, Float> defaultRange = setBonus.getTier().getBonusAttributeRanges().getOrDefault(bonusAttribute, ZERO_RANGE);
            Pair<Float, Float> range = attributeRanges.getOrDefault(bonusAttribute, defaultRange);
            float low = range.getA() != 0 ? range.getA() : defaultRange.getA();
            float high = range.getA() != 0 ? range.getB() : defaultRange.getB();
            // TODO yikes, create separate format range method?
            lore.add(Component.text(NumberFormat.formatOptionalTenths(low) + "-", bonusAttribute.getTextColor()).append(bonusAttribute.formatValue(high, "")));
        }
        lore.add(Component.empty());
    }

    public static class Builder {

        private final List<Component> components = new ArrayList<>();
        private NewItemsSetBonus setBonus;
        private NewItemTier tier;

        public Builder(NewItem newItem) {
            this(newItem.getSetBonus());
        }

        public Builder(NewItemsSetBonus setBonus) {
            this.setBonus = setBonus;
            this.tier = setBonus.getTier();
        }

        public Builder addStarComponent() {
            components.add(tier.getStarComponent());
            components.add(Component.empty());
            return this;
        }

        public Builder addBasicAttributes() {
            NewItemLoreCreator.addBasicAttributes(setBonus, components);
            return this;
        }

        public Builder addBonusAttributes(Map<NewItemAttribute, Float> bonusAttributeValues) {
            NewItemLoreCreator.addBonusAttributes(components, bonusAttributeValues);
            return this;
        }

        public Builder addBonusAttributes() {
            NewItemLoreCreator.addBonusAttributes(components, setBonus);
            return this;
        }

        public Builder addSetBonus() {
            return addSetBonus(null, null);
        }

        public Builder addSetBonus(NewItemsManager itemsManager, NewItemLoadout loadout) {
            NewItemLoreCreator.addSetBonus(setBonus, itemsManager, loadout, components, tier);
            return this;
        }

        public List<Component> build() {
            if (!components.isEmpty() && components.getLast() == Component.empty()) {
                components.removeLast();
            }
            return components;
        }

    }

}
