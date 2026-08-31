package com.ebicep.warlords.pve.newitems.gems;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.NamedEnum;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A socketable gem, identified by its {@link GemType} and {@link GemTier}. Gems are fungible, so a player owns a count
 * of each rather than individual instances, which is what lets duplicates be merged into higher tiers.
 */
public enum Gem implements Spendable, NamedEnum {

    IMPAIRMENT_I(GemType.IMPAIRMENT, GemTier.ONE),
    IMPAIRMENT_II(GemType.IMPAIRMENT, GemTier.TWO),
    IMPAIRMENT_III(GemType.IMPAIRMENT, GemTier.THREE),
    IMPAIRMENT_IV(GemType.IMPAIRMENT, GemTier.FOUR),

    ALLEVIATION_I(GemType.ALLEVIATION, GemTier.ONE),
    ALLEVIATION_II(GemType.ALLEVIATION, GemTier.TWO),
    ALLEVIATION_III(GemType.ALLEVIATION, GemTier.THREE),
    ALLEVIATION_IV(GemType.ALLEVIATION, GemTier.FOUR),

    SURGES_I(GemType.SURGES, GemTier.ONE),
    SURGES_II(GemType.SURGES, GemTier.TWO),
    SURGES_III(GemType.SURGES, GemTier.THREE),
    SURGES_IV(GemType.SURGES, GemTier.FOUR),

    NOURISHMENT_I(GemType.NOURISHMENT, GemTier.ONE),
    NOURISHMENT_II(GemType.NOURISHMENT, GemTier.TWO),
    NOURISHMENT_III(GemType.NOURISHMENT, GemTier.THREE),
    NOURISHMENT_IV(GemType.NOURISHMENT, GemTier.FOUR),

    SPEED_I(GemType.SPEED, GemTier.ONE),
    SPEED_II(GemType.SPEED, GemTier.TWO),
    SPEED_III(GemType.SPEED, GemTier.THREE),
    SPEED_IV(GemType.SPEED, GemTier.FOUR),

    ;

    public static final Gem[] VALUES = values();
    /**
     * Paid on top of {@link GemTier#MERGE_AMOUNT} gems of the tier being merged.
     */
    public static final LinkedHashMap<Spendable, Long> MERGE_COST = new LinkedHashMap<>() {{
        put(Currencies.SYNTHETIC_SHARD, 1_000L);
        put(Currencies.COIN, 100_000L);
    }};

    private static final Map<GemType, Map<GemTier, Gem>> BY_TYPE_AND_TIER = new EnumMap<>(GemType.class);

    static {
        for (Gem gem : VALUES) {
            BY_TYPE_AND_TIER.computeIfAbsent(gem.type, k -> new EnumMap<>(GemTier.class)).put(gem.tier, gem);
        }
    }

    public static Gem of(GemType type, GemTier tier) {
        return BY_TYPE_AND_TIER.get(type).get(tier);
    }

    private final GemType type;
    private final GemTier tier;
    private final String name;
    private final ItemStack item;

    Gem(GemType type, GemTier tier) {
        this.type = type;
        this.tier = tier;
        this.name = type.getName() + " " + tier.getName();
        this.item = new ItemStack(type.getMaterial());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TextColor getTextColor() {
        return tier.getTextColor();
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Override
    public void addToPlayer(DatabasePlayer databasePlayer, long amount) {
        databasePlayer.getPveStats().addGems(this, amount);
    }

    @Override
    public Long getFromPlayer(DatabasePlayer databasePlayer) {
        return databasePlayer.getPveStats().getGems(this);
    }

    @Override
    public boolean pluralIncludeS() {
        return false;
    }

    public GemType getType() {
        return type;
    }

    public GemTier getTier() {
        return tier;
    }

    public NewItemAttribute getAttribute() {
        return type.getAttribute();
    }

    public float getValue() {
        return type.getValuePerTier() * tier.getMultiplier();
    }

    /**
     * @return the gem this one merges into, or null if it is already the highest tier
     */
    @Nullable
    public Gem getMergeResult() {
        GemTier nextTier = tier.next();
        return nextTier == null ? null : of(type, nextTier);
    }

    /**
     * @return the full merge cost, including the gems consumed, or null if this gem cannot be merged
     */
    @Nullable
    public LinkedHashMap<Spendable, Long> getMergeCost() {
        if (getMergeResult() == null) {
            return null;
        }
        LinkedHashMap<Spendable, Long> cost = new LinkedHashMap<>();
        cost.put(this, (long) GemTier.MERGE_AMOUNT);
        cost.putAll(MERGE_COST);
        return cost;
    }

    public Component getColoredName() {
        return Component.text(name, getTextColor());
    }

    /**
     * @return the attribute this gem grants, formatted the same way item attributes are
     */
    public Component getAttributeComponent() {
        return getAttribute().formatValue(getValue(), "+");
    }

    public ItemStack getDisplayItem() {
        List<Component> lore = new ArrayList<>();
        lore.add(getAttributeComponent());
        lore.add(Component.empty());
        lore.add(Component.text("Tier " + tier.getName() + " Gem", NamedTextColor.GRAY));
        return new ItemBuilder(item)
                .name(getColoredName())
                .lore(lore)
                .get();
    }

}
