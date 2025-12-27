package com.ebicep.warlords.pve.newitems;

import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.pve.newitems.setbonus.NewItemsSetBonus;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.JavaUtils;
import com.ebicep.warlords.util.java.RandomCollection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class NewItemsUtils {

    public static void reloadConfig() {
        for (NewItemsSetBonus value : NewItemsSetBonus.VALUES) {
            value.init();
        }
        NewItemsSetBonus.BY_TIER = Arrays
                .stream(NewItemsSetBonus.VALUES)
                .collect(Collectors.groupingBy(
                        NewItemsSetBonus::getTier,
                        () -> new EnumMap<>(NewItemTier.class),
                        Collectors.toSet()
                ));
        for (NewItemTier value : NewItemTier.VALUES) {
            value.init();
        }
    }

    @Nonnull
    public static NewItem generateRandomItem() {
        RandomCollection<NewItemTier> weightedTiers = new RandomCollection<>();
        for (NewItemTier value : NewItemTier.VALUES) {
            weightedTiers.add(value.getWeight(), value);
        }
        NewItemTier tier = weightedTiers.next();
        return generateRandomItem(tier);
    }

    @Nonnull
    public static NewItem generateRandomItem(NewItemTier tier) {
        Set<NewItemsSetBonus> setBonuses = NewItemsSetBonus.BY_TIER.get(tier);
        if (setBonuses == null || setBonuses.isEmpty()) {
            throw new IllegalStateException("No set bonuses found for tier: " + tier);
        }
        NewItemsSetBonus setBonus = JavaUtils.randomFromSet(setBonuses);
        return new NewItem(setBonus);
    }

    public static List<Component> getTotalStatsComponent(List<NewItem> items) {
        List<Component> components = new ArrayList<>();
        Map<NewItemAttribute, Float> totalAttributeValues = getTotalAttributeValues(items);
        int basicAttributeCount = 0;
        int bonusAttributeCount = 0;
        for (NewItemAttribute attribute : totalAttributeValues.keySet()) {
            if (attribute != NewItemAttribute.HEALTH && NewItemAttribute.BASIC_ATTRIBUTE_SET.contains(attribute)) {
                basicAttributeCount++;
            } else if (NewItemAttribute.BONUS_ATTRIBUTE_SET.contains(attribute)) {
                bonusAttributeCount++;
            }
        }

        if (totalAttributeValues.containsKey(NewItemAttribute.HEALTH)) {
            components.add(NewItemAttribute.HEALTH.formatValue(totalAttributeValues.get(NewItemAttribute.HEALTH), "+"));
            components.add(Component.empty());
        }
        if (basicAttributeCount > 0) {
            components.add(Component.text("Basic Attributes:", NamedTextColor.GRAY));
            for (NewItemAttribute basicAttribute : NewItemAttribute.BASIC_ATTRIBUTES) {
                if (basicAttribute == NewItemAttribute.HEALTH) {
                    continue;
                }
                Float value = totalAttributeValues.get(basicAttribute);
                if (value != null) {
                    components.add(basicAttribute.formatValue(value, "+"));
                }
            }
            components.add(Component.empty());
        }
        if (bonusAttributeCount > 0) {
            components.add(Component.text("Bonus Attributes:", NamedTextColor.GRAY));
            for (NewItemAttribute bonusAttribute : NewItemAttribute.BONUS_ATTRIBUTES) {
                Float value = totalAttributeValues.get(bonusAttribute);
                if (value != null) {
                    components.add(bonusAttribute.formatValue(value, "+"));
                }
            }
            components.add(Component.empty());
        }
        if (!components.isEmpty()) {
            components.removeLast();
        }
        return components;
    }

    public static Map<NewItemAttribute, Float> getTotalAttributeValues(List<NewItem> items) {
        Map<NewItemAttribute, Float> attributeValues = new HashMap<>();
        for (NewItem item : items) {
            Map<NewItemAttribute, Float> allAttributeValues = item.getAllAttributeValues();
            for (Map.Entry<NewItemAttribute, Float> entry : allAttributeValues.entrySet()) {
                attributeValues.merge(entry.getKey(), entry.getValue(), Float::sum);
            }
        }
        return attributeValues;
    }

    public static List<Component> getTotalSetsStatsComponent(List<NewItem> items) {
        List<Component> components = new ArrayList<>();
        Map<NewItemsSetBonus, Set<NewItemsSlot>> activeSets = getActiveSets(items);
        activeSets.forEach((setBonus, slots) -> {
            components.add(Component.text(setBonus.getName() + " Set [" + slots.size() + "/" + slots.size() + "]", NamedTextColor.GRAY));
            for (NewItemsSlot newItemsSlot : setBonus.getSlots()) {
                components.add(Component.text(" - " + setBonus.getName() + " " + newItemsSlot.getName(),
                        slots.contains(newItemsSlot) ? setBonus.getTier().getTextColor() : NamedTextColor.GRAY
                ));
            }
            boolean setActive = setBonus.getSlots().size() == slots.size();
            components.add(Component.empty());
            components.add(Component.text("Set Bonus: ", NamedTextColor.GRAY)
                                    .append(Component.text(setActive ? "[ACTIVE]" : "[INACTIVE]", setActive ? NamedTextColor.GREEN : NamedTextColor.RED)));
            components.addAll(setBonus.getDescriptionLore());
            components.add(Component.empty());
        });
        if (!components.isEmpty()) {
            components.removeLast();
        }
        return components;
    }

    @Nonnull
    public static Map<NewItemsSetBonus, Set<NewItemsSlot>> getActiveSets(List<NewItem> items) {
        Map<NewItemsSetBonus, Set<NewItemsSlot>> activeSets = new HashMap<>();
        for (NewItem item : items) {
            NewItemsSetBonus setBonus = item.getSetBonus();
            if (setBonus.isNoBonus()) {
                continue;
            }
            activeSets.computeIfAbsent(setBonus, k -> new HashSet<>()).add(item.getSlot());
        }
        return activeSets;
    }

    public static Component createStarComponent(TextColor textColor, int starCount) {
        if (starCount <= 0 || starCount > 6) {
            throw new IllegalArgumentException("starCount must be between 0 and 6");
        }
        ComponentBuilder builder = ComponentBuilder.create().text("[", NamedTextColor.GRAY);
        if (starCount == 6) {
            builder.text("❂".repeat(6), textColor);
        } else {
            builder.text("❂".repeat(starCount), textColor)
                   .text("❂".repeat(6 - starCount), NamedTextColor.DARK_GRAY);
        }
        builder.text("]", NamedTextColor.GRAY);
        return builder.build();
    }

}
