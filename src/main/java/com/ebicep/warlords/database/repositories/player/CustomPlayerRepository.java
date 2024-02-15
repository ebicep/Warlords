package com.ebicep.warlords.database.repositories.player;


import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import org.bson.Document;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomPlayerRepository {

    DatabasePlayer create(DatabasePlayer player, PlayersCollections collection);

    DatabasePlayer save(DatabasePlayer player, PlayersCollections collection);

    void updateMany(Query query, UpdateDefinition update, Class<?> clazz, PlayersCollections collection);

    void delete(DatabasePlayer player, PlayersCollections collection);

    void deleteAll(PlayersCollections collection);

    DatabasePlayer findOne(Query query, PlayersCollections collection);

    DatabasePlayer findByUUID(UUID uuid, PlayersCollections collection);

    List<DatabasePlayer> findAll(PlayersCollections collection);

    List<DatabasePlayer> find(Query query, PlayersCollections collection);

    BulkOperations bulkOps();

    List<DatabasePlayer> getPlayersSorted(Aggregation aggregation, PlayersCollections collections);

    <T> T convertDocumentToClass(Document document, Class<T> clazz);

    void renameCollection(String collectionName, String newCollectionName, boolean dropTarget);

}
