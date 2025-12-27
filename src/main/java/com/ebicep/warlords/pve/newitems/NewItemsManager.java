package com.ebicep.warlords.pve.newitems;

import org.springframework.data.annotation.TypeAlias;

import java.util.ArrayList;
import java.util.List;

@TypeAlias("new_items_manager")
public class NewItemsManager {

    private List<NewItem> itemInventory = new ArrayList<>();
    private List<NewItemLoadout> loadouts = new ArrayList<>() {{
        add(new NewItemLoadout("Default"));
    }};

    public List<NewItem> getItemInventory() {
        return itemInventory;
    }

    public List<NewItemLoadout> getLoadouts() {
        return loadouts;
    }

}
