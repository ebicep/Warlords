package com.ebicep.warlords.pve.newitems.tiers;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.util.java.NamedEnum;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

import java.util.LinkedHashMap;
import java.util.Map;

public enum NewItemTier implements ItemTier, NamedEnum {

    COMMON(new Common(), 0),
    RARE(new Rare(), 0),
    EPIC(new Epic(), 1),
    SOVEREIGN(new Sovereign(), 2),
    LEGENDARY(new Legendary(), 2),
    ASCENDANT(new Ascendant(), 3),

    ;

    public static final NewItemTier[] VALUES = values();
    /**
     * Paid once per socket, regardless of the item's tier or how many sockets are already unlocked.
     */
    public static final LinkedHashMap<Spendable, Long> GEM_SLOT_UNLOCK_COST = new LinkedHashMap<>() {{
        put(Currencies.LEGENDARY_STAR_PIECE, 3L);
        put(Currencies.LEGEND_FRAGMENTS, 5_000L);
    }};

    private final ItemTier itemTier;
    private final int maxGemSlots;

    NewItemTier(ItemTier itemTier, int maxGemSlots) {
        this.itemTier = itemTier;
        this.maxGemSlots = maxGemSlots;
    }

    public int getMaxGemSlots() {
        return maxGemSlots;
    }

    @Override
    public Material getTerracotaMaterial() {
        return itemTier.getTerracotaMaterial();
    }

    @Override
    public void init() {
        itemTier.init();
    }

    @Override
    public TextColor getTextColor() {
        return itemTier.getTextColor();
    }

    @Override
    public Component getStarComponent() {
        return itemTier.getStarComponent();
    }

    public NewItemTier next() {
        return VALUES[this.ordinal() + 1 % VALUES.length];
    }

    @Override
    public String getName() {
        return itemTier.getName();
    }

    @Override
    public int getWeight() {
        return itemTier.getWeight();
    }

    @Override
    public int bonusAttributes() {
        return itemTier.bonusAttributes();
    }

    @Override
    public Map<NewItemAttribute, Pair<Float, Float>> getBonusAttributeRanges() {
        return itemTier.getBonusAttributeRanges();
    }

    @Override
    public void setBonusAttributeRanges(Map<NewItemAttribute, Pair<Float, Float>> bonusAttributeRanges) {
        itemTier.setBonusAttributeRanges(bonusAttributeRanges);
    }

    @Override
    public String getConfigFieldName() {
        return itemTier.getConfigFieldName();
    }

    public ItemTier getType() {
        return itemTier;
    }

    @Override
    public Map<Integer, Map<Spendable, Long>> getRerollCost() {
        return itemTier.getRerollCost();
    }

    @Override
    public void setRerollCost(Map<Integer, Map<Spendable, Long>> rerollCost) {
        itemTier.setRerollCost(rerollCost);
    }

    @Override
    public Map<Integer, Map<Spendable, Long>> getLockScrollRerollCost() {
        return itemTier.getLockScrollRerollCost();
    }

    @Override
    public void setLockScrollRerollCost(Map<Integer, Map<Spendable, Long>> lockScrollRerollCost) {
        itemTier.setLockScrollRerollCost(lockScrollRerollCost);
    }

    @Override
    public Map<Spendable, Long> getCraftCost() {
        return itemTier.getCraftCost();
    }

    @Override
    public void setCraftCost(Map<Spendable, Long> craftCost) {
        itemTier.setCraftCost(craftCost);
    }

}