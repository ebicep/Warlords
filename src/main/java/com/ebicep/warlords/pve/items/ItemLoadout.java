package com.ebicep.warlords.pve.items;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.items.addons.ItemAddonClassBonus;
import com.ebicep.warlords.pve.items.statpool.StatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.AbstractSpecialItem;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.items.types.ItemType;
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
        int weightModifier = 0;
        for (AbstractItem actualItem : actualItems) {
            weight += actualItem.getWeight();
            if (actualItem.getType() == ItemType.BUCKLER) {
                weightModifier += actualItem.getModifierCalculated();
            }
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

    public void applyToWarlordsPlayer(ItemsManager itemsManager, WarlordsPlayer warlordsPlayer) {
        Map<StatPool, Integer> statPoolValues = new HashMap<>();
        Map<StatPool, ItemTier> statPoolHighestTier = new HashMap<>();
        getActualItems(itemsManager).forEach(item -> {
            ItemTier tier = item.getTier();
            addStatPool(statPoolValues, statPoolHighestTier, item.getStatPool(), tier);
            if (item instanceof AbstractSpecialItem) {
                if (item instanceof ItemAddonClassBonus) {
                    if (((ItemAddonClassBonus) item).getClasses() != Specializations.getClass(warlordsPlayer.getSpecClass())) {
                        return;
                    }
                }
                addStatPool(statPoolValues, statPoolHighestTier, ((AbstractSpecialItem) item).getBonusStats(), tier);
            }
            if (item instanceof AppliesToWarlordsPlayer) {
                ((AppliesToWarlordsPlayer) item).applyToWarlordsPlayer(warlordsPlayer);
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
            Map<StatPool, Integer> statPoolValues,
            Map<StatPool, ItemTier> statPoolHighestTier,
            Map<T, Integer> statPool,
            ItemTier itemTier
    ) {
        statPool.forEach((stat, tier) -> {
            statPoolValues.merge(stat, tier, Integer::sum);
            if (statPoolHighestTier.get(stat) == null || statPoolHighestTier.get(stat).ordinal() < itemTier.ordinal()) {
                statPoolHighestTier.put(stat, itemTier);
            }
        });
    }

    public String getName() {
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

    public enum DifficultyMode {
        ANY("Any") {
            @Override
            public boolean validGameMode(GameMode gameMode) {
                return true;
            }
        },
        WAVE_DEFENSE("Wave Defense"),
        WAVE_DEFENSE_EASY(" - Easy") {
            @Override
            public boolean validDifficulty(DifficultyIndex difficultyIndex) {
                return difficultyIndex == DifficultyIndex.EASY;
            }

            @Override
            public String getShortName() {
                return "Easy";
            }
        },
        WAVE_DEFENSE_NORMAL(" - Normal") {
            @Override
            public boolean validDifficulty(DifficultyIndex difficultyIndex) {
                return difficultyIndex == DifficultyIndex.NORMAL;
            }

            @Override
            public String getShortName() {
                return "Normal";
            }
        },
        WAVE_DEFENSE_HARD(" - Hard") {
            @Override
            public boolean validDifficulty(DifficultyIndex difficultyIndex) {
                return difficultyIndex == DifficultyIndex.HARD;
            }

            @Override
            public String getShortName() {
                return "Hard";
            }
        },
        WAVE_DEFENSE_ENDLESS(" - Endless") {
            @Override
            public boolean validDifficulty(DifficultyIndex difficultyIndex) {
                return difficultyIndex == DifficultyIndex.ENDLESS;
            }

            @Override
            public String getShortName() {
                return "Endless";
            }
        },
        ONSLAUGHT("Onslaught") {
            @Override
            public boolean validGameMode(GameMode gameMode) {
                return gameMode == GameMode.ONSLAUGHT;
            }
        },
        EVENT("Event") {
            @Override
            public boolean validGameMode(GameMode gameMode) {
                return gameMode == GameMode.EVENT_WAVE_DEFENSE;
            }
        },

        ;

        public static final DifficultyMode[] VALUES = values();
        public final String name;

        DifficultyMode(String name) {
            this.name = name;
        }

        public String getShortName() {
            return name;
        }

        public DifficultyMode next() {
            return VALUES[(this.ordinal() + 1) % VALUES.length];
        }

        public boolean validGameMode(GameMode gameMode) {
            return gameMode == GameMode.WAVE_DEFENSE;
        }

        public boolean validDifficulty(DifficultyIndex difficultyIndex) {
            return true;
        }

    }
}
