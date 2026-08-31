package com.ebicep.warlords.pve.newitems;

import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.pve.newitems.gems.Gem;
import com.ebicep.warlords.pve.newitems.setbonus.NewItemsSetBonus;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class NewItemLoreCreator {

    public static final Pair<Float, Float> ZERO_RANGE = new Pair<>(0f, 0f);
    private static final Component BONUS_ATTRIBUTES = Component.text("Bonus Attributes:", NamedTextColor.GRAY);
    private static final Component BASIC_ATTRIBUTES = Component.text("Basic Attributes:", NamedTextColor.GRAY);
    private static final Component SET_BONUS = Component.text("Set Bonus:", NamedTextColor.GRAY);
    private static final Component GEM_SLOTS = Component.text("Gem Slots:", NamedTextColor.GRAY);

    private static void addBasicAttributes(boolean label, NewItemsSetBonus setBonus, List<Component> lore) {
        Map<NewItemAttribute, Float> basicAttributes = setBonus.getAttributes();
        boolean onlyHealth = basicAttributes.size() == 1 && basicAttributes.containsKey(NewItemAttribute.HEALTH);
        if (basicAttributes.containsKey(NewItemAttribute.HEALTH)) {
            lore.add(NewItemAttribute.HEALTH.formatValue(basicAttributes.get(NewItemAttribute.HEALTH), "+"));
            lore.add(Component.empty());
        }
        if (!basicAttributes.isEmpty() && !onlyHealth) {
            if (label) {
                lore.add(BASIC_ATTRIBUTES);
            }
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

    private static void addBonusAttributes(
            boolean label,
            NewItemsSetBonus setBonus,
            List<Component> lore,
            Map<NewItemAttribute, Integer> bonusAttributeValues,
            NewItem.StarPieceBonus starPieceBonus
    ) {
        if (bonusAttributeValues.isEmpty()) {
            return;
        }
        if (label) {
            lore.add(BONUS_ATTRIBUTES);
        }
        for (NewItemAttribute bonusAttribute : NewItemAttribute.BONUS_ATTRIBUTES) {
            Integer value = bonusAttributeValues.get(bonusAttribute);
            if (value == null) {
                continue;
            }
            Pair<Float, Float> defaultRange = setBonus.getTier().getBonusAttributeRanges().getOrDefault(bonusAttribute, ZERO_RANGE);
            Pair<Float, Float> range = setBonus.getBonusAttributeRanges().getOrDefault(bonusAttribute, defaultRange);
            float high = range.getA() != 0 ? range.getB() : defaultRange.getB();
            boolean hasStarPieceBonus = starPieceBonus != null && starPieceBonus.attribute() == bonusAttribute;
            int maxValue = (int) Math.ceil(high);
            if (hasStarPieceBonus) {
                maxValue = (int) Math.ceil(maxValue * (1 + starPieceBonus.starPiece().starPieceBonusValue / 100f));
            }
            Component component = bonusAttribute.formatValue(value, "+");
            if (maxValue == value) {
                component = component.append(Component.text(" [MAX]", NamedTextColor.LIGHT_PURPLE));
            }
            if (hasStarPieceBonus) {
                component = component.append(Component.text(" (+" + starPieceBonus.starPiece().starPieceBonusValue + "% ✦)", NamedTextColor.WHITE));
            }
            lore.add(component);
        }
        lore.add(Component.empty());
    }

    private static void addSetBonus(NewItemsSetBonus setBonus, @Nullable NewItemsManager itemsManager, @Nullable NewItemLoadout loadout, List<Component> lore, NewItemTier tier) {
        if (setBonus.isNoBonus()) {
            return;
        }
        List<NewItemsSlot> slots = setBonus.getSlots();
        String suffix = "";
        if (itemsManager != null && loadout != null) {
            List<NewItem> appliedItems = loadout.getActualItems(itemsManager);
            Map<NewItemsSetBonus, List<NewItemsSlot>> activeSets = NewItemsUtils.getActiveSets(appliedItems);
            suffix = " [" + activeSets.get(setBonus).size() + "/" + slots.size() + "]";
        }

        lore.add(Component.text(setBonus.getName() + " Set" + suffix, NamedTextColor.GRAY));
        for (NewItemsSlot newItemsSlot : slots) {
            lore.add(Component.text(" - " + setBonus.getName() + " " + newItemsSlot.getName(), tier.getTextColor()));
        }
        lore.add(Component.empty());
        lore.add(SET_BONUS);
        lore.addAll(setBonus.getDescriptionLore());
        lore.add(Component.empty());
    }

    private static void addGems(List<Component> lore, int unlockedSlots, int maxSlots, List<Gem> socketedGems) {
        if (maxSlots <= 0) {
            return;
        }
        lore.add(GEM_SLOTS);
        for (int slot = 0; slot < maxSlots; slot++) {
            if (slot >= unlockedSlots) {
                lore.add(Component.text(" - Locked Socket", NamedTextColor.DARK_GRAY));
                continue;
            }
            Gem gem = slot < socketedGems.size() ? socketedGems.get(slot) : null;
            if (gem == null) {
                lore.add(Component.text(" - Empty Socket", NamedTextColor.GRAY));
            } else {
                lore.add(Component.text(" - ", NamedTextColor.GRAY)
                                  .append(gem.getColoredName())
                                  .append(Component.text(" ", NamedTextColor.GRAY))
                                  .append(gem.getAttributeComponent())
                );
            }
        }
        lore.add(Component.empty());
    }

    private static void addBonusAttributes(boolean label, List<Component> lore, NewItemsSetBonus setBonus, Set<NewItemAttribute> attributes) {
        Map<NewItemAttribute, Pair<Float, Float>> attributeRanges = setBonus.getBonusAttributeRanges();
        if (label) {
            lore.add(BONUS_ATTRIBUTES);
        }
        for (NewItemAttribute bonusAttribute : NewItemAttribute.BONUS_ATTRIBUTES) {
            if (!attributes.contains(bonusAttribute)) {
                continue;
            }
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
            NewItemLoreCreator.addBasicAttributes(true, setBonus, components);
            return this;
        }

        public Builder addBonusAttributes(Map<NewItemAttribute, Integer> bonusAttributeValues, NewItem.StarPieceBonus starPieceBonus) {
            return addBonusAttributes(true, bonusAttributeValues, starPieceBonus);
        }

        public Builder addBonusAttributes(boolean label, Map<NewItemAttribute, Integer> bonusAttributeValues, NewItem.StarPieceBonus starPieceBonus) {
            NewItemLoreCreator.addBonusAttributes(label, setBonus, components, bonusAttributeValues, starPieceBonus);
            return this;
        }

        public Builder addBonusAttributes() {
            return addBonusAttributes(true);
        }

        public Builder addBonusAttributes(boolean label) {
            return addBonusAttributes(label, EnumSet.allOf(NewItemAttribute.class));
        }

        public Builder addBonusAttributes(boolean label, Set<NewItemAttribute> attributes) {
            NewItemLoreCreator.addBonusAttributes(label, components, setBonus, attributes);
            return this;
        }

        public Builder addGems(NewItem newItem) {
            NewItemLoreCreator.addGems(components, newItem.getUnlockedGemSlots(), newItem.getMaxGemSlots(), newItem.getSocketedGems());
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
