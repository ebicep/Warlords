package com.ebicep.warlords.database.repositories.items;

import com.ebicep.warlords.database.repositories.items.pojos.Item;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ItemService {

    Item create(Item item);

    List<Item> findAll();

}
