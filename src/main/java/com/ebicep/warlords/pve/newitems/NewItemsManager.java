package com.ebicep.warlords.pve.newitems;

import com.ebicep.warlords.pve.newitems.menu.NewItemSearchMenu;
import org.springframework.data.annotation.TypeAlias;

import java.util.ArrayList;
import java.util.List;

@TypeAlias("new_items_manager")
public class NewItemsManager {

    private List<NewItem> itemInventory = new ArrayList<>();
    private List<NewItemLoadout> loadouts = new ArrayList<>() {{
        add(new NewItemLoadout("Default"));
    }};
    private NewItemSearchMenu.PlayerItemMenuSettings.PlayerItemMenuFilterSettings menuFilterSettings = new NewItemSearchMenu.PlayerItemMenuSettings.PlayerItemMenuFilterSettings();

    public void addItem(NewItem item) {
        this.itemInventory.add(item);
    }

    public void removeItem(NewItem item) {
        this.loadouts.forEach(loadout -> loadout.getItems().removeIf(uuid -> uuid.equals(item.getUUID())));
        this.itemInventory.remove(item);
    }

    public NewItemSearchMenu.PlayerItemMenuSettings.PlayerItemMenuFilterSettings getMenuFilterSettings() {
        return menuFilterSettings;
    }

    public void setMenuFilterSettings(NewItemSearchMenu.PlayerItemMenuSettings.PlayerItemMenuFilterSettings menuFilterSettings) {
        this.menuFilterSettings = menuFilterSettings;
    }

    public List<NewItem> getItemInventory() {
        return itemInventory;
    }

    public List<NewItemLoadout> getLoadouts() {
        return loadouts;
    }

}
