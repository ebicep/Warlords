package com.ebicep.warlords.database.repositories.player;

import com.ebicep.warlords.database.repositories.player.pojos.DatabasePlayer;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface PlayerService {

    void create(DatabasePlayer player);

    void create(DatabasePlayer player, PlayersCollections collection);

    void update(DatabasePlayer player);

    void update(DatabasePlayer player, PlayersCollections collection);

    void delete(DatabasePlayer player);

    void deleteAll();

    void deleteAll(PlayersCollections collection);

    DatabasePlayer findOne(Criteria criteria, PlayersCollections collection);

    DatabasePlayer findByUUID(UUID uuid);

    List<DatabasePlayer> findAll();

    BulkOperations bulkOps();

    List<DatabasePlayer> getPlayersSortedByPlays();

    List<DatabasePlayer> getPlayersSorted(Aggregation aggregation, PlayersCollections collections);
}
