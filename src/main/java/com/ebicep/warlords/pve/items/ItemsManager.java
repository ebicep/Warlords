package com.ebicep.warlords.pve.items;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemsManager {

    public static int getMaxWeight(DatabasePlayer databasePlayer, Specializations selectedSpec) {
        int weight = 20;
        for (Specializations spec : Specializations.VALUES) {
            int prestige = databasePlayer.getSpec(spec).getPrestige();
            if (selectedSpec == spec) {
                weight += 2 * prestige;
            } else {
                weight += prestige;
            }
        }
        return Math.min(weight, 40);
    }

    @Field("item_inventory")
    private List<AbstractItem<?, ?, ?>> itemInventory = new ArrayList<>();
    private List<ItemLoadout> loadouts = new ArrayList<>() {{
        add(new ItemLoadout("Default"));
    }};
    private Map<Integer, Integer> blessings = new HashMap<>();

    public ItemsManager() {
    }

    public List<AbstractItem<?, ?, ?>> getItemInventory() {
        return itemInventory;
    }

    public void addItem(AbstractItem<?, ?, ?> item) {
        this.itemInventory.add(item);
    }

    public List<ItemLoadout> getLoadouts() {
        return loadouts;
    }

    public Integer getBlessingAmount(int tier) {
        return blessings.getOrDefault(tier, 0);
    }

    public void addBlessing(int tier) {
        blessings.merge(tier, 1, Integer::sum);
    }
}