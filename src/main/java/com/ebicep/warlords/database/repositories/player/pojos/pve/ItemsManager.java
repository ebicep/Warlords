package com.ebicep.warlords.database.repositories.player.pojos.pve;

import com.ebicep.warlords.database.repositories.items.pojos.ItemEntry;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.items.Items;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

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
    private List<ItemEntry> itemInventory = new ArrayList<>();
    private List<ItemLoadout> loadouts = new ArrayList<>() {{
        add(new ItemLoadout("Default"));
    }};

    public ItemsManager() {
    }

    public List<ItemEntry> getItemInventory() {
        return itemInventory;
    }

    public void addItem(Items items) {
        this.itemInventory.add(new ItemEntry(items));
    }

    public List<ItemLoadout> getLoadouts() {
        return loadouts;
    }

}