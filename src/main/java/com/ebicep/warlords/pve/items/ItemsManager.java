package com.ebicep.warlords.pve.items;

import com.ebicep.warlords.pve.items.menu.util.ItemSearchMenu;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemsManager {

    @Field("item_inventory")
    private List<AbstractItem> itemInventory = new ArrayList<>();
    @Field("menu_settings")
    private ItemSearchMenu.PlayerItemMenuSettings.PlayerItemMenuFilterSettings menuFilterSettings = new ItemSearchMenu.PlayerItemMenuSettings.PlayerItemMenuFilterSettings();
    private List<ItemLoadout> loadouts = new ArrayList<>() {{
        add(new ItemLoadout("Default"));
    }};
    @Field("blessings_found")
    private int blessingsFound;
    @Field("blessings_bought")
    private Map<Integer, Integer> blessingsBought = new HashMap<>();

    public ItemsManager() {
    }

    public List<AbstractItem> getItemInventory() {
        return itemInventory;
    }

    public void addItem(AbstractItem item) {
        this.itemInventory.add(item);
    }

    public void removeItem(AbstractItem item) {
        this.itemInventory.remove(item);
    }

    public ItemSearchMenu.PlayerItemMenuSettings.PlayerItemMenuFilterSettings getMenuFilterSettings() {
        return menuFilterSettings;
    }

    public void setMenuFilterSettings(ItemSearchMenu.PlayerItemMenuSettings.PlayerItemMenuFilterSettings menuFilterSettings) {
        this.menuFilterSettings = menuFilterSettings;
    }

    public List<ItemLoadout> getLoadouts() {
        return loadouts;
    }

    public int getBlessingsFound() {
        return blessingsFound;
    }

    public void addBlessingsFound(int amount) {
        this.blessingsFound += amount;
    }

    public void subtractBlessingsFound(int amount) {
        this.blessingsFound -= amount;
    }

    public Map<Integer, Integer> getBlessingsBought() {
        return blessingsBought;
    }

    public Integer getBlessingBoughtAmount(int tier) {
        return blessingsBought.getOrDefault(tier, 0);
    }

    public void addBlessingBought(int tier) {
        blessingsBought.merge(tier, 1, Integer::sum);
    }

    public void subtractBlessingBought(int tier) {
        blessingsBought.merge(tier, -1, Integer::sum);
    }

    public void setBlessingsFound(int blessingsFound) {
        this.blessingsFound = blessingsFound;
    }

}