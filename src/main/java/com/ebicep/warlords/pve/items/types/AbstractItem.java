package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.modifiers.ItemModifier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.RandomCollection;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractItem {

    public static void sendItemMessage(Player player, String message) {
        player.sendMessage(ChatColor.RED + "Items" + ChatColor.DARK_GRAY + " > " + message);
    }

    public static void sendItemMessage(Player player, ComponentBuilder message) {
        player.spigot().sendMessage(message.prependAndCreate(new ComponentBuilder(ChatColor.RED + "Items" + ChatColor.DARK_GRAY + " > ").create()));
    }

    private static double getAverageValue(double min, double max, double current) {
        return (current - min) / (max - min);
    }

    public static String getModifierCalculatedLore(
            ItemModifier[] blessings,
            ItemModifier[] curses,
            float modifierCalculated,
            boolean inverted
    ) {
        if (modifierCalculated > 0) {
            ItemModifier blessing = blessings[0];
            return WordWrap.wrapWithNewline(!inverted ? blessing.getDescriptionCalculated(modifierCalculated) : blessing.getDescriptionCalculatedInverted(modifierCalculated), 150);
        } else {
            ItemModifier curse = curses[0];
            return WordWrap.wrapWithNewline(!inverted ? curse.getDescriptionCalculated(modifierCalculated) : curse.getDescriptionCalculatedInverted(modifierCalculated), 150);
        }
    }

    protected UUID uuid = UUID.randomUUID();
    @Field("obtained_date")
    protected Instant obtainedDate = Instant.now();
    protected ItemType type;
    protected ItemTier tier;
    @Field("stat_pool")
    protected Map<BasicStatPool, Float> statPoolDistribution = new HashMap<>();
    @Transient
    protected HashMap<BasicStatPool, Integer> statPoolValues;
    protected int modifier;
    protected boolean favorite;

    public AbstractItem() {
    }

    public AbstractItem(ItemType type, ItemTier tier) {
        this(type, tier, tier.generateStatPool());
    }

    public AbstractItem(ItemType type, ItemTier tier, Set<BasicStatPool> statPool) {
        this.type = type;
        this.tier = tier;
        for (BasicStatPool stat : statPool) {
            this.statPoolDistribution.put(stat, (float) getRandomValueNormalDistribution());
        }
        if (tier != ItemTier.DELTA && tier != ItemTier.OMEGA) {
            bless(null);
        }
    }

    private static double getRandomValueNormalDistribution() {
        double mean = 0.5;
        double stdDev = 0.15;
        double random = ThreadLocalRandom.current().nextGaussian() * stdDev + mean;
        // Clamp to [0, 1]
        return Math.max(0, Math.min(1, random));
    }

    /**
     * Ran when the item is first created or found blessing applied
     * <p>
     * Either blesses/curses/does nothing to the item based on the tier
     * <p>
     * Based on bless/curse this will generate a random tier
     * <p>
     * Random tier generated adds to current modifier (blessing is positive, curse is negative)
     */
    public void bless(Integer tier) {
        Integer result = new RandomCollection<Integer>()
                .add(this.tier.blessedChance, 1)
                .add(this.tier.cursedChance, -1)
                .add(1 - this.tier.blessedChance - this.tier.cursedChance, 0)
                .next();
        switch (result) {
            case 1:
                this.modifier = Math.min(this.modifier + (tier == null ? ItemModifier.GENERATE_BLESSING.next() : tier), 5);
                break;
            case -1:
                this.modifier = Math.max(this.modifier - (tier == null ? ItemModifier.GENERATE_CURSE.next() : tier), -5);
                break;
        }
    }

    public ItemStack generateItemStack() {
        return generateItemBuilder().get();
    }

    public ItemBuilder generateItemBuilder() {
        ItemBuilder itemBuilder = getBaseItemBuilder();
        addStatPoolAndBlessing(itemBuilder);
        addItemScoreAndWeight(itemBuilder);
        return itemBuilder;
    }

    protected ItemBuilder getBaseItemBuilder() {
        return new ItemBuilder(getType().skull)
                .name(getItemName())
                .lore(
                        ChatColor.GRAY + "Tier: " + tier.getColoredName(),
                        ""
                );
    }

    protected void addStatPoolAndBlessing(ItemBuilder itemBuilder) {
        itemBuilder.addLore(getStatPoolLore());
        if (modifier != 0) {
            itemBuilder.addLore(
                    "",
                    getModifierCalculatedLore(getBlessings(), getCurses(), getModifierCalculated(), false)
            );
        }
    }

    protected void addItemScoreAndWeight(ItemBuilder itemBuilder) {
        String itemScoreString = getItemScoreString();
        itemBuilder.addLore(
                (itemScoreString != null ? "\n" + itemScoreString + "\n\n" : "\n") +
                        getWeightString()
        );
    }

    public String getItemName() {
        String name = "";
        ItemModifier itemModifier = getItemModifier();
        if (itemModifier != null) {
            name += (modifier > 0 ? ChatColor.GREEN : ChatColor.RED) + itemModifier.getName() + " ";
        } else {
            name += ChatColor.GRAY + "Normal ";
        }
        name += getType().name;
        return name;
    }

    public ItemModifier getItemModifier() {
        return getItemModifier(modifier);
    }

    public ItemModifier getItemModifier(int modifier) {
        if (modifier > 0) {
            return getBlessings()[modifier - 1];
        } else if (modifier < 0) {
            return getCurses()[-modifier - 1];
        } else {
            return null;
        }
    }

    public List<String> getStatPoolLore() {
        return BasicStatPool.getStatPoolLore(getStatPool(), false);
    }

    public <R extends Enum<R> & ItemModifier> R[] getBlessings() {
        return getType().getBlessings();
    }

    public <U extends Enum<U> & ItemModifier> U[] getCurses() {
        return getType().getCurses();
    }

    public float getModifierCalculated() {
        if (modifier == 0) {
            return 0;
        }
        return modifier * getItemModifier().getIncreasePerTier();
    }

    protected String getItemScoreString() {
        return ChatColor.GRAY + "Score: " + ChatColor.YELLOW + NumberFormat.formatOptionalHundredths(getItemScore()) + ChatColor.GRAY + "/" + ChatColor.GREEN + "100";
    }

    private String getWeightString() {
        return ChatColor.GRAY + "Weight: " + ChatColor.GOLD + ChatColor.BOLD + NumberFormat.formatOptionalHundredths(getWeight());
    }

    public HashMap<BasicStatPool, Integer> getStatPool() {
        if (statPoolValues == null) {
            statPoolValues = new HashMap<>();
            statPoolDistribution.forEach((stat, distribution) -> {
                BasicStatPool.StatRange statRange = BasicStatPool.STAT_RANGES.get(stat);
                double tieredDistribution = distribution + tier.statDistributionModifier;
                // clamp to [0, 1]
                tieredDistribution = Math.max(0, Math.min(1, tieredDistribution));
                int max = statRange.getMax() * stat.getDecimalPlace().value;
                int min = statRange.getMin() * stat.getDecimalPlace().value;
                int value = (int) ((max - min) * tieredDistribution + min);
                // floor value
                value = value / stat.getDecimalPlace().value * stat.getDecimalPlace().value;
                statPoolValues.put(stat, value);
            });
        }
        return statPoolValues;
    }

    public float getItemScore() {
        double sum = 0;
        for (Map.Entry<BasicStatPool, Float> statDistribution : statPoolDistribution.entrySet()) {
            sum += statDistribution.getValue();
        }
        return Math.round(sum / statPoolDistribution.size() * 10000) / 100f;
    }

    public int getWeight() {
        float itemScore = getWeightScore();
        ItemTier.WeightRange weightRange = tier.weightRange;
        int min = weightRange.getMin();
        int normal = weightRange.getNormal();
        int max = weightRange.getMax();

        if (itemScore <= 10) {
            return max;
        }
        if (45 <= itemScore && itemScore <= 55) {
            return normal;
        }
        if (itemScore >= 90) {
            return min;
        }

        int weight = max;
        // score: 10 - normal
        double midToTopIncrement = 35d / (max - normal - 1);
        for (double weightCheck = 10; weightCheck < 45; weightCheck += midToTopIncrement) {
            weight--;
            if (weightCheck <= itemScore && itemScore < weightCheck + midToTopIncrement) {
                return weight;
            }
            // safety check
            if (weight < 0) {
                return 1000;
            }
        }

        // account for normal weight
        weight--;

        // score: normal - 90
        double bottomToMidIncrement = 35d / (normal - min - 1);
        for (double weightCheck = 55; weightCheck < 90; weightCheck += bottomToMidIncrement) {
            weight--;
            if (weightCheck <= itemScore && itemScore < weightCheck + bottomToMidIncrement) {
                return weight;
            }
            // safety check
            if (weight < 0) {
                return 1000;
            }
        }
        return 1000;
    }

    public float getWeightScore() {
        double sum = 0;
        for (Map.Entry<BasicStatPool, Float> statDistribution : statPoolDistribution.entrySet()) {
            float value = statDistribution.getValue() + tier.statDistributionModifier;
            sum += Math.max(0, Math.min(1, value));
        }
        return Math.round(sum / statPoolDistribution.size() * 10000) / 100f;
    }

    public ItemType getType() {
        return type;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Instant getObtainedDate() {
        return obtainedDate;
    }

    public ItemTier getTier() {
        return tier;
    }

    public int getModifier() {
        return modifier;
    }

    public AbstractItem setModifier(int modifier) {
        this.modifier = modifier;
        return this;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
