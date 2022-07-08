package com.ebicep.warlords.database.repositories.timings;

import com.ebicep.warlords.database.repositories.timings.pojos.DatabaseTiming;
import org.springframework.stereotype.Service;

@Service
public interface TimingsService {

    void create(DatabaseTiming databaseTiming);

    void update(DatabaseTiming databaseTiming);

    void delete(DatabaseTiming databaseTiming);

    DatabaseTiming findByTitle(String title);


}
