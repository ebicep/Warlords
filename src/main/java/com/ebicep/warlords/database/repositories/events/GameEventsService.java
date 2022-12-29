package com.ebicep.warlords.database.repositories.events;

import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GameEventsService {

    void create(DatabaseGameEvent databaseGameEvent);

    void update(DatabaseGameEvent databaseGameEvent);

    void delete(DatabaseGameEvent databaseGameEvent);

    List<DatabaseGameEvent> findAll();

}
