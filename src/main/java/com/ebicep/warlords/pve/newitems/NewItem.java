package com.ebicep.warlords.pve.newitems;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.Spendable;
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
    private List<Map<Spendable, Long>> rerollCostsHistory = new ArrayList<>();

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

    public void reroll(EnumSet<NewItemAttribute> lockedAttributes) {
        for (NewItemAttribute newItemAttribute : this.bonusAttributeDistribution.keySet()) {
            if (!lockedAttributes.contains(newItemAttribute)) {
                this.bonusAttributeDistribution.put(newItemAttribute, (byte) ThreadLocalRandom.current().nextInt(0, 101));
            }
        }
    }

    public Component getHoverComponent() {
        return getName().hoverEvent(getItemBuilder().get());
    }

    public NewItemTier getTier() {
        return setBonus.getTier();
    }

    public ItemBuilder getItemBuilder() {
        return getItemBuilder(null, null);
    }

    public Component getName() {
        return Component.text(getStringName(), getTier().getTextColor());
    }

    public ItemBuilder getItemBuilder(NewItemsManager itemsManager, NewItemLoadout loadout) {
        List<Component> lore = new NewItemLoreCreator.Builder(this)
                .addStarComponent()
                .addBasicAttributes()
                .addBonusAttributes(getBonusAttributeValues())
                .addSetBonus(itemsManager, loadout)
                .build();
        lore.add(Component.empty());
        lore.add(Component.text(getTier().getName() + " " + slot.getName(), getTier().getTextColor()));
        lore.add(Component.text("REROLL [" + rerollCostsHistory.size() + "]", NamedTextColor.DARK_GRAY)); // TODO ?
        return new ItemBuilder(getItemStack())
                .name(getName())
                .lore(lore);
    }

    public Map<NewItemAttribute, Float> getBonusAttributeValues() {
        Map<NewItemAttribute, Float> attributeValues = new EnumMap<>(NewItemAttribute.class);
        bonusAttributeDistribution.forEach((attribute, distributionPercent) -> {
            Pair<Float, Float> range = getTier().getBonusAttributeRanges().get(attribute);
            if (range != null) {
                int bonusValue = (int) Math.ceil(range.getA() + (range.getB() - range.getA()) * (distributionPercent / 100f));
                attributeValues.put(attribute, attributeValues.getOrDefault(attribute, 0f) + bonusValue);
            }
        });
        return attributeValues;
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

    public List<Map<Spendable, Long>> getRerollCostsHistory() {
        return rerollCostsHistory;
    }

}
