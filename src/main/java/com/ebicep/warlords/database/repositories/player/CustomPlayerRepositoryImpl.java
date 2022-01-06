package com.ebicep.warlords.database.repositories.player;

import com.ebicep.warlords.database.repositories.player.pojos.ctf.DatabasePlayerCTF;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.general.classescomppub.DatabaseMage;
import com.ebicep.warlords.database.repositories.player.pojos.general.classescomppub.DatabasePaladin;
import com.ebicep.warlords.database.repositories.player.pojos.general.classescomppub.DatabaseShaman;
import com.ebicep.warlords.database.repositories.player.pojos.general.classescomppub.DatabaseWarrior;
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
    public DatabasePlayerCTF convertDocumentToPlayer(Document document) {
        return mongoTemplate.getConverter().read(DatabasePlayerCTF.class, document);
    }

    @Override
    public DatabaseMage convertDocumentToMage(Document document) {
        return mongoTemplate.getConverter().read(DatabaseMage.class, document);
    }

    @Override
    public DatabaseWarrior convertDocumentToWarrior(Document document) {
        return mongoTemplate.getConverter().read(DatabaseWarrior.class, document);
    }

    @Override
    public DatabasePaladin convertDocumentToPaladin(Document document) {
        return mongoTemplate.getConverter().read(DatabasePaladin.class, document);
    }

    @Override
    public DatabaseShaman convertDocumentToShaman(Document document) {
        return mongoTemplate.getConverter().read(DatabaseShaman.class, document);
    }

}
