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

import java.util.Comparator;
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
        ensureIndexes(collection);
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
    public void ensureIndexes() {
        ChatUtils.MessageType.PLAYER_SERVICE.sendMessage("Ensuring player indexes on " + PlayersCollections.ACTIVE_COLLECTIONS.size() + " collections");
        for (PlayersCollections collection : PlayersCollections.ACTIVE_COLLECTIONS) {
            ensureIndexes(collection);
        }
        ChatUtils.MessageType.PLAYER_SERVICE.sendMessage("Finished ensuring player indexes");
    }

    private void ensureIndexes(PlayersCollections collection) {
        ensureLastLoginIndex(collection);
        ensureUuidUniqueIndex(collection);
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

    private void ensureUuidUniqueIndex(PlayersCollections collection) {
        Index uuidIndex = new Index().on("uuid", Sort.Direction.ASC).unique();
        try {
            String indexName = mongoTemplate.indexOps(collection.collectionName).ensureIndex(uuidIndex);
            ChatUtils.MessageType.PLAYER_SERVICE.sendMessage("Ensured unique uuid index on " + collection.collectionName + " (" + indexName + ")");
        } catch (Exception e) {
            ChatUtils.MessageType.PLAYER_SERVICE.sendMessage("Unique uuid index failed on " + collection.collectionName + ", deduping then retrying");
            ChatUtils.MessageType.PLAYER_SERVICE.sendErrorMessage(e);
            removeDuplicateUuids(collection);
            try {
                String indexName = mongoTemplate.indexOps(collection.collectionName).ensureIndex(uuidIndex);
                ChatUtils.MessageType.PLAYER_SERVICE.sendMessage("Ensured unique uuid index on " + collection.collectionName + " after dedupe (" + indexName + ")");
            } catch (Exception retry) {
                ChatUtils.MessageType.PLAYER_SERVICE.sendErrorMessage("Failed to ensure unique uuid index on " + collection.collectionName);
                ChatUtils.MessageType.PLAYER_SERVICE.sendErrorMessage(retry);
                throw retry;
            }
        }
    }

    private void removeDuplicateUuids(PlayersCollections collection) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("uuid").count().as("count"),
                Aggregation.match(Criteria.where("count").gt(1))
        );
        List<Document> duplicateGroups = mongoTemplate.aggregate(aggregation, collection.collectionName, Document.class)
                .getMappedResults();
        int removed = 0;
        for (Document group : duplicateGroups) {
            Object rawUuid = group.get("_id");
            if (rawUuid == null) {
                continue;
            }
            UUID uuid = rawUuid instanceof UUID u ? u : UUID.fromString(rawUuid.toString());
            List<DatabasePlayer> players = mongoTemplate.find(
                    new Query(Criteria.where("uuid").is(uuid)),
                    DatabasePlayer.class,
                    collection.collectionName
            );
            if (players.size() < 2) {
                continue;
            }
            DatabasePlayer keep = players.stream()
                    .max(Comparator
                            .comparing(DatabasePlayer::getLastLogin, Comparator.nullsFirst(Comparator.naturalOrder()))
                            .thenComparingLong(DatabasePlayer::getExperience))
                    .orElse(players.get(0));
            for (DatabasePlayer player : players) {
                if (player == keep || (keep.getId() != null && keep.getId().equals(player.getId()))) {
                    continue;
                }
                mongoTemplate.remove(player, collection.collectionName);
                removed++;
            }
        }
        ChatUtils.MessageType.PLAYER_SERVICE.sendMessage("Removed " + removed + " duplicate uuid documents from " + collection.collectionName);
    }
}
