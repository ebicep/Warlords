package com.ebicep.warlords.database.repositories.player;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class CustomPlayerRepositoryImpl implements CustomPlayerRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public void create(DatabasePlayer player, PlayersCollections collection) {
        mongoTemplate.insert(player, collection.collectionName);
    }

    @Override
    public void save(DatabasePlayer player, PlayersCollections collection) {
        mongoTemplate.save(player, collection.collectionName);
    }

    @Override
    public void delete(DatabasePlayer player, PlayersCollections collection) {
        mongoTemplate.remove(player, collection.collectionName);
    }

    @Override
    public void deleteAll(PlayersCollections collection) {
        mongoTemplate.dropCollection(collection.collectionName);
        mongoTemplate.createCollection(collection.collectionName);
    }

    @Override
    public DatabasePlayer findOne(Query query, PlayersCollections collection) {
        return mongoTemplate.findOne(query, DatabasePlayer.class, collection.collectionName);
    }

    @Override
    public DatabasePlayer findByUUID(UUID uuid, PlayersCollections collection) {
        return mongoTemplate.findOne(new Query().addCriteria(Criteria.where("uuid").is(uuid.toString())), DatabasePlayer.class, collection.collectionName);
    }

    @Override
    public List<DatabasePlayer> findAll(PlayersCollections collection) {
        return mongoTemplate.findAll(DatabasePlayer.class, collection.collectionName);
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

    @Override
    public <T> T convertDocumentToClass(Document document, Class<T> clazz) {
        return mongoTemplate.getConverter().read(clazz, document);
    }

}
