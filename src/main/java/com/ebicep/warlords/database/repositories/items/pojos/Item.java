package com.ebicep.warlords.database.repositories.items.pojos;

import com.ebicep.warlords.pve.items.Items;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "Items")
public class Item {

    @Id
    protected String id;

    //@Indexed(unique = true)
    private Items item;
    private String name = "TODO";
    private int weight = 0;
    private String description = "TODO";

    public Item() {
    }

    public Item(Items item) {
        this.item = item;
    }

    public void importToItem() {
        item.setName(name);
        item.setWeight(weight);
        item.setDescription(description);
    }

    public Items getItem() {
        return item;
    }
}
