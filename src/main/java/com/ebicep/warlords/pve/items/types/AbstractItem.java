package com.ebicep.warlords.pve.items.types;

import com.ebicep.warlords.pve.items.ItemTier;
import com.ebicep.warlords.pve.items.modifiers.ItemModifier;
import com.ebicep.warlords.pve.items.statpool.ItemStatPool;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.RandomCollection;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractItem<
        T extends Enum<T> & ItemStatPool<T>,
        R extends Enum<R> & ItemModifier<R>,
        U extends Enum<U> & ItemModifier<U>> {

    private static double getAverageValue(double min, double max, double current) {
        return (current - min) / (max - min);
    }

    protected UUID uuid;
    @Field("obtained_date")
    protected Instant obtainedDate = Instant.now();
    protected ItemTier tier;
    @Field("stat_pool")
    protected Map<T, Integer> statPool = new HashMap<>();
    protected int modifier;

    public AbstractItem(UUID uuid, ItemTier tier, Set<T> statPool) {
        this.uuid = uuid;
        this.tier = tier;
        HashMap<T, ItemTier.StatRange> tierStatRanges = getTierStatRanges();
        for (T t : statPool) {
            this.statPool.put(t, tierStatRanges.get(t).generateValue());
        }
        Integer result = new RandomCollection<Integer>()
                .add(tier.blessedChance, 1)
                .add(tier.cursedChance, -1)
                .add(1 - tier.blessedChance - tier.cursedChance, 0)
                .next();
        switch (result) {
            case 1:
                this.modifier = ItemModifier.BLESSING_TIER_CHANCE.next();
                break;
            case -1:
                this.modifier = -ItemModifier.CURSE_TIER_CHANCE.next();
                break;
        }
    }

    public abstract HashMap<T, ItemTier.StatRange> getTierStatRanges();

    public ItemStack generateItemStack() {
        ItemBuilder itemBuilder = new ItemBuilder(Material.SKULL_ITEM)
                .name(getName())
                .lore("")
                .addLore(getStatPoolLore())
                .addLore(
                        "",
                        getItemScoreString(),
                        getWeightString()
                );
        if (modifier != 0) {
            if (modifier > 0) {
                R blessing = getBlessings()[modifier - 1];
                itemBuilder.addLore(
                        "",
                        ChatColor.GREEN + "Blessed (" + blessing.getName() + ")",
                        WordWrap.wrapWithNewline(blessing.getDescription(), 150),
                        ""
                );
            } else {
                U curse = getCurses()[-modifier - 1];
                itemBuilder.addLore(
                        "",
                        ChatColor.RED + "Cursed (" + curse.getName() + ")",
                        WordWrap.wrapWithNewline(curse.getDescription(), 150),
                        ""
                );
            }
        }
        return itemBuilder.get();
    }

    public String getName() {
        String name = "";
        if (modifier != 0) {
            if (modifier > 0) {
                name += ChatColor.GREEN + "Blessed ";
            } else {
                name += ChatColor.RED + "Cursed ";
            }
        }
        name += tier.getColoredName() + " " + ChatColor.GRAY + getType().name;
        return name;
    }

    public List<String> getStatPoolLore() {
        List<String> lore = new ArrayList<>();
        statPool.keySet()
                .stream()
                .sorted(Comparator.comparingInt(Enum::ordinal))
                .forEachOrdered(stat -> lore.add(stat.getValueFormatted(statPool.get(stat))));
        return lore;
    }

    public abstract R[] getBlessings();

    public abstract U[] getCurses();

    public abstract ItemTypes getType();

    public float getItemScore() {
        List<Double> averageScores = getItemScoreAverageValues();
        double sum = 0;
        for (Double d : averageScores) {
            sum += d;
        }
        return Math.round(sum / averageScores.size() * 10000) / 100f;
    }

    public List<Double> getItemScoreAverageValues() {
        HashMap<T, ItemTier.StatRange> statRanges = getTierStatRanges();
        return statPool.entrySet()
                       .stream()
                       .map(statValue -> {
                           ItemTier.StatRange statRange = statRanges.get(statValue.getKey());
                           return getAverageValue(statRange.getMin(), statRange.getMax(), statValue.getValue());
                       })
                       .collect(Collectors.toList());
    }

    private String getItemScoreString() {
        return ChatColor.GRAY + "Score: " + ChatColor.YELLOW + NumberFormat.formatOptionalHundredths(getItemScore()) + ChatColor.GRAY + "/" + ChatColor.GREEN + "100";
    }

    public int getWeight() {
        float itemScore = getItemScore();
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

    private String getWeightString() {
        return ChatColor.GRAY + "Weight: " + ChatColor.YELLOW + NumberFormat.formatOptionalHundredths(getWeight());
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


}
