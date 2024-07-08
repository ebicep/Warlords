package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.modifiers.ItemModifier;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.mobs.Aspect;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.java.MathUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.RandomCollection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractItem {

    public static RandomCollection<Integer> MODIFIER_CHANCE = new RandomCollection<Integer>()
            .add(1, 0)
            .add(1, 1)
            .add(1, 2);

    public static void sendItemMessage(Player player, String message) {
        player.sendMessage(Component.text("Items", NamedTextColor.RED).append(ChatChannels.CHAT_ARROW).append(Component.text(message)));
    }

    public static void sendItemMessage(Player player, Component message) {
        player.sendMessage(Component.text("Items", NamedTextColor.RED).append(ChatChannels.CHAT_ARROW).append(message));
    }

    public static void sendItemMessage(WarlordsEntity player, Component message) {
        player.sendMessage(Component.text("Items", NamedTextColor.RED).append(ChatChannels.CHAT_ARROW).append(message));
    }

    private static double getAverageValue(double min, double max, double current) {
        return (current - min) / (max - min);
    }

    protected UUID uuid = UUID.randomUUID();
    @Field("obtained_date")
    protected Instant obtainedDate = Instant.now();
    protected ItemType type;
    protected ItemTier tier;
    @Field("stat_pool")
    protected Map<BasicStatPool, Float> statPoolDistribution = new HashMap<>();
    @Transient
    protected HashMap<BasicStatPool, Float> statPoolValues;
    @Deprecated
    protected int modifier;
    @Field("aspect_modifier_1")
    @Nullable
    protected Aspect aspectModifier1 = null;

    @Field("aspect_modifier_2")
    @Nullable
    protected Aspect aspectModifier2 = null;
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
        applyRandomModifier();
    }

    private static double getRandomValueNormalDistribution() {
        double mean = 0.5;
        double stdDev = 0.15;
        double random = ThreadLocalRandom.current().nextGaussian() * stdDev + mean;
        // Clamp to [0, 1]
        return Math.max(0, Math.min(1, random));
    }

    public void applyRandomModifier() {
        switch (MODIFIER_CHANCE.next()) {
            case 1 -> aspectModifier1 = Aspect.getRandomAspect(Collections.emptyList());
            case 2 -> {
                aspectModifier1 = Aspect.getRandomAspect(Collections.emptyList());
                aspectModifier2 = Aspect.getRandomAspect(Collections.singletonList(aspectModifier1));
            }
        }
    }

    public void applyRandomModifierOnly() {
        aspectModifier1 = null;
        aspectModifier2 = null;
        switch (MODIFIER_CHANCE.next()) {
            case 1 -> aspectModifier1 = Aspect.getRandomAspect(Collections.emptyList());
            case 2 -> {
                aspectModifier1 = Aspect.getRandomAspect(Collections.emptyList());
                aspectModifier2 = Aspect.getRandomAspect(Collections.singletonList(aspectModifier1));
            }
        }
    }

    @Override
    public abstract AbstractItem clone();

    /**
     * Ran when the item is first created or found blessing applied
     * <p>
     * Either blesses/curses/does nothing to the item based on the tier
     * <p>
     * Based on bless/curse this will generate a random tier
     * <p>
     * Random tier generated adds to current modifier (blessing is positive, curse is negative)
     */
    @Deprecated
    public void bless(Integer tier) {
        Integer result = new RandomCollection<Integer>()
                .add(this.tier.blessedChance, 1)
                .add(this.tier.cursedChance, -1)
                .add(1 - this.tier.blessedChance - this.tier.cursedChance, 0)
                .next();
        switch (result) {
            case 1 -> this.modifier = Math.min(this.modifier + (tier == null ? ItemModifier.GENERATE_BLESSING.next() : tier), 5);
            case -1 -> this.modifier = Math.max(this.modifier - (tier == null ? ItemModifier.GENERATE_CURSE.next() : tier), -5);
        }
    }

    public Component getHoverComponent() {
        return getItemName().hoverEvent(generateItemStack());
    }

    public Component getItemName() {
        String aspectName = Aspect.getAspectName(aspectModifier1, aspectModifier2);
        return Component.text((aspectName.isEmpty() ? "Normal" : aspectName) + " " + getType().name, getModifierColor());
    }

    public ItemStack generateItemStack() {
        return generateItemBuilder().get();
    }

    public ItemType getType() {
        return type;
    }

    protected TextColor getModifierColor() {
        return aspectModifier1 != null ? aspectModifier2 != null ? NamedTextColor.DARK_GREEN : NamedTextColor.GREEN : NamedTextColor.GRAY;
    }

    public ItemBuilder generateItemBuilder() {
        ItemBuilder itemBuilder = getBaseItemBuilder();
        addStatPoolAndModifier(itemBuilder, null);
        addItemScore(itemBuilder, false);
        if (isFavorite()) {
            itemBuilder.addLore(Component.empty(), Component.text("FAVORITE", NamedTextColor.LIGHT_PURPLE));
        }
        return itemBuilder;
    }

    protected ItemBuilder getBaseItemBuilder() {
        return new ItemBuilder(getItemStack())
                .name(getItemName())
                .lore(
                        Component.textOfChildren(
                                Component.text("Tier: ", NamedTextColor.GRAY),
                                tier.getColoredName()
                        ),
                        Component.empty()
                );
    }

    protected void addStatPoolAndModifier(ItemBuilder itemBuilder, BasicStatPool obfuscatedStat) {
        itemBuilder.addLore(getStatPoolLore(obfuscatedStat));
        if (obfuscatedStat != null) {
            itemBuilder.addLore(
                    Component.empty(),
                    Component.text("???????", NamedTextColor.GREEN),
                    Component.text("  ????", NamedTextColor.GREEN).append(Component.text(" ?????????", NamedTextColor.GRAY))
            );

            return;
        }
        if (aspectModifier1 != null) {
            itemBuilder.addLore(Component.empty());
            boolean doubleModifier = aspectModifier2 != null;
            addModifier(itemBuilder, aspectModifier1, doubleModifier ? tier.aspectModifierValues.dualModifier1() : tier.aspectModifierValues.singleModifier());
            if (doubleModifier) {
                addModifier(itemBuilder, aspectModifier2, tier.aspectModifierValues.dualModifier2());
            }
        }
    }

    protected void addItemScore(ItemBuilder itemBuilder, boolean obfuscated) {
        Component itemScoreString = getItemScoreString(obfuscated);
        itemBuilder.addLore(Component.empty());
        if (itemScoreString != null) {
            itemBuilder.addLore(itemScoreString);
        }
    }

    public boolean isFavorite() {
        return favorite;
    }

    protected ItemStack getItemStack() {
        return getType().skull;
    }

    public List<Component> getStatPoolLore(BasicStatPool obfuscatedStat) {
        return BasicStatPool.getStatPoolLore(getStatPool(), false, obfuscatedStat);
    }

    private void addModifier(ItemBuilder itemBuilder, Aspect aspectModifier, int perTier) {
        itemBuilder.addLore(Component.textOfChildren(
//                Component.text("- ", NamedTextColor.AQUA),
                Component.text(aspectModifier.name, aspectModifier.textColor)
        ));
        itemBuilder.addLore(
                Component.textOfChildren(
                        Component.text("  ", NamedTextColor.AQUA),
                        getType().getModifierDescriptionCalculated(perTier)
                )
        );
    }

    protected Component getItemScoreString(boolean obfuscated) {
        return Component.text("Score: ", NamedTextColor.GRAY)
                        .append(Component.text(obfuscated ? "???" : NumberFormat.formatOptionalHundredths(getItemScore()), NamedTextColor.YELLOW))
                        .append(Component.text("/"))
                        .append(Component.text("100"));
    }

    public HashMap<BasicStatPool, Float> getStatPool() {
        if (statPoolValues == null) {
            statPoolValues = new HashMap<>();
            statPoolDistribution.forEach((stat, distribution) -> {
                BasicStatPool.StatRange statRange = BasicStatPool.STAT_RANGES.get(stat);
                float tieredDistribution = distribution + tier.statDistributionModifier;
                // clamp to [0, 1]
                tieredDistribution = Math.max(0, Math.min(1, tieredDistribution));
                float max = statRange.max() * stat.getDecimalPlace().value;
                float min = statRange.min() * stat.getDecimalPlace().value;
                float value = MathUtils.lerp(min, max, tieredDistribution);
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

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Deprecated
    protected void addItemScoreAndWeight(ItemBuilder itemBuilder, boolean obfuscated) {
        Component itemScoreString = getItemScoreString(obfuscated);
        itemBuilder.addLore(Component.empty());
        if (itemScoreString != null) {
            itemBuilder.addLore(
                    itemScoreString,
                    Component.empty(),
                    getWeightString(obfuscated)
            );
        } else {
            itemBuilder.addLore(getWeightString(obfuscated));
        }
    }

    private Component getWeightString(boolean obfuscated) {
        return Component.text("Weight: ", NamedTextColor.GRAY)
                        .append(Component.text(obfuscated ? "???" : NumberFormat.formatOptionalHundredths(getWeight()), NamedTextColor.GOLD, TextDecoration.BOLD));
    }

    public int getWeight() {
        float itemScore = getWeightScore();
        ItemTier.WeightRange weightRange = tier.weightRange;
        int min = weightRange.min();
        int normal = weightRange.normal();
        int max = weightRange.max();

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

    private float getWeightScore() {
        double sum = 0;
        for (Map.Entry<BasicStatPool, Float> statDistribution : statPoolDistribution.entrySet()) {
            float value = statDistribution.getValue() + tier.statDistributionModifier;
            sum += Math.max(0, Math.min(1, value));
        }
        return Math.round(sum / statPoolDistribution.size() * 10000) / 100f;
    }

    @Deprecated
    protected void addStatPoolAndBlessing(ItemBuilder itemBuilder, BasicStatPool obfuscatedStat) {
        itemBuilder.addLore(getStatPoolLore(obfuscatedStat));
        if (modifier != 0) {
            itemBuilder.addLore(Component.empty());
            itemBuilder.addLore(getModifierCalculatedLore(getBlessings(), getCurses(), getModifierCalculated(), false));
        }
    }

    public static List<Component> getModifierCalculatedLore(
            ItemModifier[] blessings,
            ItemModifier[] curses,
            float modifierCalculated,
            boolean inverted
    ) {
        if (modifierCalculated > 0) {
            ItemModifier blessing = blessings[0];
            return WordWrap.wrap(!inverted ? blessing.getDescriptionCalculated(modifierCalculated) : blessing.getDescriptionCalculatedInverted(
                    modifierCalculated), 150);
        } else {
            ItemModifier curse = curses[0];
            return WordWrap.wrap(!inverted ? curse.getDescriptionCalculated(modifierCalculated) : curse.getDescriptionCalculatedInverted(
                    modifierCalculated), 150);
        }
    }

    public <R extends Enum<R> & ItemModifier> R[] getBlessings() {
        return getType().getBlessings();
    }

    public <U extends Enum<U> & ItemModifier> U[] getCurses() {
        return getType().getCurses();
    }

    @Deprecated
    public float getModifierCalculated() {
        if (modifier == 0) {
            return 0;
        }
        return modifier * getItemModifier().getIncreasePerTier();
    }

    @Deprecated
    public ItemModifier getItemModifier() {
        return getItemModifier(modifier);
    }

    @Deprecated
    public ItemModifier getItemModifier(int modifier) {
        if (modifier > 0) {
            return getBlessings()[modifier - 1];
        } else if (modifier < 0) {
            return getCurses()[-modifier - 1];
        } else {
            return null;
        }
    }

    public UUID getUUID() {
        return uuid;
    }

    public Instant getObtainedDate() {
        return obtainedDate;
    }

    public void setObtainedDate(Instant obtainedDate) {
        this.obtainedDate = obtainedDate;
    }

    public ItemTier getTier() {
        return tier;
    }

    @Deprecated
    public int getModifier() {
        return modifier;
    }

    @Deprecated
    public AbstractItem setModifier(int modifier) {
        this.modifier = modifier;
        return this;
    }

    @Nullable
    public Aspect getAspectModifier1() {
        return aspectModifier1;
    }

    @Nullable
    public Aspect getAspectModifier2() {
        return aspectModifier2;
    }

}
