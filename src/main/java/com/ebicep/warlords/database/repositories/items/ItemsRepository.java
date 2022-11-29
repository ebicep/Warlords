package com.ebicep.warlords.database.repositories.items;

import com.ebicep.warlords.database.repositories.items.pojos.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemsRepository extends MongoRepository<Item, String> {

}
