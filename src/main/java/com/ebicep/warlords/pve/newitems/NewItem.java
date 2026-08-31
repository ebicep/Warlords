package com.ebicep.warlords.pve.newitems;

import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.StarPieces;
import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.pve.newitems.gems.Gem;
import com.ebicep.warlords.pve.newitems.setbonus.NewItemsSetBonus;
import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.JavaUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.TypeAlias;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@TypeAlias("new_item")
public class NewItem {

    private static final double SCORE_ROLL_EXPONENT = 1.1;

    private Instant creationTime = Instant.now();
    private UUID uuid = UUID.randomUUID();
    private Map<NewItemAttribute, Byte> bonusAttributeDistribution;
    private NewItemsSlot slot;
    private NewItemsSetBonus setBonus;
    private boolean isFavorite = false;
    private List<Map<Spendable, Long>> rerollCostsHistory = new ArrayList<>();
    private List<StarPieceBonus> starPieceBonuses = new ArrayList<>();
    private int unlockedGemSlots = 0;
    /**
     * Indexed by slot, so a null entry is an unlocked but empty socket.
     */
    private List<Gem> socketedGems = new ArrayList<>();

    public NewItem() {
        // for deserialization
    }

    public NewItem(@NotNull NewItemsSetBonus setBonus) {
        this(setBonus, JavaUtils.randomFromList(setBonus.getSlots()));
    }

    public NewItem(@NotNull NewItemsSetBonus setBonus, @NotNull NewItemsSlot slot) {
        if (!setBonus.getSlots().contains(slot)) {
            throw new IllegalArgumentException(slot + " is not part of set " + setBonus);
        }
        this.setBonus = setBonus;
        this.slot = slot;
        NewItemTier tier = setBonus.getTier();
        NewItemAttribute[] bonusAttributes = JavaUtils.pickRandom(NewItemAttribute.BONUS_ATTRIBUTES, tier.bonusAttributes());
        this.bonusAttributeDistribution = new EnumMap<>(NewItemAttribute.class);
        for (NewItemAttribute bonusAttribute : bonusAttributes) {
            this.bonusAttributeDistribution.put(bonusAttribute, rollAttributeDistribution(ThreadLocalRandom.current()));
        }
    }

    public NewItem(@NotNull NewItemsSetBonus setBonus, @NotNull Random random) {
        this.setBonus = setBonus;
        this.slot = setBonus.getSlots().get(random.nextInt(setBonus.getSlots().size()));
        NewItemTier tier = setBonus.getTier();
        List<NewItemAttribute> bonusAttributes = new ArrayList<>(Arrays.asList(NewItemAttribute.BONUS_ATTRIBUTES));
        Collections.shuffle(bonusAttributes, random);
        this.bonusAttributeDistribution = new EnumMap<>(NewItemAttribute.class);
        for (int i = 0; i < tier.bonusAttributes(); i++) {
            this.bonusAttributeDistribution.put(bonusAttributes.get(i), rollAttributeDistribution(random));
        }
    }

    private static byte rollAttributeDistribution(Random random) {
        return (byte) Math.min(100, (int) (Math.pow(random.nextDouble(), SCORE_ROLL_EXPONENT) * 101));
    }

    public NewItem(@NotNull NewItem source) {
        this.bonusAttributeDistribution = new EnumMap<>(source.bonusAttributeDistribution);
        this.slot = source.slot;
        this.setBonus = source.setBonus;
        this.rerollCostsHistory = new ArrayList<>();
        for (Map<Spendable, Long> rerollCost : source.rerollCostsHistory) {
            this.rerollCostsHistory.add(new HashMap<>(rerollCost));
        }
        this.starPieceBonuses = new ArrayList<>(source.starPieceBonuses);
        this.unlockedGemSlots = source.unlockedGemSlots;
        this.socketedGems = new ArrayList<>(source.socketedGems);
    }

    public void reroll(EnumSet<NewItemAttribute> lockedAttributes) {
        for (NewItemAttribute newItemAttribute : this.bonusAttributeDistribution.keySet()) {
            if (!lockedAttributes.contains(newItemAttribute)) {
                this.bonusAttributeDistribution.put(newItemAttribute, rollAttributeDistribution(ThreadLocalRandom.current()));
            }
        }
    }

    public Component getHoverComponent() {
        return getName().hoverEvent(getItemBuilder().get());
    }

    public Component getName() {
        return Component.text(getStringName(), getTier().getTextColor());
    }

    public ItemBuilder getItemBuilder() {
        return getItemBuilder(null, null);
    }

    public ItemBuilder getItemBuilder(NewItemsManager itemsManager, NewItemLoadout loadout) {
        List<Component> lore = new NewItemLoreCreator.Builder(this)
                .addStarComponent()
                .addBasicAttributes()
                .addBonusAttributes(getBonusAttributeValues(), getStarPieceBonus())
                .addGems(this)
                .addSetBonus(itemsManager, loadout)
                .build();
        lore.add(Component.empty());
        lore.add(Component.text("Score: ", NamedTextColor.GRAY)
                          .append(Component.text(NumberFormat.formatOptionalHundredths(getItemScore()), NamedTextColor.YELLOW))
                          .append(Component.text("/100", NamedTextColor.GRAY)));
        lore.add(Component.empty());
        lore.add(Component.text(getTier().getName() + " " + slot.getName(), getTier().getTextColor()));
        lore.add(Component.text("REROLL [" + rerollCostsHistory.size() + "/" + NewItemRerollCost.MAX_REROLLS + "]", NamedTextColor.DARK_GRAY)); // TODO ?
        if (isFavorite) {
            lore.add(Component.text("FAVORITE", NamedTextColor.LIGHT_PURPLE));
        }
        return new ItemBuilder(getItemStack())
                .name(getName())
                .lore(lore);
    }

    public Map<NewItemAttribute, Integer> getBonusAttributeValues() {
        StarPieceBonus starPieceBonus = getStarPieceBonus();
        Map<NewItemAttribute, Integer> attributeValues = new EnumMap<>(NewItemAttribute.class);
        bonusAttributeDistribution.forEach((attribute, distributionPercent) -> {
            Pair<Float, Float> range = getTier().getBonusAttributeRanges().get(attribute);
            if (range != null) {
                int bonusValue = (int) Math.ceil(range.getA() + (range.getB() - range.getA()) * (distributionPercent / 100f));
                if (starPieceBonus != null && starPieceBonus.attribute() == attribute) {
                    bonusValue = (int) Math.ceil(bonusValue * (1 + starPieceBonus.starPiece().starPieceBonusValue / 100f));
                }
                attributeValues.put(attribute, attributeValues.getOrDefault(attribute, 0) + bonusValue);
            }
        });
        return attributeValues;
    }

    public float getItemScore() {
        if (bonusAttributeDistribution == null || bonusAttributeDistribution.isEmpty()) {
            return 0;
        }
        double average = bonusAttributeDistribution.values()
                                                   .stream()
                                                   .mapToInt(Byte::intValue)
                                                   .average()
                                                   .orElse(0);
        return Math.round(average * 100) / 100f;
    }

    public NewItemTier getTier() {
        return setBonus.getTier();
    }

    public String getStringName() {
        return setBonus.getName() + " " + slot.getName();
    }

    public Set<NewItemAttribute> getBonusAttributes() {
        return bonusAttributeDistribution.keySet();
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
        for (Gem gem : socketedGems) {
            if (gem != null) {
                attributes.add(gem.getAttribute());
            }
        }
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
        for (Gem gem : socketedGems) {
            if (gem != null) {
                attributeValues.merge(gem.getAttribute(), gem.getValue(), Float::sum);
            }
        }
        return attributeValues;
    }

    public int getMaxGemSlots() {
        return getTier().getMaxGemSlots();
    }

    public int getUnlockedGemSlots() {
        return unlockedGemSlots;
    }

    public boolean canUnlockGemSlot() {
        return unlockedGemSlots < getMaxGemSlots();
    }

    public void unlockGemSlot() {
        if (!canUnlockGemSlot()) {
            throw new IllegalStateException(getStringName() + " has no gem slots left to unlock");
        }
        unlockedGemSlots++;
    }

    public List<Gem> getSocketedGems() {
        return socketedGems;
    }

    @Nullable
    public Gem getSocketedGem(int slot) {
        return slot < 0 || slot >= socketedGems.size() ? null : socketedGems.get(slot);
    }

    public void setSocketedGem(int slot, @Nullable Gem gem) {
        if (slot < 0 || slot >= unlockedGemSlots) {
            throw new IllegalArgumentException("Gem slot " + slot + " is not unlocked on " + getStringName());
        }
        while (socketedGems.size() <= slot) {
            socketedGems.add(null);
        }
        socketedGems.set(slot, gem);
    }

    public UUID getUUID() {
        return uuid;
    }

    public List<Map<Spendable, Long>> getRerollCostsHistory() {
        return rerollCostsHistory;
    }

    @Nullable
    public StarPieceBonus getStarPieceBonus() {
        return starPieceBonuses.isEmpty() ? null : starPieceBonuses.getLast();
    }

    public List<StarPieceBonus> getStarPieceBonuses() {
        return starPieceBonuses;
    }

    public record StarPieceBonus(StarPieces starPiece, NewItemAttribute attribute, Instant appliedTime) {

        public StarPieceBonus(StarPieces starPiece, NewItemAttribute attribute) {
            this(starPiece, attribute, Instant.now());
        }

    }

}
