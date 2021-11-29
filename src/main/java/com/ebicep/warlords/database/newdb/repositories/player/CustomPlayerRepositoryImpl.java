package com.ebicep.warlords.database.newdb.repositories.player;

import com.ebicep.warlords.database.newdb.repositories.player.pojos.DatabasePlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomPlayerRepositoryImpl implements CustomPlayerRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void create(DatabasePlayer player, String collection) {
        mongoTemplate.insert(player, collection);
    }

    @Override
    public void save(DatabasePlayer player, String collection) {
        mongoTemplate.save(player, collection);
    }

    @Override
    public DatabasePlayer findOne(Query query, String collection) {
        return mongoTemplate.findOne(query, DatabasePlayer.class, collection);
    }

    @Override
    public BulkOperations bulkOps() {
        return mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, DatabasePlayer.class);
    }

    @Override
    public List<DatabasePlayer> getPlayersSorted(Aggregation aggregation, PlayersCollections collections) {
        return mongoTemplate.aggregate(aggregation,
                collections.collectionName,
                DatabasePlayer.class)
                .getMappedResults();
    }

}
