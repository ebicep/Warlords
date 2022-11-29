package com.ebicep.warlords.database.repositories.items;


import com.ebicep.warlords.database.repositories.items.pojos.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("itemService")
public class ItemServiceImpl implements ItemService {

    @Autowired
    ItemsRepository itemsRepository;

    @Override
    public Item create(Item item) {
        return itemsRepository.save(item);
    }

    @Override
    public List<Item> findAll() {
        return itemsRepository.findAll();
    }

}
