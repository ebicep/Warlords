package com.ebicep.warlords.database.repositories.games;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import org.bson.Document;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameService {

    boolean exists(DatabaseGameBase game, GamesCollections collections);

    void create(DatabaseGameBase game);

    void create(DatabaseGameBase game, GamesCollections collection);

    void save(DatabaseGameBase game, GamesCollections collection);

    void delete(DatabaseGameBase game, GamesCollections collection);

    DatabaseGameBase findOne(Query query, GamesCollections collection);

    List<DatabaseGameBase> findAll(GamesCollections collection);

    BulkOperations bulkOps();

    <T> T convertDocumentToClass(Document document, Class<T> clazz);

    List<? extends DatabaseGameBase> getLastGames(int amount);

}
