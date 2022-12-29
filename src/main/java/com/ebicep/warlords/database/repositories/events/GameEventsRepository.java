package com.ebicep.warlords.database.repositories.events;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GameEventsRepository extends MongoRepository<DatabaseGameEvent, String> {

    @Query("{title:'?0'}")
    DatabaseGameEvent findByTitle(String title);

}
