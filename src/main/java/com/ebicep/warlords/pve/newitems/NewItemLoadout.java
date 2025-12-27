package com.ebicep.warlords.pve.newitems;

import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyMode;
import com.ebicep.warlords.pve.newitems.attributes.NewItemAttribute;
import org.springframework.data.annotation.TypeAlias;

import java.time.Instant;
import java.util.*;

@TypeAlias("new_item_loadout")
public class NewItemLoadout {

    private Instant creationDate = Instant.now();
    private String name;
    private List<UUID> items = new ArrayList<>();
    private DifficultyMode difficultyMode = DifficultyMode.ANY;
    private Specializations spec;

    public NewItemLoadout(String name) {
        this.name = name;
    }

    public void apply(NewItemsManager itemsManager, WarlordsPlayer warlordsPlayer) {
        List<NewItem> itemList = getActualItems(itemsManager);
        Map<NewItemAttribute, Float> attributeValues = new HashMap<>();
        for (NewItem item : itemList) {
            Map<NewItemAttribute, Float> allAttributeValues = item.getAllAttributeValues();
            for (Map.Entry<NewItemAttribute, Float> entry : allAttributeValues.entrySet()) {
                attributeValues.merge(entry.getKey(), entry.getValue(), Float::sum);
            }
        }
        attributeValues.forEach((attribute, value) -> {
            attribute.apply(warlordsPlayer, value);
        });
        warlordsPlayer.updateInventory(false);
    }

    public List<NewItem> getActualItems(NewItemsManager itemsManager) {
        Set<UUID> items = Set.copyOf(this.items);
        List<NewItem> actualItems = new ArrayList<>();
        for (NewItem item : itemsManager.getItemInventory()) {
            if (items.contains(item.getUUID())) {
                actualItems.add(item);
            }
        }
        return actualItems;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public String getName() {
        return name;
    }

    public List<UUID> getItems() {
        return items;
    }

    public DifficultyMode getDifficultyMode() {
        return difficultyMode;
    }

    public Specializations getSpec() {
        return spec;
    }

}
