package com.ebicep.warlords.database.repositories.player;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.mongodb.MongoNamespace;
import com.mongodb.client.model.RenameCollectionOptions;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CustomPlayerRepositoryImpl implements CustomPlayerRepository {

    final MongoTemplate mongoTemplate;

    public CustomPlayerRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public DatabasePlayer create(DatabasePlayer player, PlayersCollections collection) {
        return mongoTemplate.insert(player, collection.collectionName);
    }

    @Override
    public DatabasePlayer save(DatabasePlayer player, PlayersCollections collection) {
        return mongoTemplate.save(player, collection.collectionName);
    }

    @Override
    public void updateMany(Query query, UpdateDefinition update, Class<?> clazz, PlayersCollections collection) {
        mongoTemplate.updateMulti(query, update, clazz, collection.collectionName);
    }

    @Override
    public void delete(DatabasePlayer player, PlayersCollections collection) {
        mongoTemplate.remove(player, collection.collectionName);
    }

    @Override
    public void deleteAll(PlayersCollections collection) {
        mongoTemplate.dropCollection(collection.collectionName);
        mongoTemplate.createCollection(collection.collectionName);
        ensureLastLoginIndex(collection);
    }

    @Override
    public DatabasePlayer findOne(Query query, PlayersCollections collection) {
        return mongoTemplate.findOne(query, DatabasePlayer.class, collection.collectionName);
    }

    @Override
    public Optional<DatabasePlayer> findByUUID(UUID uuid, PlayersCollections collection) {
        return Optional.ofNullable(mongoTemplate.findOne(new Query()
                        .addCriteria(Criteria.where("uuid").is(uuid)),
                DatabasePlayer.class,
                collection.collectionName
        ));
    }

    @Override
    public List<DatabasePlayer> findAll(PlayersCollections collection) {
        return mongoTemplate.findAll(DatabasePlayer.class, collection.collectionName);
    }

    @Override
    public List<DatabasePlayer> find(Query query, PlayersCollections collection) {
        return mongoTemplate.find(query, DatabasePlayer.class, collection.collectionName);
    }

    @Override
    public BulkOperations bulkOps() {
        return mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, DatabasePlayer.class);
    }

    @Override
    public List<DatabasePlayer> getPlayersSorted(Aggregation aggregation, PlayersCollections collections) {
        return mongoTemplate.aggregate(aggregation,
                        collections.collectionName,
                        DatabasePlayer.class
                )
                .getMappedResults();
    }

    @Override
    public <T> T convertDocumentToClass(Document document, Class<T> clazz) {
        return mongoTemplate.getConverter().read(clazz, document);
    }

    @Override
    public void renameCollection(String collectionName, String newCollectionName, boolean dropTarget) {
        mongoTemplate.getCollection(collectionName).renameCollection(
                new MongoNamespace(DatabaseManager.warlordsDatabase.getName(), newCollectionName),
                new RenameCollectionOptions().dropTarget(dropTarget)
        );
    }

    @Override
    public void ensureLastLoginIndexes() {
        ChatUtils.MessageType.PLAYER_SERVICE.sendMessage("Ensuring last_login indexes on " + PlayersCollections.ACTIVE_COLLECTIONS.size() + " collections");
        for (PlayersCollections collection : PlayersCollections.ACTIVE_COLLECTIONS) {
            ensureLastLoginIndex(collection);
        }
        ChatUtils.MessageType.PLAYER_SERVICE.sendMessage("Finished ensuring last_login indexes");
    }

    private void ensureLastLoginIndex(PlayersCollections collection) {
        Index lastLoginIndex = new Index().on("last_login", Sort.Direction.ASC);
        try {
            String indexName = mongoTemplate.indexOps(collection.collectionName).ensureIndex(lastLoginIndex);
            ChatUtils.MessageType.PLAYER_SERVICE.sendMessage("Ensured last_login index on " + collection.collectionName + " (" + indexName + ")");
        } catch (Exception e) {
            ChatUtils.MessageType.PLAYER_SERVICE.sendErrorMessage("Failed to ensure last_login index on " + collection.collectionName);
            ChatUtils.MessageType.PLAYER_SERVICE.sendErrorMessage(e);
            throw e;
        }
    }
}
