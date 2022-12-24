package com.ebicep.warlords.database.repositories.events;


import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("gameEventsService")
public class GameEventsServiceImpl implements GameEventsService {

    @Autowired
    GameEventsRepository gameEventsRepository;


    @Override
    public void create(DatabaseGameEvent databaseGameEvent) {
        DatabaseGameEvent event = gameEventsRepository.insert(databaseGameEvent);
        ChatUtils.MessageTypes.EVENTS.sendMessage("Created: - " + event);
    }

    @Override
    public void update(DatabaseGameEvent databaseGameEvent) {
        DatabaseGameEvent event = gameEventsRepository.save(databaseGameEvent);
        ChatUtils.MessageTypes.EVENTS.sendMessage("Updated: - " + event);
    }

    @Override
    public void delete(DatabaseGameEvent databaseGameEvent) {
        gameEventsRepository.delete(databaseGameEvent);
        ChatUtils.MessageTypes.EVENTS.sendMessage("Deleted: - " + databaseGameEvent);
    }

    @Override
    public List<DatabaseGameEvent> findAll() {
        return gameEventsRepository.findAll();
    }

}
