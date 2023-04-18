package com.ebicep.warlords.database.repositories.items;

import com.ebicep.warlords.database.repositories.items.pojos.WeeklyBlessings;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeeklyBlessingsRepository extends MongoRepository<WeeklyBlessings, String> {

}
