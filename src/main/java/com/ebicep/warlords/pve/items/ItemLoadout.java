package com.ebicep.warlords.pve.items;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyMode;
import com.ebicep.warlords.pve.items.addons.ItemAddonClassBonus;
import com.ebicep.warlords.pve.items.statpool.StatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.items.types.BonusStats;
import com.ebicep.warlords.pve.items.types.ItemType;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega.HandsOfTheHolyCorpse;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.*;

public class ItemLoadout {

    private String name;
    @Field("creation_date")
    private Instant creationDate = Instant.now();
    private List<UUID> items = new ArrayList<>();
    private DifficultyMode difficultyMode = DifficultyMode.ANY;
    private Specializations spec;


    public ItemLoadout(String name) {
        this.name = name;
    }

    /**
     * @param itemsManager The items manager to get the items from
     * @return Pair(Weight, Modifier undivided by 100)
     */
    public int getWeight(ItemsManager itemsManager) {
        int weight = 0;

        List<AbstractItem> actualItems = getActualItems(itemsManager);
        List<Integer> itemWeights = new ArrayList<>();
        int weightModifier = 0;
        boolean hasHandsOfTheHolyCorpse = false;
        for (AbstractItem actualItem : actualItems) {
            weight += actualItem.getWeight();
            itemWeights.add(actualItem.getWeight());
            if (actualItem.getType() == ItemType.BUCKLER) {
                weightModifier += actualItem.getModifierCalculated();
            }
            if (actualItem instanceof HandsOfTheHolyCorpse) {
                hasHandsOfTheHolyCorpse = true;
            }
        }
        if (hasHandsOfTheHolyCorpse && itemWeights.size() > 1) {
            itemWeights.sort(Comparator.reverseOrder());
            weight -= itemWeights.get(1);
        }

        return (int) (weight * (1 - weightModifier / 100f));
    }

    public List<AbstractItem> getActualItems(ItemsManager itemsManager) {
        List<AbstractItem> items = new ArrayList<>();
        for (AbstractItem item : itemsManager.getItemInventory()) {
            if (this.items.contains(item.getUUID())) {
                items.add(item);
            }
        }
        return items;
    }

    public void applyToWarlordsPlayer(ItemsManager itemsManager, WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        Map<StatPool, Float> statPoolValues = new HashMap<>();
        Map<StatPool, ItemTier> statPoolHighestTier = new HashMap<>();
        HashSet<Class<?>> appliedClasses = new HashSet<>();
        getActualItems(itemsManager).forEach(item -> {
            ItemTier tier = item.getTier();
            addStatPool(statPoolValues, statPoolHighestTier, item.getStatPool(), tier);
            if (item instanceof ItemAddonClassBonus itemAddonClassBonus) {
                if (itemAddonClassBonus.getClasses() != Specializations.getClass(warlordsPlayer.getSpecClass())) {
                    return;
                }
            }
            if (item instanceof BonusStats bonusStats) {
                addStatPool(statPoolValues, statPoolHighestTier, bonusStats.getBonusStats(), tier);
            }
            if (item instanceof AppliesToWarlordsPlayer appliesToWarlordsPlayer && !appliedClasses.contains(item.getClass())) {
                appliedClasses.add(item.getClass());
                appliesToWarlordsPlayer.applyToWarlordsPlayer(warlordsPlayer, pveOption);
            }
        });

        // Applying stats
        statPoolValues.forEach((stat, value) -> stat.applyToWarlordsPlayer(warlordsPlayer,
                (float) value / stat.getDecimalPlace().value,
                statPoolHighestTier.get(stat)
        ));
        for (AbstractAbility ability : warlordsPlayer.getSpec().getAbilities()) {
            statPoolValues.forEach((stat, value) -> stat.applyToAbility(ability, (float) value / stat.getDecimalPlace().value, statPoolHighestTier.get(stat)));
        }

        warlordsPlayer.updateInventory(false);
    }

    private static <T extends StatPool> void addStatPool(
            Map<StatPool, Float> statPoolValues,
            Map<StatPool, ItemTier> statPoolHighestTier,
            Map<T, Float> statPool,
            ItemTier itemTier
    ) {
        statPool.forEach((stat, tier) -> {
            statPoolValues.merge(stat, tier, Float::sum);
            if (statPoolHighestTier.get(stat) == null || statPoolHighestTier.get(stat).ordinal() < itemTier.ordinal()) {
                statPoolHighestTier.put(stat, itemTier);
            }
        });
    }

    public String getName() {
        if (name == null) {
            name = "bugged";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public List<UUID> getItems() {
        return items;
    }

    public DifficultyMode getDifficultyMode() {
        return difficultyMode;
    }

    public void setDifficultyMode(DifficultyMode difficultyMode) {
        this.difficultyMode = difficultyMode;
    }

    public Specializations getSpec() {
        return spec;
    }

    public void setSpec(Specializations spec) {
        this.spec = spec;
    }

}
