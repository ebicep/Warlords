package com.ebicep.warlords.database.repositories.player;


import com.ebicep.warlords.database.repositories.player.pojos.DatabasePlayer;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomPlayerRepository {

    void create(DatabasePlayer player, String collection);

    void save(DatabasePlayer player, String collection);

    DatabasePlayer findOne(Query query, String collection);

    BulkOperations bulkOps();

    List<DatabasePlayer> getPlayersSorted(Aggregation aggregation, PlayersCollections collections);

}
