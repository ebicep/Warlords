package com.ebicep.warlords.database.repositories.timings;

import com.ebicep.warlords.database.repositories.timings.pojos.DatabaseTiming;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TimingsRepository extends MongoRepository<DatabaseTiming, String> {

    @Query("{title:'?0'}")
    DatabaseTiming findByTitle(String title);

}
