package com.ebicep.warlords.pve.items;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.items.statpool.ItemStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.ItemBuckler;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ItemLoadout {

    private String name;
    @Field("creation_date")
    private Instant creationDate = Instant.now();
    private List<UUID> items = new ArrayList<>();
    private DifficultyIndex difficulty;
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

        List<AbstractItem<?, ?, ?>> actualItems = getActualItems(itemsManager);
        int weightModifier = 0;
        for (AbstractItem<?, ?, ?> actualItem : actualItems) {
            weight += actualItem.getWeight();
            if (actualItem instanceof ItemBuckler) {
                weightModifier += actualItem.getModifierCalculated();
            }
        }

        return (int) (weight * (1 - weightModifier / 100f));
    }

    public List<AbstractItem<?, ?, ?>> getActualItems(ItemsManager itemsManager) {
        List<AbstractItem<?, ?, ?>> items = new ArrayList<>();
        for (AbstractItem<?, ?, ?> item : itemsManager.getItemInventory()) {
            if (this.items.contains(item.getUUID())) {
                items.add(item);
            }
        }
        return items;
    }

    public void applyToWarlordsPlayer(ItemsManager itemsManager, WarlordsPlayer warlordsPlayer) {
        HashMap<ItemStatPool<?>, Integer> statPoolValues = new HashMap<>();
        getActualItems(itemsManager).forEach(item -> item.getStatPool().forEach((stat, tier) -> statPoolValues.merge(stat, tier, Integer::sum)));

        statPoolValues.forEach((stat, tier) -> stat.applyToWarlordsPlayer(warlordsPlayer, (float) tier / stat.getDecimalPlace().value));
        for (AbstractAbility ability : warlordsPlayer.getSpec().getAbilities()) {
            statPoolValues.forEach((stat, tier) -> stat.applyToAbility(ability, (float) tier / stat.getDecimalPlace().value));
        }
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

    public DifficultyIndex getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(DifficultyIndex difficulty) {
        this.difficulty = difficulty;
    }

    public Specializations getSpec() {
        return spec;
    }

    public void setSpec(Specializations spec) {
        this.spec = spec;
    }
}
