package com.ebicep.warlords.database.repositories.items.pojos;

import com.ebicep.warlords.player.general.WeaponsRarity;
import com.ebicep.warlords.pve.items.ItemAttribute;
import com.ebicep.warlords.pve.items.ItemFamily;
import com.ebicep.warlords.pve.items.Items;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection = "Items")
public class Item {

    @Id
    protected String id;

    //@Indexed(unique = true)
    private Items item;
    private String name = "NAME_PLACEHOLDER";
    private int weight = 100;
    private ItemAttribute attribute = ItemAttribute.ALPHA;
    private ItemFamily family = ItemFamily.SPEED_BASIC;
    private WeaponsRarity rarity = WeaponsRarity.COMMON;
    private String description = "DESCRIPTION_PLACEHOLDER";
    @Field("skull_id")
    private String skullID = "009a87cf-a01b-4f26-ba2b-75dbee0cea98";
    @Field("skull_texture_id")
    private String skullTextureID = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZThmNWRjOTI3OTIzMjc5MTI3YTlkMmFkZTg2NDMyZjk4Nzc2MDljYjlmODM4NTRhNWI4OTJiZjdjYWQ5ZGYyZiJ9fX0";

    public Item() {
    }

    public Item(Items item) {
        this.item = item;
    }

    public void importToItem() {
        item.setName(name);
        item.setWeight(weight);
        item.setAttribute(attribute);
        item.setFamily(family);
        item.setRarity(rarity);
        item.setDescription(description);
        item.setSkullID(skullID);
        item.setSkullTextureID(skullTextureID);
    }

    public Items getItem() {
        return item;
    }
}
