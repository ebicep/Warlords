package com.ebicep.warlords.pve.newitems;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.pve.newitems.setbonus.NewItemsSetBonus;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.java.JavaUtils;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.TypeAlias;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@TypeAlias("new_item")
public class NewItem {

    public static void sendItemMessage(Player player, String message) {
        player.sendMessage(Component.text("Items", NamedTextColor.RED).append(ChatChannels.CHAT_ARROW).append(Component.text(message)));
    }

    public static void sendItemMessage(Player player, Component message) {
        player.sendMessage(Component.text("Items", NamedTextColor.RED).append(ChatChannels.CHAT_ARROW).append(message));
    }

    public static void sendItemMessage(WarlordsEntity player, Component message) {
        player.sendMessage(Component.text("Items", NamedTextColor.RED).append(ChatChannels.CHAT_ARROW).append(message));
    }

    private Instant creationTime = Instant.now();
    private UUID uuid = UUID.randomUUID();
    private Map<NewItemAttribute, Byte> bonusAttributeDistribution;
    private NewItemsSlot slot;
    private NewItemsSetBonus setBonus;
    private boolean isFavorite = false;

    public NewItem() {
        // for deserialization
    }

    public NewItem(@NotNull NewItemsSetBonus setBonus) {
        this.setBonus = setBonus;
        this.slot = JavaUtils.randomFromList(setBonus.getSlots());
        NewItemTier tier = setBonus.getTier();
        NewItemAttribute[] bonusAttributes = JavaUtils.pickRandom(NewItemAttribute.BONUS_ATTRIBUTES, tier.bonusAttributes());
        this.bonusAttributeDistribution = new EnumMap<>(NewItemAttribute.class);
        for (NewItemAttribute bonusAttribute : bonusAttributes) {
            this.bonusAttributeDistribution.put(bonusAttribute, (byte) ThreadLocalRandom.current().nextInt(0, 101));
        }
    }

    public Component getHoverComponent() {
        return getName().hoverEvent(getItemBuilder().get());
    }

    public NewItemTier getTier() {
        return setBonus.getTier();
    }

    public Map<NewItemAttribute, Integer> getBonusAttributeValues() {
        Map<NewItemAttribute, Integer> attributeValues = new EnumMap<>(NewItemAttribute.class);
        bonusAttributeDistribution.forEach((attribute, distributionPercent) -> {
            Pair<Short, Short> range = getTier().bonusAttributeRanges().get(attribute);
            if (range != null) {
                int bonusValue = (int) Math.ceil(range.getA() + (range.getB() - range.getA()) * (distributionPercent / 100f));
                attributeValues.put(attribute, attributeValues.getOrDefault(attribute, 0) + bonusValue);
            }
        });
        return attributeValues;
    }

    public Component getName() {
        return Component.text(getStringName(), getTier().getTextColor());
    }

    // TODO pass in loadout
    public ItemBuilder getItemBuilder() {
        List<Component> lore = new ArrayList<>();
        lore.add(getTier().getStarComponent());
        lore.add(Component.empty());
        Map<NewItemAttribute, Float> basicAttributes = setBonus.getAttributes();
        boolean onlyHealth = basicAttributes.size() == 1 && basicAttributes.containsKey(NewItemAttribute.HEALTH);
        if (basicAttributes.containsKey(NewItemAttribute.HEALTH)) {
            lore.add(NewItemAttribute.HEALTH.formatValue(basicAttributes.get(NewItemAttribute.HEALTH)));
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
                    lore.add(basicAttribute.formatValue(value));
                }
            }
            lore.add(Component.empty());
        }
        Map<NewItemAttribute, Integer> bonusAttributeValues = getBonusAttributeValues();
        if (!bonusAttributeValues.isEmpty()) {
            lore.add(Component.text("Bonus Attributes:", NamedTextColor.GRAY));
            for (NewItemAttribute bonusAttribute : NewItemAttribute.BONUS_ATTRIBUTES) {
                Integer value = bonusAttributeValues.get(bonusAttribute);
                if (value != null) {
                    lore.add(bonusAttribute.formatValue(value));
                }
            }
            lore.add(Component.empty());
        }

        if (!setBonus.isNoBonus()) {
            List<NewItemsSlot> slots = setBonus.getSlots();
            lore.add(Component.text(setBonus.getName() + " Set [" + "?" + "/" + slots.size() + "]", NamedTextColor.GRAY));
            for (NewItemsSlot newItemsSlot : slots) {
                lore.add(Component.text(" - " + setBonus.getName() + " " + newItemsSlot.getName(), getTier().getTextColor()));
            }
            lore.add(Component.empty());
            lore.add(Component.text("Set Bonus:", NamedTextColor.GRAY));
            lore.addAll(setBonus.getDescriptionLore());
            lore.add(Component.empty());
        }

        lore.add(Component.text(getTier().getName() + " " + slot.getName(), getTier().getTextColor()));
        return new ItemBuilder(getItemStack())
                .name(getName())
                .lore(lore);
    }

    public String getStringName() {
        return setBonus.getName() + " " + slot.getName();
    }

    public ItemStack getItemStack() {
        return slot.getItemStack();
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public NewItemsSlot getSlot() {
        return slot;
    }

    public NewItemsSetBonus getSetBonus() {
        return setBonus;
    }

    public Set<NewItemAttribute> getAllAttributes() {
        Set<NewItemAttribute> attributes = new HashSet<>(setBonus.getAttributes().keySet());
        attributes.addAll(getBonusAttributeValues().keySet());
        return attributes;
    }

    public Map<NewItemAttribute, Float> getAllAttributeValues() {
        Map<NewItemAttribute, Float> attributeValues = new EnumMap<>(NewItemAttribute.class);
        setBonus.getAttributes().forEach((attribute, value) ->
                attributeValues.put(attribute, attributeValues.getOrDefault(attribute, 0f) + value)
        );
        getBonusAttributeValues().forEach((attribute, value) ->
                attributeValues.put(attribute, attributeValues.getOrDefault(attribute, 0f) + value)
        );
        return attributeValues;
    }

    public UUID getUUID() {
        return uuid;
    }

}
